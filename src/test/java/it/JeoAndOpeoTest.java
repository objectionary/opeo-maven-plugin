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
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.XmirRepresentation;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.xmir.XmlProgram;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.compilation.JeoCompiler;
import org.eolang.opeo.jeo.JeoDecompiler;
import org.eolang.opeo.jeo.JeoInstructions;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
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
    @CsvSource("xmir/disassembled/AsIsEscapeUtil.xmir")
    void decompilesCompiles(final String path) throws Exception {
        MatcherAssert.assertThat(
            "The original and compiled instructions are not equal",
            new JeoInstructions(
                new XmlProgram(
                    new JeoCompiler(
                        new JeoDecompiler(
                            new XMLDocument(
                                new BytesOf(
                                    new ResourceOf(path)
                                ).asBytes()
                            )
                        ).decompile()
                    ).compile()
                ).top().methods().get(0)
            ).instuctionNames(),
            Matchers.equalTo(
                new JeoInstructions(
                    new XmlProgram(
                        new XMLDocument(
                            new BytesOf(
                                new ResourceOf(path)
                            ).asBytes()
                        )
                    ).top().methods().get(0)
                ).instuctionNames()
            )
        );
    }

}
