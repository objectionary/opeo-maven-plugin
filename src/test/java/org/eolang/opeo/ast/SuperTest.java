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
import org.eolang.jeo.representation.HexData;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.compilation.HasInstructions;
import org.eolang.opeo.compilation.Parser;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link Super}.
 * @since 0.1
 */
final class SuperTest {

    /**
     * XMIR for the 'super' statement.
     */
    private static final String XMIR = String.join(
        "",
        "<o base='.super'>",
        "   <o base='string' data='bytes'>64 65 73 63 72 69 70 74 6F 72 3D 28 29 56 7C 6E 61 6D 65 3D 3C 69 6E 69 74 3E 7C 6F 77 6E 65 72 3D 6A 61 76 61 2F 6C 61 6E 67 2F 4F 62 6A 65 63 74</o>",
        "   <o base='$'>",
        "      <o base='string' data='bytes'>64 65 73 63 72 69 70 74 6F 72 3D 6A 61 76 61 2E 6C 61 6E 67 2E 4F 62 6A 65 63 74</o>",
        "   </o>",
        "</o>"
    );

    /**
     * Dummy parser for the 'super' statement.
     */
    private static final Parser PARSER = (node) -> new This();


    @Test
    void createsFromXmir() {
        MatcherAssert.assertThat(
            "Can't parse 'super' statement from XMIR",
            new Super(new XmlNode(SuperTest.XMIR), SuperTest.PARSER),
            Matchers.equalTo(new Super(new This()))
        );
    }

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        final String xmir = new Xembler(new Super(new This()).toXmir()).xml();
        MatcherAssert.assertThat(
            String.format(
                "Can't convert 'super' statement to XMIR, result is wrong: %n%s%n",
                xmir
            ),
            new XMLDocument(xmir),
            Matchers.equalTo(new XMLDocument(SuperTest.XMIR))
        );
    }

    @Test
    void convertsToXmirWithCustomDescriptor() throws ImpossibleModificationException {
        final String descriptor = "(I)V";
        final String xmir = new Xembler(
            new Super(new This(), descriptor, new Literal(10)).toXmir()
        ).xml();
        MatcherAssert.assertThat(
            String.format(
                "Can't convert 'super' statement to XMIR, result is wrong: %n%s%n",
                xmir
            ),
            xmir,
            Matchers.containsString(new HexData(descriptor).value())
        );
    }

    @Test
    void convertsToOpcodes() {
        MatcherAssert.assertThat(
            "Can't convert 'super' statement to opcodes with correct arguments",
            new OpcodeNodes(
                new Super(new This(), "(I)V", new Literal(1))
            ).opcodes(),
            new HasInstructions(
                new HasInstructions.Instruction(Opcodes.ALOAD, 0),
                new HasInstructions.Instruction(Opcodes.ICONST_1),
                new HasInstructions.Instruction(
                    Opcodes.INVOKESPECIAL,
                    "java/lang/Object",
                    "<init>",
                    "(I)V",
                    false
                )
            )
        );
    }

    @Test
    void convertsToOpcodesWithNoArguments() {
        MatcherAssert.assertThat(
            "Can't convert 'super' statement to opcodes with no arguments",
            new OpcodeNodes(
                new Super(new This())
            ).opcodes(),
            new HasInstructions(
                new HasInstructions.Instruction(Opcodes.ALOAD, 0),
                new HasInstructions.Instruction(
                    Opcodes.INVOKESPECIAL,
                    "java/lang/Object",
                    "<init>",
                    "()V",
                    false
                )
            )
        );
    }

    @Test
    void convertsToOpcodesWithMultipleArguments() {
        MatcherAssert.assertThat(
            "Can't convert 'super' statement to opcodes with multiple arguments",
            new OpcodeNodes(
                new Super(new This(), "(II)V", new Literal(1), new Literal(2))
            ).opcodes(),
            new HasInstructions(
                new HasInstructions.Instruction(Opcodes.ALOAD, 0),
                new HasInstructions.Instruction(Opcodes.ICONST_1),
                new HasInstructions.Instruction(Opcodes.ICONST_2),
                new HasInstructions.Instruction(
                    Opcodes.INVOKESPECIAL,
                    "java/lang/Object",
                    "<init>",
                    "(II)V",
                    false
                )
            )
        );
    }

    @Test
    @Disabled("Not implemented yet")
    void convertsToOpcodesWithParent() {
        MatcherAssert.assertThat(
            "Can't convert 'super' statement to opcodes with parent",
            new OpcodeNodes(
                new Super(new This())
            ).opcodes(),
            new HasInstructions(
                new HasInstructions.Instruction(Opcodes.ALOAD, 0),
                new HasInstructions.Instruction(
                    Opcodes.INVOKESPECIAL,
                    "some/interesting/Parent",
                    "<init>",
                    "()V",
                    false
                )
            )
        );
    }
}
