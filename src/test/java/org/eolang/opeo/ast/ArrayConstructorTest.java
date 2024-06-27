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

import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.compilation.HasInstructions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link ArrayConstructor}.
 * @since 0.1
 */
final class ArrayConstructorTest {

    /**
     * XMIR representation of the array constructor.
     */
    private static final String XMIR = String.join(
        "\n",
        "<o base='.array-node'>",
        "   <o base='string' data='bytes'>6A 61 76 61 2F 6C 61 6E 67 2F 49 6E 74 65 67 65 72</o>",
        "   <o base='.plus'>",
        "      <o base='int' data='bytes'>00 00 00 00 00 00 00 01</o>",
        "      <o base='int' data='bytes'>00 00 00 00 00 00 00 02</o>",
        "   </o>",
        "</o>"
    );

    @Test
    void createsArrayConstructorFromXmir() {
        MatcherAssert.assertThat(
            "Can't create array constructor from XMIR",
            new ArrayConstructor(
                new XmlNode(ArrayConstructorTest.XMIR),
                node -> new Add(new Literal(1), new Literal(2))
            ),
            Matchers.equalTo(
                new ArrayConstructor(new Add(new Literal(1), new Literal(2)), "java/lang/Integer")
            )
        );
    }

    @Test
    void compilesSimpleArrayCreation() {
        final int size = 10;
        final String type = "java/lang/Integer";
        final ArrayConstructor constructor = new ArrayConstructor(new Literal(size), type);
        MatcherAssert.assertThat(
            "Can't compile array constructor with defined length",
            new OpcodeNodes(constructor).opcodes(),
            new HasInstructions(
                new HasInstructions.Instruction(Opcodes.BIPUSH, size),
                new HasInstructions.Instruction(Opcodes.ANEWARRAY, type),
                new HasInstructions.Instruction(Opcodes.DUP)
            )
        );
    }

    @Test
    void compilesArrayWithComplexLength() {
        final String type = "java/lang/Integer";
        MatcherAssert.assertThat(
            "Can't compile array constructor with complex undefined length",
            new OpcodeNodes(
                new ArrayConstructor(
                    new Add(new Literal(1), new Literal(2)),
                    type
                )
            ).opcodes(),
            new HasInstructions(
                new HasInstructions.Instruction(Opcodes.ICONST_1),
                new HasInstructions.Instruction(Opcodes.ICONST_2),
                new HasInstructions.Instruction(Opcodes.IADD),
                new HasInstructions.Instruction(Opcodes.ANEWARRAY, type),
                new HasInstructions.Instruction(Opcodes.DUP)
            )
        );
    }

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        final String type = "java/lang/Integer";
        final String xmir = new Xembler(
            new ArrayConstructor(
                new Add(new Literal(1), new Literal(2)),
                type
            ).toXmir()
        ).xml();
        MatcherAssert.assertThat(
            String.format(
                "We expect that array constructor will be correctly transformed to XMIR, but it didn't. Result is: %n%s%n",
                xmir
            ),
            new XMLDocument(xmir),
            Matchers.equalTo(new XMLDocument(ArrayConstructorTest.XMIR))
        );
    }
}
