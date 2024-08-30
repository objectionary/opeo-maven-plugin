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
package org.eolang.opeo.ast;

import org.eolang.jeo.matchers.SameXml;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link If}.
 * @since 0.2
 */
final class IfTest {

    /**
     * Xmir for the 'if' statement.
     */
    private static final String XMIR = String.join(
        "\n",
        "<?xml version='1.0' encoding='UTF-8'?>",
        "<o base='.if'>",
        "   <o base='.gt'>",
        "      <o base='int' data='bytes'>00 00 00 00 00 00 00 01</o>",
        "      <o base='int' data='bytes'>00 00 00 00 00 00 00 02</o>",
        "   </o>",
        "   <o base='label' data='bytes'>C3 BF</o>",
        "   <o base='nop'/>",
        "</o>",
        ""
    );

    @Test
    void convertsIfStatementToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can convert 'if' statement to correct XMIR",
            new Xembler(
                new If(
                    new Const(1), new Const(2), new Label("FF")
                ).toXmir()
            ).xml(),
            new SameXml(IfTest.XMIR)
        );
    }

    @Test
    void createsIfStatementFromXmir() {
        MatcherAssert.assertThat(
            "Can create 'if' statement from XMIR",
            new If(
                new XmlNode(IfTest.XMIR),
                xml -> {
                    final AstNode result;
                    if (xml.text().contains("1")) {
                        result = new Const(1);
                    } else {
                        result = new Const(2);
                    }
                    return result;
                }
            ),
            Matchers.equalTo(
                new If(
                    new Const(1),
                    new Const(2),
                    new Label("C3 BF")
                )
            )
        );
    }

    @Test
    void convertsToOpcodes() {
        final Label label = new Label("C3 BF");
        MatcherAssert.assertThat(
            "Can convert 'if' statement to opcodes",
            new If(
                new Const(1),
                new Const(2),
                label
            ).opcodes(),
            Matchers.hasItems(
                new Opcode(Opcodes.ICONST_1),
                new Opcode(Opcodes.ICONST_2),
                new Opcode(Opcodes.IF_ICMPGT, label.toAsmLabel())
            )
        );
    }
}
