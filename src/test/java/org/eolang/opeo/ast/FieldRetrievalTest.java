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
import org.eolang.opeo.compilation.HasInstructions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link FieldRetrieval}.
 * @since 0.1
 */
final class FieldRetrievalTest {

    /**
     * XMIR representation of the field retrieval.
     */
    private static final String XMIR = String.join(
        "\n",
        "   <o base='.get-field'>",
        "      <o base='.bar'>",
        "         <o base='string' data='bytes'>64 65 73 63 72 69 70 74 6F 72 3D 49 7C 6E 61 6D 65 3D 62 61 72 7C 74 79 70 65 3D 66 69 65 6C 64</o>",
        "         <o base='$'>",
        "            <o base='string' data='bytes'>64 65 73 63 72 69 70 74 6F 72 3D 6A 61 76 61 2E 6C 61 6E 67 2E 4F 62 6A 65 63 74</o>",
        "         </o>",
        "      </o>",
        "</o>"
    );

    @Test
    void createsFromXmir() {
        MatcherAssert.assertThat(
            "The field retrieval should be successfully created from XMIR",
            new FieldRetrieval(new XmlNode(FieldRetrievalTest.XMIR), node -> new This()),
            Matchers.equalTo(new FieldRetrieval(new This(), "bar"))
        );
    }

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        final String actual = new Xembler(new FieldRetrieval(new This(), "bar").toXmir()).xml();
        MatcherAssert.assertThat(
            String.format(
                "Can't convert to a field access construct, actual result is : %n%s%n",
                actual
            ),
            actual,
            new SameXml(FieldRetrievalTest.XMIR)
        );
    }

    @Test
    void transformsToOpcodes() {
        final String descriptor = "S";
        final String owner = "java/lang/Object";
        final String name = "bar";
        MatcherAssert.assertThat(
            "Can't transform to opcodes",
            new OpcodeNodes(
                new FieldRetrieval(
                    new This(),
                    new Attributes("name", name, "descriptor", descriptor, "owner", owner)
                )
            ).opcodes(),
            new HasInstructions(
                new HasInstructions.Instruction(Opcodes.ALOAD, 0),
                new HasInstructions.Instruction(Opcodes.GETFIELD, owner, name, descriptor)
            )
        );
    }
}
