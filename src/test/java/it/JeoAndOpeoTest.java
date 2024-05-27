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

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
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
import org.eolang.opeo.ast.Label;
import org.eolang.opeo.ast.Labeled;
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
    })
    void decompilesCompilesAndKeepsTheSameInstructions(final String path) throws Exception {
        final XMLDocument original = new XMLDocument(new BytesOf(new ResourceOf(path)).asBytes());
        final XML decompiled = new JeoDecompiler(original).decompile();
        System.out.println(decompiled);
        MatcherAssert.assertThat(
            "The original and compiled instructions are not equal",
            new JeoInstructions(
                new XmlProgram(
                    new JeoCompiler(
                        decompiled
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
    void findTheProblem() {
        Path golden = Paths.get(
            "/Users/lombrozo/Workspace/EOlang/opeo-maven-plugin/target-standard/it/spring-fat/target/generated-sources/opeo-compile-xmir/org"
        );

        Path real = Paths.get(
            "/Users/lombrozo/Workspace/EOlang/opeo-maven-plugin/target/it/spring-fat/target/generated-sources/opeo-compile-xmir/org"
        );

        try (final Stream<Path> all = Files.walk(golden).filter(Files::isRegularFile)) {
            all.forEach(
                path -> {
                    final Path relative = golden.relativize(path);
                    XMLDocument good = null;
                    try {
                        good = new XMLDocument(golden.resolve(relative));
                    } catch (final FileNotFoundException exception) {
                        throw new RuntimeException(exception);
                    }
                    XMLDocument bad = null;
                    try {
                        bad = new XMLDocument(real.resolve(relative));
                    } catch (final FileNotFoundException exception) {
                        throw new RuntimeException(exception);
                    }

                    final XmlProgram goodProgram = new XmlProgram(good);
                    final XmlProgram badProgram = new XmlProgram(bad);
                    final List<XmlMethod> goodMethods = goodProgram.top().methods();
                    final List<XmlMethod> badMethods = badProgram.top().methods();
                    for (int i = 0; i < goodMethods.size(); i++) {
                        final XmlMethod goodMethod = goodMethods.get(i);
                        final XmlMethod badMethod = badMethods.get(i);
                        final List<XmlBytecodeEntry> goodInstructions = goodMethod.instructions()
                            .stream()
                            .filter(xmlBytecodeEntry -> !(xmlBytecodeEntry instanceof XmlLabel))
                            .collect(Collectors.toList());
                        final List<XmlBytecodeEntry> badInstructions = badMethod.instructions()
                            .stream()
                            .filter(xmlBytecodeEntry -> !(xmlBytecodeEntry instanceof XmlLabel))
                            .collect(Collectors.toList());
                        for (int j = 0; j < goodInstructions.size(); j++) {
                            final XmlBytecodeEntry goodEntry = goodInstructions.get(j);
                            final XmlBytecodeEntry badEntry = badInstructions.get(j);

                            if (goodEntry instanceof XmlInstruction && badEntry instanceof XmlInstruction) {

                                final XmlInstruction goodInstruction = (XmlInstruction) goodEntry;
                                final XmlInstruction badInstruction = (XmlInstruction) badEntry;

                                if (goodInstruction.opcode() != badInstruction.opcode()) {
                                    throw new IllegalArgumentException(
                                        String.format(
                                            "The operands '%s' and '%s' are differ in %n%s%n%s",
                                            goodEntry,
                                            badEntry,
                                            "file://" + golden.resolve(relative),
                                            "file://" + real.resolve(relative)
                                        )
                                    );
                                }
                                final List<XmlOperand> goodOperands = goodInstruction.operands();
                                final List<XmlOperand> badOperands = badInstruction.operands();
                                for (int k = 0; k < goodOperands.size(); k++) {
                                    final XmlOperand goodOperand = goodOperands.get(k);
                                    final XmlOperand badOperand = badOperands.get(k);

                                    final String goodOperandString = goodOperand.toString();
                                    final String badOperandString = badOperand.toString();
                                    if (goodOperandString.contains(
                                        "label") && badOperandString.contains("label"))
                                        continue;
                                    if (!goodOperandString.equals(badOperandString)) {
                                        throw new IllegalArgumentException(
                                            String.format(
                                                "The operands '%s' and '%s' are differ in '%s'%n'%s'%n'%s',%n%s%n%s",
                                                goodEntry,
                                                badEntry,
                                                relative,
                                                goodOperand,
                                                badOperand,
                                                "file://" + golden.resolve(relative),
                                                "file://" + real.resolve(relative)
                                            )
                                        );
                                    }
                                }

                            }


                        }

                    }
                }
            );
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }


}
