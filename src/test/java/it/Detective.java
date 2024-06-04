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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.xmir.XmlBytecodeEntry;
import org.eolang.jeo.representation.xmir.XmlInstruction;
import org.eolang.jeo.representation.xmir.XmlLabel;
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
        "./target-standard/it/spring-fat/target/generated-sources/opeo-compile-xmir, ./target/it/spring-fat/target/generated-sources/opeo-compile-xmir"
    )
    void findTheProblem(final String etalon, final String target) {
        final Path golden = Paths.get(etalon);
        final Path real = Paths.get(target);
        final Results results = new Results();
        try (Stream<Path> all = Files.walk(golden).filter(Files::isRegularFile)) {
            all.forEach(
                path -> {
                    results.oneMore();
                    final Path relative = golden.relativize(path);
                    final XMLDocument bad;
                    final XMLDocument good;
                    try {
                        good = new XMLDocument(golden.resolve(relative));
                        bad = new XMLDocument(real.resolve(relative));
                    } catch (final FileNotFoundException exception) {
                        throw new IllegalArgumentException(
                            String.format("File not found: %s", relative),
                            exception
                        );
                    }
                    final XmlProgram gprogram = new XmlProgram(good);
                    final XmlProgram bprogram = new XmlProgram(bad);
                    final List<XmlMethod> gmethods = gprogram.top().methods();
                    final List<XmlMethod> bmethods = bprogram.top().methods();
                    final int size = gmethods.size();
                    outer:
                    for (int index = 0; index < size; ++index) {
                        final XmlMethod gmethod = gmethods.get(index);
                        final XmlMethod bmethod = bmethods.get(index);
                        final List<XmlBytecodeEntry> ginstrs = gmethod.instructions()
                            .stream()
                            .filter(xmlBytecodeEntry -> !(xmlBytecodeEntry instanceof XmlLabel))
                            .collect(Collectors.toList());
                        final List<XmlBytecodeEntry> binstrs = bmethod.instructions()
                            .stream()
                            .filter(xmlBytecodeEntry -> !(xmlBytecodeEntry instanceof XmlLabel))
                            .collect(Collectors.toList());
                        final int isize = ginstrs.size();
                        for (int jindex = 0; jindex < isize; ++jindex) {
                            final XmlBytecodeEntry gentry = ginstrs.get(jindex);
                            final XmlBytecodeEntry bentry = binstrs.get(jindex);
                            if (gentry instanceof XmlInstruction
                                && bentry instanceof XmlInstruction) {
                                final XmlInstruction ginstr = (XmlInstruction) gentry;
                                final XmlInstruction binstr = (XmlInstruction) bentry;
                                if (ginstr.opcode() != binstr.opcode()) {
                                    final String message = String.format(
                                        "The operands '%s' and '%s' are differ in %n%s%n%s,%nTotal scanned files: %d",
                                        gentry,
                                        bentry,
                                        String.format("file://%s", golden.resolve(relative)),
                                        String.format("file://%s", real.resolve(relative)),
                                        );
                                    results.problem(message);
                                    break outer;
                                }
                                final List<XmlOperand> goperands = ginstr.operands();
                                final List<XmlOperand> boperands = binstr.operands();
                                for (int kindex = 0; kindex < goperands.size(); ++kindex) {
                                    final XmlOperand goperand = goperands.get(kindex);
                                    final XmlOperand boperand = boperands.get(kindex);
                                    final String gsoperand = goperand.toString();
                                    final String bsoperand = boperand.toString();
                                    if (gsoperand.contains("label")
                                        && bsoperand.contains("label")) {
                                        continue;
                                    }
                                    if (!gsoperand.equals(bsoperand)) {
                                        final String message = String.format(
                                            "The operands '%s' and '%s' are differ in '%s'%n'%s'%n'%s',%n%s%n%s%n",
                                            gentry,
                                            bentry,
                                            relative,
                                            goperand,
                                            boperand,
                                            String.format("file://%s", golden.resolve(relative)),
                                            String.format("file://%s", real.resolve(relative))
                                        );
                                        results.problem(message);
                                        break outer;
                                    }
                                }
                            }
                        }
                    }
                    results.logCounters();
                }
            );
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
            this.logs = new ArrayList<>(0);
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
                "Total files scanned:%d%nTotal errors:%d%n",
                this.total.get(),
                this.errors
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


    private static class DisassembledXmir {

        private final Path path;

        private DisassembledXmir(final Path path) {
            this.path = path;
        }

        public void compare(final DisassembledXmir xmir) {

        }
    }


}
