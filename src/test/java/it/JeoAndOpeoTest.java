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
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.XmirRepresentation;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.xmir.XmlBytecodeEntry;
import org.eolang.jeo.representation.xmir.XmlInstruction;
import org.eolang.jeo.representation.xmir.XmlLabel;
import org.eolang.jeo.representation.xmir.XmlMethod;
import org.eolang.jeo.representation.xmir.XmlOperand;
import org.eolang.jeo.representation.xmir.XmlProgram;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.compilation.JeoCompiler;
import org.eolang.opeo.jeo.JeoDecompiler;
import org.eolang.opeo.jeo.JeoInstructions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Integration tests for JEO and OPEO transformations.
 * This class is rather useful for bug identification on different transformation stages.
 * @since 0.2
 * @todo #229:90min Refactor JeoAndOpeoTest.
 *  In orderd to check the correctness of the transformation from JEO to OPEO,
 *  we created this class. Since we didn't have time to write it properly,
 *  we need to refactor it. The main goal is to make the tests more readable and
 *  to make the test names more descriptive. Moreover, the test
 *  {@link #findTheProblem(String, String)} should be moved into a separate place.
 */
final class JeoAndOpeoTest {

    @ParameterizedTest
    @CsvSource("AgentBuilder.class")
    void dissassemblesDecompilesCompilesAssembles(final String name) {
        Assertions.assertDoesNotThrow(
            () -> new XmirRepresentation(
                new JeoCompiler(
                    new JeoDecompiler(
                        new BytecodeRepresentation(
                            new Bytecode(
                                new BytesOf(
                                    new ResourceOf(String.format("bytecode/%s", name))
                                ).asBytes()
                            )
                        ).toEO()
                    ).decompile()
                ).compile()
            ).toBytecode(),
            "We expect that the entire pipeline '.class' -> 'jeo-xmir' -> 'opeo-xmir' -> 'jeo-xmir' -> '.class' will not throw any exceptions."
        );
    }

    @ParameterizedTest
    @CsvSource("AgentBuilder$RedefinitionStrategy$Collector.xmir")
    void compilesAlreadyCompiledAndAssembles(final String path) {
        Assertions.assertDoesNotThrow(
            () -> new XmirRepresentation(
                new JeoCompiler(new XMLDocument(new BytesOf(
                    new ResourceOf(String.format("xmir/compiled/%s", path))
                ).asBytes())).compile()
            ).toBytecode(),
            "We expect that the pipeline 'already-compiled-xmir' -> (compile) -> (assemble) ->'.class' will not throw any exceptions."
        );
    }

    @ParameterizedTest
    @CsvSource({
        "xmir/disassembled/AsIsEscapeUtil.xmir",
        "xmir/disassembled/LongArrayAssert.xmir",
        "xmir/disassembled/AssertionsKt$sam$i$org_junit_jupiter_api_function_Executable$0.xmir",
        "xmir/disassembled/App.xmir",
        "xmir/disassembled/SmartLifecycle.xmir",
        "xmir/disassembled/FlightRecorderStartupEvent.xmir",
        "xmir/disassembled/SimpleLog.xmir",
        "xmir/disassembled/OpenSSLContext$1.xmir",
    })
    void decompilesCompilesAndKeepsTheSameInstructions(final String path) throws Exception {
        final XMLDocument original = new XMLDocument(new BytesOf(new ResourceOf(path)).asBytes());
        MatcherAssert.assertThat(
            "The original and compiled instructions are not equal",
            new JeoInstructions(
                new XmlProgram(
                    new JeoCompiler(
                        new JeoDecompiler(original).decompile()
                    ).compile()
                ).top().methods().get(0)
            ).instuctionNames(),
            Matchers.equalTo(
                new JeoInstructions(
                    new XmlProgram(original).top().methods().get(0)
                ).instuctionNames()
            )
        );
    }

    @ParameterizedTest
    @CsvSource({
        "xmir/disassembled/SimpleLog.xmir",
        "xmir/disassembled/OpenSSLContext$1.xmir",
    })
    void decompilesCompilesAndKeepsTheSameInstructionsWithTheSameOperands(
        final String path
    ) throws Exception {
        final XMLDocument original = new XMLDocument(new BytesOf(new ResourceOf(path)).asBytes());
        Opcode.disableCounting();
        final List<XmlBytecodeEntry> actual = new XmlProgram(
            new JeoCompiler(
                new JeoDecompiler(original).decompile()
            ).compile()
        ).top().methods().get(0).instructions();
        final List<XmlBytecodeEntry> expected = new XmlProgram(original).top().methods().get(0)
            .instructions();
        final int size = expected.size();
        for (int index = 0; index < size; ++index) {
            final XmlBytecodeEntry expect = expected.get(index);
            final XmlBytecodeEntry act = actual.get(index);
            if (expect instanceof XmlLabel && act instanceof XmlLabel) {
                continue;
            }
            MatcherAssert.assertThat(
                "The original and compiled instructions are not equal",
                act,
                Matchers.equalTo(expect)
            );
        }
    }

    @Disabled
    @ParameterizedTest
    @CsvSource("xmir/disassembled/SimpleTypeConverter.xmir, org.springframework.beans.SimpleTypeConverter")
    void decompilesCompilesAndKeepsExactlyTheSameContent(
        final String path, final String pckg
    ) throws Exception {
        Opcode.disableCounting();
        final XMLDocument original = new XMLDocument(new BytesOf(new ResourceOf(path)).asBytes());
        MatcherAssert.assertThat(
            "The original and decompiled/compiled content are not equal",
            new JeoCompiler(new JeoDecompiler(original, pckg).decompile()).compile(),
            Matchers.equalTo(original)
        );
    }


    @Test
    @Disabled
    @ParameterizedTest
    @CsvSource(
        "./target-standard/it/spring-fat/target/generated-sources/opeo-compile-xmir, ./target/it/spring-fat/target/generated-sources/opeo-compile-xmir"
    )
    void findTheProblem(final String etalon, final String target) {
        final Path golden = Paths.get(etalon);
        final Path real = Paths.get(target);
        final AtomicInteger counter = new AtomicInteger(0);
        final AtomicInteger ecounter = new AtomicInteger(0);
        final List<String> errors = new ArrayList<>(0);
        try (final Stream<Path> all = Files.walk(golden).filter(Files::isRegularFile)) {
            all.forEach(
                path -> {
                    counter.incrementAndGet();
                    final Path relative = golden.relativize(path);
                    XMLDocument bad;
                    XMLDocument good;
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
                        final List<XmlBytecodeEntry> ginstuctions = gmethod.instructions()
                            .stream()
                            .filter(xmlBytecodeEntry -> !(xmlBytecodeEntry instanceof XmlLabel))
                            .collect(Collectors.toList());
                        final List<XmlBytecodeEntry> binstructions = bmethod.instructions()
                            .stream()
                            .filter(xmlBytecodeEntry -> !(xmlBytecodeEntry instanceof XmlLabel))
                            .collect(Collectors.toList());
                        final int isize = ginstuctions.size();
                        for (int jindex = 0; jindex < isize; ++jindex) {
                            final XmlBytecodeEntry gentry = ginstuctions.get(jindex);
                            final XmlBytecodeEntry bentry = binstructions.get(jindex);
                            if (gentry instanceof XmlInstruction
                                && bentry instanceof XmlInstruction) {
                                final XmlInstruction ginstruction = (XmlInstruction) gentry;
                                final XmlInstruction binstruction = (XmlInstruction) bentry;
                                if (ginstruction.opcode() != binstruction.opcode()) {
                                    final String message = String.format(
                                        "The operands '%s' and '%s' are differ in %n%s%n%s,%nTotal scanned files: %d",
                                        gentry,
                                        bentry,
                                        String.format("file://%s", golden.resolve(relative)),
                                        String.format("file://%s", real.resolve(relative)),
                                        counter.get()
                                    );
                                    errors.add(message);
                                    ecounter.incrementAndGet();
                                    break outer;
                                }
                                final List<XmlOperand> goodOperands = ginstruction.operands();
                                final List<XmlOperand> badOperands = binstruction.operands();
                                for (int kindex = 0; kindex < goodOperands.size(); ++kindex) {
                                    final XmlOperand goodOperand = goodOperands.get(kindex);
                                    final XmlOperand badOperand = badOperands.get(kindex);
                                    final String goodOperandString = goodOperand.toString();
                                    final String badOperandString = badOperand.toString();
                                    if (goodOperandString.contains("label")
                                        && badOperandString.contains("label"))
                                        continue;
                                    if (!goodOperandString.equals(badOperandString)) {
                                        final String message = String.format(
                                            "The operands '%s' and '%s' are differ in '%s'%n'%s'%n'%s',%n%s%n%s%nTotal scanned files: %d",
                                            gentry,
                                            bentry,
                                            relative,
                                            goodOperand,
                                            badOperand,
                                            String.format("file://%s", golden.resolve(relative)),
                                            String.format("file://%s", real.resolve(relative)),
                                            counter.get()
                                        );
                                        errors.add(message);
                                        ecounter.incrementAndGet();
                                        break outer;
                                    }
                                }
                            }
                        }

                    }
                    System.out.printf("Total files:%d%n", counter.get());
                    System.out.printf("Number of failed files:%d%n", ecounter.get());
                }
            );
        } catch (final IOException exception) {
            throw new IllegalStateException("Can't walk through the files", exception);
        }
        System.out.println("All Found Errors:\n");
        errors.forEach(System.out::println);
    }
}
