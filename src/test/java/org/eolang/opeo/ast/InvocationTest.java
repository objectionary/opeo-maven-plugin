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

import com.jcabi.matchers.XhtmlMatchers;
import org.eolang.jeo.matchers.SameXml;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.compilation.HasInstructions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test case for {@link Invocation}.
 * @since 0.1
 */
final class InvocationTest {

    /**
     * XMIR for the 'invocation' statement.
     * new foo().bar("baz")
     */
    private static final String XMIR = String.join(
        "",
        "<o base='.bar'>",
        "   <o base='.new'>",
        "      <o base='.new-type'>",
        "         <o base='string' data='bytes'>66 6F 6F</o>",
        "      </o>",
        "      <o base='string' data='bytes'/>",
        "   </o>",
        "   <o base='string' data='bytes'>64 65 73 63 72 69 70 74 6F 72 3D 56 28 29 7C 6E 61 6D 65 3D 62 61 72 7C 74 79 70 65 3D 6D 65 74 68 6F 64</o>",
        "   <o base='string' data='bytes'>62 61 7A</o>",
        "</o>"
    );

    @Test
    void createsFromXmir() {
        MatcherAssert.assertThat(
            "Can't parse 'invocation' statement from XMIR",
            new Invocation(
                new XmlNode(InvocationTest.XMIR),
                node -> {
                    final AstNode result;
                    if (node.attribute("base").map("string"::equals).orElse(false)) {
                        result = new Literal(node);
                    } else {
                        result = new Constructor("foo");
                    }
                    return result;
                }
            ),
            Matchers.equalTo(
                new Invocation(
                    new Constructor("foo"),
                    "bar",
                    new Literal("baz")
                )
            )
        );
    }

    @Test
    void transformsToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't transform 'invocation' to XMIR",
            new Xembler(
                new Invocation(
                    new Constructor("foo"),
                    "bar",
                    new Literal("baz")
                ).toXmir(),
                new Transformers.Node()
            ).xml(),
            new SameXml(InvocationTest.XMIR)
        );
    }

    @Test
    void savesDescriptorToAttribute() {
        final String xml = new Xembler(
            new Invocation(
                new This(),
                new Attributes().name("bar")
                    .descriptor("(Ljava/lang/String;)Ljava/lang/String;")
                    .owner("some/Owner"),
                new Literal("baz")
            ).toXmir(),
            new Transformers.Node()
        ).xmlQuietly();
        MatcherAssert.assertThat(
            String.format("Can't save descriptor to '.bar' invocation attribute %s", xml),
            xml,
            XhtmlMatchers.hasXPaths(
                "./o[@base='.bar']/o[@base='string' and contains(text(),'28 4C 6A 61 76 61 2F 6C 61 6E 67 2F 53 74 72 69 6E 67 3B 29 4C 6A 61 76 61 2F 6C 61 6E 67 2F 53 74 72 69 6E 67 3B')]"
            )
        );
    }

    @Test
    void transformsToOpcodes() {
        final String name = "bar";
        final String constant = "baz";
        final String descriptor = "(Ljava/lang/String;)Ljava/lang/String;";
        MatcherAssert.assertThat(
            "Can't transform 'invocation' to correct opcodes",
            new OpcodeNodes(
                new Invocation(
                    new This(),
                    new Attributes().descriptor(descriptor).interfaced(false).name(name),
                    new Literal(constant)
                )
            ).opcodes(),
            new HasInstructions(
                new HasInstructions.Instruction(Opcodes.ALOAD, 0),
                new HasInstructions.Instruction(Opcodes.LDC, constant),
                new HasInstructions.Instruction(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/Object",
                    name,
                    descriptor,
                    false
                )
            )
        );
    }

    @Test
    void transformsToOpcodesWithoutArguments() {
        final String name = "toString";
        final String descriptor = "()Ljava/lang/String;";
        final Type type = Type.getType(String.class);
        MatcherAssert.assertThat(
            "Can't transform 'local1.toSting()' to correct opcodes",
            new OpcodeNodes(
                new Invocation(
                    new LocalVariable(1, type),
                    new Attributes().name(name).interfaced(false).descriptor(descriptor)
                )
            ).opcodes(),
            new HasInstructions(
                new HasInstructions.Instruction(Opcodes.ALOAD, 1),
                new HasInstructions.Instruction(
                    Opcodes.INVOKEVIRTUAL,
                    type.getClassName().replace('.', '/'),
                    name,
                    descriptor,
                    false
                )
            )
        );
    }
}
