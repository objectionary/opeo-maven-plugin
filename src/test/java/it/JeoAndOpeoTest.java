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
import java.util.List;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.matchers.SameXml;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.XmirRepresentation;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.xmir.XmlBytecodeEntry;
import org.eolang.jeo.representation.xmir.XmlLabel;
import org.eolang.jeo.representation.xmir.XmlProgram;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.compilation.JeoCompiler;
import org.eolang.opeo.jeo.JeoDecompiler;
import org.eolang.opeo.jeo.JeoInstructions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Integration tests for JEO and OPEO transformations.
 * This class is rather useful for bug identification on different transformation stages.
 * @since 0.2
 * @todo #284:90min Enable {@link #decompilesAndCompilesTryCatchBlocks(String)} test.
 *  This test is disabled because the decompilation and compilation of bytecode with
 *  try-catch-blocks significantly differs from the plain bytecode.
 *  Now, this transformation fails (you can try to enable the test to check.)
 *  We need to fix this issue and enable the test.
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
                new JeoCompiler(
                    new XMLDocument(
                        new BytesOf(
                            new ResourceOf(String.format("xmir/compiled/%s", path))
                        ).asBytes()
                    )
                ).compile()
            ).toBytecode(),
            "We expect that the pipeline 'already-compiled-xmir' -> (compile) -> (assemble) ->'.class' will not throw any exceptions."
        );
    }

    @ParameterizedTest
    @CsvSource("WebProperties$Resources$Chain$Strategy$Content.xmir")
    @Disabled
    void compilesDecompiled(final String path) {
        Assertions.assertDoesNotThrow(
            () -> new XmirRepresentation(
                new JeoCompiler(
                    new XMLDocument(
                        new BytesOf(
                            new ResourceOf(String.format("xmir/decompiled/%s", path))
                        ).asBytes()
                    )
                ).compile()
            ).toBytecode(),
            "We expect that the pipeline 'already-decompiled-xmir' -> (compile) -> (assemble) ->'.class' will not throw any exceptions."
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
        "xmir/disassembled/Factorial.xmir",
        "xmir/disassembled/Lambda.xmir",
        "xmir/disassembled/Main.xmir",
        "xmir/disassembled/UndertowWebServerFactoryCustomizer$ServerOptions.xmir",
        "xmir/disassembled/MutableCoercionConfig.xmir",
        "xmir/disassembled/WebProperties$Resources$Chain$Strategy$Content.xmir",
        "xmir/disassembled/OAuth2ClientRegistrationRepositoryConfiguration.xmir",
        "xmir/disassembled/DefaultRouterFunctionSpec.xmir",
        "xmir/disassembled/SpringBootExceptionHandler$LoggedExceptionHandlerThreadLocal.xmir",
        "xmir/disassembled/ApplicationContextAssertProvider.xmir",
        "xmir/disassembled/Sum.xmir"
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
        "xmir/disassembled/Main.xmir",
        "xmir/disassembled/CachingJupiterConfiguration.xmir"
    })
    void decompilesCompilesAndKeepsTheSameInstructionsWithTheSameOperands(
        final String path
    ) throws Exception {
        Opcode.disableCounting();
        final XMLDocument original = new XMLDocument(new BytesOf(new ResourceOf(path)).asBytes());
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
    @CsvSource("xmir/disassembled/OpenSSLContext$1.xmir")
    void decompilesAndCompilesTryCatchBlocks(final String path) throws Exception {
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
    @CsvSource(
        "xmir/disassembled/SimpleTypeConverter.xmir, org.springframework.beans.SimpleTypeConverter"
    )
    void decompilesCompilesAndKeepsExactlyTheSameContent(
        final String path,
        final String pckg
    ) throws Exception {
        Opcode.disableCounting();
        final XMLDocument original = new XMLDocument(new BytesOf(new ResourceOf(path)).asBytes());
        MatcherAssert.assertThat(
            "The original and decompiled/compiled content are not equal",
            new JeoCompiler(new JeoDecompiler(original, pckg).decompile()).compile().toString(),
            new SameXml(original)
        );
    }
}
