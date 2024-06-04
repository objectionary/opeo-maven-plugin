/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package it;

import com.jcabi.log.Logger;
import com.jcabi.xml.XMLDocument;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.xmir.XmlBytecodeEntry;
import org.eolang.jeo.representation.xmir.XmlInstruction;
import org.eolang.jeo.representation.xmir.XmlMethod;
import org.eolang.jeo.representation.xmir.XmlOperand;
import org.eolang.jeo.representation.xmir.XmlProgram;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * This isn't an integration test in the classical sense.
 * But rather a detective that helps to find out what's wrong with the code.
 * The idea is simple.
 * We have two folders - the first folder is the golden folder with the correct files.
 * The second folder is the real folder with the files that we want to check.
 * <p>
 * The test will compare the files from the golden folder with the files from the real folder
 * and will print out the differences and find errors.
 * <p>
 * Please, remember to set the correct paths to the folders before running the test.
 * @since 0.2
 */
final class Detective {

    /**
     * This test is used to find the problem in the transformation from JEO to OPEO.
     * Remember to set the correct folders.
     * @param etalon The path to the golden files.
     * @param target The path to the real files.
     */
    @Test
    @Disabled
    @ParameterizedTest
    @CsvSource(
        "./target/it/spring-fat/target/generated-sources/jeo-disassemble-xmir, ./target/it/spring-fat/target/generated-sources/opeo-compile-xmir"
    )
    void findTheProblem(final String etalon, final String target) {
        final Path golden = Paths.get(etalon);
        final Path real = Paths.get(target);
        final Results results = new Results();
        try (Stream<Path> all = Files.walk(golden).filter(Files::isRegularFile).parallel()) {
            all.map(path -> new XmirPair(golden, real, path, results)).forEach(XmirPair::compare);
        } catch (final IOException exception) {
            throw new IllegalStateException("Can't walk through the files", exception);
        }
        results.logSummary();
    }

    private static class Results {
        private final AtomicInteger total;
        private final AtomicInteger errors;
        private final List<String> logs;

        private Results() {
            this.total = new AtomicInteger(0);
            this.errors = new AtomicInteger(0);
            this.logs = new CopyOnWriteArrayList<>();
        }

        private void oneMore() {
            this.total.incrementAndGet();
        }

        private void problem(final String message) {
            this.errors.incrementAndGet();
            this.logs.add(message);
        }

        private void logCounters() {
            Logger.info(
                this,
                "Total files scanned:%d, Total errors:%d%n",
                this.total.get(),
                this.errors.get()
            );
        }

        private void logSummary() {
            this.logCounters();
            if (this.logs.isEmpty()) {
                Logger.info(this, "No errors found.");
            } else {
                Logger.info(this, "All Found Errors:\n");
                this.logs.forEach(entry -> Logger.info(this, entry));
            }
        }
    }

    private static final class XmirPair {

        private final Path golden;
        private final Path real;
        private final Path current;
        private final Results results;

        public XmirPair(
            final Path golden, final Path real, final Path current, final Results results
        ) {
            this.golden = golden;
            this.real = real;
            this.current = current;
            this.results = results;
        }

        void compare() {
            try {
                final Path relative = this.golden.relativize(this.current);
                final Xmir gold = new Xmir(this.golden.resolve(relative), this.results);
                gold.compare(new Xmir(this.real.resolve(relative), this.results));
            } catch (final CompareException swallow) {
                this.results.problem(swallow.getMessage());
            }
        }
    }


    private static final class Xmir {

        private final Path path;
        private final Results results;

        public Xmir(
            final Path path,
            final Results results
        ) {
            this.path = path;
            this.results = results;
        }

        private XMLDocument xml(final Path path) {
            try {
                return new XMLDocument(path);
            } catch (final FileNotFoundException exception) {
                throw new IllegalArgumentException(
                    String.format("File not found: %s", path),
                    exception
                );
            }
        }

        void compare(final Xmir xmir) throws CompareException {
            final Path goldenPath = path;
            final Path realPath = xmir.path;
            try {
                this.results.oneMore();
                final List<XmlMethod> gmethods = new XmlProgram(
                    this.xml(goldenPath)
                ).top().methods();
                final List<XmlMethod> bmethods = new XmlProgram(
                    this.xml(realPath)
                ).top().methods();
                final int size = gmethods.size();
                for (int index = 0; index < size; ++index) {
                    new Method(gmethods.get(index)).compare(new Method(bmethods.get(index)));
                }
                this.results.logCounters();
            } catch (final CompareException exception) {
                throw new CompareException(
                    String.format(
                        "%nFiles are different: %n%s%n%s%n%s",
                        goldenPath,
                        realPath,
                        exception.getMessage()
                    ),
                    exception
                );
            }

        }
    }

    private static final class Method {

        private final XmlMethod method;

        public Method(final XmlMethod method) {
            this.method = method;
        }

        void compare(final Method method) throws CompareException {
            final List<Instruction> ginstrs = this.method.instructions()
                .stream()
                .map(Instruction::new)
                .collect(Collectors.toList());
            final List<Instruction> binstrs = method.method.instructions()
                .stream()
                .map(Instruction::new)
                .collect(Collectors.toList());
            final int isize = ginstrs.size();
            for (int jindex = 0; jindex < isize; ++jindex) {
                final Instruction gentry = ginstrs.get(jindex);
                final Instruction bentry = binstrs.get(jindex);
                gentry.compare(bentry);
            }
        }
    }

    /**
     * Instruction to compare.
     * @since 0.2
     */
    private static final class Instruction {

        /**
         * Xmir representation of an instruction.
         */
        private final XmlBytecodeEntry entry;

        /**
         * Constructor.
         * @param entry Xmir representation of an instruction.
         */
        private Instruction(final XmlBytecodeEntry entry) {
            this.entry = entry;
        }

        /**
         * Compare the instructions.
         * @param instruction The instruction to compare with.
         * @throws CompareException If the instructions are different.
         */
        void compare(final Instruction instruction) throws CompareException {
            final XmlBytecodeEntry gentry = this.entry;
            final XmlBytecodeEntry bentry = instruction.entry;
            if (gentry instanceof XmlInstruction && bentry instanceof XmlInstruction) {
                final XmlInstruction ginstr = (XmlInstruction) gentry;
                final XmlInstruction binstr = (XmlInstruction) bentry;
                if (ginstr.opcode() != binstr.opcode()) {
                    throw new CompareException(
                        String.format(
                            "The opcodes %n'%s'%n and %n'%s'%n are different",
                            gentry,
                            bentry
                        )
                    );
                }
                final List<Operand> goperands = ginstr.operands()
                    .stream()
                    .map(Operand::new)
                    .collect(Collectors.toList());
                final List<Operand> boperands = binstr
                    .operands()
                    .stream()
                    .map(Operand::new)
                    .collect(Collectors.toList());
                for (int kindex = 0; kindex < goperands.size(); ++kindex) {
                    final Operand goperand = goperands.get(kindex);
                    final Operand boperand = boperands.get(kindex);
                    goperand.compare(boperand);
                }
            }
        }

    }

    /**
     * Operand to compare.
     * @since 0.2
     */
    private static final class Operand {

        /**
         * Xmir representation of an operand.
         */
        private final XmlOperand operand;

        /**
         * Constructor.
         * @param operand Xmir representation of an operand.
         */
        private Operand(final XmlOperand operand) {
            this.operand = operand;
        }

        /**
         * Compare the operands.
         * @param operand The operand to compare with.
         * @throws CompareException If the operands are different.
         */
        void compare(final Operand operand) throws CompareException {
            final XmlOperand goperand = this.operand;
            final XmlOperand boperand = operand.operand;
            final String gsoperand = goperand.toString();
            final String bsoperand = boperand.toString();
            if (gsoperand.contains("label") && bsoperand.contains("label")) {
                return;
            }
            if (!gsoperand.equals(bsoperand)) {
                throw new CompareException(
                    String.format(
                        "The operands '%s' and '%s' are different",
                        goperand,
                        boperand
                    )
                );
            }
        }
    }

    /**
     * Exception to be thrown when the comparison fails.
     * @since 0.2
     */
    private static final class CompareException extends Exception {

        /**
         * Constructor.
         * @param message The message to be shown.
         */
        private CompareException(final String message) {
            super(message);
        }

        /**
         * Constructor.
         * @param message The message to be shown.
         * @param cause The cause of the exception.
         */
        private CompareException(final String message, final CompareException cause) {
            super(message, cause);
        }
    }
}
