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
 * Test for {@link FieldAssignment}.
 * @since 0.2
 */
final class FieldAssignmentTest {

    /**
     * XMIR representation of the field assignment.
     */
    private static final String XMIR = String.join(
        "\n",
        "<o base='.write-field'>",
        "      <o base='.bar'>",
        "         <o base='string' data='bytes'>6E 61 6D 65 3D 62 61 72</o>",
        "         <o base='$'>",
        "            <o base='string' data='bytes'>64 65 73 63 72 69 70 74 6F 72 3D 6A 61 76 61 2E 6C 61 6E 67 2E 4F 62 6A 65 63 74</o>",
        "         </o>",
        "      </o>",
        "      <o base='int' data='bytes'>00 00 00 00 00 00 00 03</o>",
        "</o>"
    );

    @Test
    void createsFromXmir() {
        final FieldAssignment assignment = new FieldAssignment(
            new XmlNode(FieldAssignmentTest.XMIR),
            node -> {
                final AstNode result;
                if (node.hasAttribute("base", "int")) {
                    result = new Literal(node);
                } else {
                    result = new This(node);
                }
                return result;
            }
        );
        MatcherAssert.assertThat(
            "The field assignment should be successfully created from XMIR",
            assignment,
            Matchers.equalTo(
                new FieldAssignment(
                    new Field(new This(), new Attributes("name", "bar")),
                    new Literal(3)
                )
            )
        );
    }

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        final String xmir = new Xembler(
            new FieldAssignment(
                new Field(new This(), new Attributes("name", "bar")),
                new Literal(3)
            ).toXmir()
        ).xml();
        MatcherAssert.assertThat(
            String.format(
                "Can't convert 'this.bar = 3' statement to the correct xmir, result is wrong: %n%s%n",
                xmir
            ),
            new XMLDocument(xmir),
            Matchers.equalTo(new XMLDocument(FieldAssignmentTest.XMIR))
        );
    }

    @Test
    void transformsToOpcodes() {
        final String name = "d";
        final String owner = "test/Test";
        final String descriptor = "I";
        MatcherAssert.assertThat(
            "Can't transform 'this.a = 1' statement to the correct opcodes, result is wrong",
            new OpcodeNodes(
                new FieldAssignment(
                    new Field(
                        new This(),
                        new Attributes()
                            .name(name)
                            .owner(owner)
                            .descriptor(descriptor)
                    ),
                    new Literal(1)
                )
            ).opcodes(),
            new HasInstructions(
                new HasInstructions.Instruction(Opcodes.ALOAD, 0),
                new HasInstructions.Instruction(Opcodes.ICONST_1),
                new HasInstructions.Instruction(Opcodes.PUTFIELD, owner, name, descriptor)
            )
        );
    }
}
