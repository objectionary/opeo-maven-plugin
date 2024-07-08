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
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test case for {@link VariableAssignment}.
 * @since 0.2
 */
final class VariableAssignmentTest {

    /**
     * XMIR representation of the variable assignment.
     */
    private static final String XMIR = String.join(
        "\n",
        "<o base='.write-local-var'>",
        "   <o base='local1'>",
        "      <o base='string' data='bytes'>64 65 73 63 72 69 70 74 6F 72 3D 44 7C 74 79 70 65 3D 6C 6F 63 61 6C</o>",
        "   </o>",
        "   <o base='int' data='bytes'>00 00 00 00 00 00 00 02</o>",
        "</o>"
    );

    @Test
    void createsFromXmir() {
        MatcherAssert.assertThat(
            "Can't create correct variable assignment from XMIR",
            new VariableAssignment(
                new XmlNode(VariableAssignmentTest.XMIR),
                node -> {
                    final AstNode result;
                    if (node.hasAttribute("base", "int")) {
                        result = new Literal(2);
                    } else {
                        result = new LocalVariable(node);
                    }
                    return result;
                }
            ),
            Matchers.equalTo(
                new VariableAssignment(
                    new LocalVariable(1, Type.DOUBLE_TYPE),
                    new Literal(2)
                )
            )
        );
    }

    @Test
    void convertsToXmirCorrectly() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new VariableAssignment(
                new LocalVariable(1, Type.DOUBLE_TYPE),
                new Literal(2)
            ).toXmir(),
            new Transformers.Node()
        ).xml();
        MatcherAssert.assertThat(
            String.format(
                "We expect the XMIR to be correct, but it's not:%n%s%n",
                xml
            ),
            xml,
            new SameXml(VariableAssignmentTest.XMIR)
        );
    }
}
