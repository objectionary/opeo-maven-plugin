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

import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.SameXml;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link CheckCast}.
 * @since 0.5
 */
class CheckCastTest {

    /**
     * XMIR representation of the CheckCast.
     */
    private static final String XMIR = String.join(
        "\n",
        "<o base='checkcast'>",
        "   <o base='type' data='bytes'>4A</o>",
        "   <o base='int' data='bytes'>00 00 00 00 00 00 00 03</o>",
        "</o>"
    );

    @Test
    void convertsCheckCastToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't convert CheckCast to XMIR",
            new Xembler(
                new CheckCast(
                    Type.LONG_TYPE,
                    new Literal(3)
                ).toXmir()
            ).xml(),
            new SameXml(CheckCastTest.XMIR)
        );
    }

    @Test
    void createsCheckCastFromXmir() {
        MatcherAssert.assertThat(
            "Can't parse CheckCast type from XMIR",
            new CheckCast(new XmlNode(CheckCastTest.XMIR), n -> new Literal(3)).type(),
            Matchers.equalTo(Type.LONG_TYPE)
        );
    }

    @Test
    void transformsToOpcodes() {
        final Type type = Type.INT_TYPE;
        MatcherAssert.assertThat(
            "Can't transform CheckCast to opcodes",
            new CheckCast(type, new Literal((byte) 3)).opcodes(),
            Matchers.hasItems(
                new Opcode(Opcodes.BIPUSH, (byte) 3),
                new Opcode(Opcodes.CHECKCAST, type.getDescriptor())
            )
        );
    }


}