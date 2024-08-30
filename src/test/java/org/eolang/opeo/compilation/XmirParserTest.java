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
package org.eolang.opeo.compilation;

import java.util.Collections;
import java.util.List;
import org.eolang.jeo.matchers.SameXml;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.ast.Addition;
import org.eolang.opeo.ast.Attributes;
import org.eolang.opeo.ast.Field;
import org.eolang.opeo.ast.FieldAssignment;
import org.eolang.opeo.ast.Constant;
import org.eolang.opeo.ast.LocalVariable;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.ast.This;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Tests for {@link XmirParser}.
 * @since 0.1
 */
final class XmirParserTest {

    @Test
    void convertsOpcodesAsIs() {
        final List<XmlNode> nodes = new XmirParser(
            new Opcode(Opcodes.ICONST_0, false), new Opcode(Opcodes.POP, false)
        ).toJeoNodes();
        MatcherAssert.assertThat(
            "We expect to retrieve 2 opcodes, but got something else instead: %n%s%n",
            nodes,
            Matchers.hasSize(2)
        );
        MatcherAssert.assertThat(
            "We expect to have specific opcodes in right order as is",
            nodes,
            new HasInstructions(
                Opcodes.ICONST_0,
                Opcodes.POP
            )
        );
    }

    @Test
    void convertsAddition() {
        final List<XmlNode> nodes = new XmirParser(
            new Addition(new Constant(1), new Constant(2))
        ).toJeoNodes();
        MatcherAssert.assertThat(
            String.format(
                "We expect to retrieve 3 opcodes, but got something else instead: %n%s%n",
                nodes
            ),
            nodes,
            Matchers.hasSize(3)
        );
        MatcherAssert.assertThat(
            "We expect to have specific opcodes in right order",
            nodes,
            new HasInstructions(
                Opcodes.ICONST_1,
                Opcodes.ICONST_2,
                Opcodes.IADD
            )
        );
    }

    @Test
    void convertsDeepAddition() {
        MatcherAssert.assertThat(
            "We expect to retrieve 7 opcodes, but got something else instead",
            new XmirParser(
                new Addition(
                    new Addition(
                        new Constant(1),
                        new Constant(2)
                    ),
                    new Addition(
                        new Constant(3),
                        new Constant(4)
                    )
                )
            ).toJeoNodes(),
            new HasInstructions(
                Opcodes.ICONST_1,
                Opcodes.ICONST_2,
                Opcodes.IADD,
                Opcodes.ICONST_3,
                Opcodes.ICONST_4,
                Opcodes.IADD,
                Opcodes.IADD
            )
        );
    }

    @Test
    void convertsFieldAssignment() {
        final String owner = "org/eolang/opeo/ast/LocalVariables";
        final String name = "d";
        final String dscr = "I";
        final Attributes attrs = new Attributes()
            .name(name)
            .descriptor(dscr)
            .owner(owner);
        MatcherAssert.assertThat(
            "We expect to retrieve exactly 3 opcodes aload, iload and putfield, but got something else instead",
            new XmirParser(
                new FieldAssignment(
                    new Field(new This(), attrs),
                    new LocalVariable(1, Type.INT_TYPE)
                )
            ).toJeoNodes(),
            new HasInstructions(
                new HasInstructions.Instruction(Opcodes.ALOAD, 0),
                new HasInstructions.Instruction(Opcodes.ILOAD, 1),
                new HasInstructions.Instruction(Opcodes.PUTFIELD, owner, name, dscr)
            )
        );
    }

    @Test
    void parsesInvokeDynamicInstruction() {
        Opcode.disableCounting();
        final XmlNode node = new XmlNode(
            String.join(
                "\n",
                "<o base='opcode' line='999' name='invokedynamic'>",
                "  <o base='int' data='bytes'>00 00 00 00 00 00 00 BA</o>",
                "  <o base='string' data='bytes'>61 70 70 6C 79 41 73 49 6E 74</o>",
                "  <o base='string' data='bytes'>28 29 4C 6A 61 76 61 2F 75 74 69 6C 2F 66 75 6E 63 74 69 6F 6E 2F 54 6F 49 6E 74 46 75 6E 63 74 69 6F 6E 3B</o>",
                "  <o base='handle'>",
                "    <o base='int' data='bytes'>00 00 00 00 00 00 00 06</o>",
                "    <o base='string' data='bytes'>6A 61 76 61 2F 6C 61 6E 67 2F 69 6E 76 6F 6B 65 2F 4C 61 6D 62 64 61 4D 65 74 61 66 61 63 74 6F 72 79</o>",
                "    <o base='string' data='bytes'>6D 65 74 61 66 61 63 74 6F 72 79</o>",
                "    <o base='string' data='bytes'>28 4C 6A 61 76 61 2F 6C 61 6E 67 2F 69 6E 76 6F 6B 65 2F 4D 65 74 68 6F 64 48 61 6E 64 6C 65 73 24 4C 6F 6F 6B 75 70 3B 4C 6A 61 76 61 2F 6C 61 6E 67 2F 53 74 72 69 6E 67 3B 4C 6A 61 76 61 2F 6C 61 6E 67 2F 69 6E 76 6F 6B 65 2F 4D 65 74 68 6F 64 54 79 70 65 3B 4C 6A 61 76 61 2F 6C 61 6E 67 2F 69 6E 76 6F 6B 65 2F 4D 65 74 68 6F 64 54 79 70 65 3B 4C 6A 61 76 61 2F 6C 61 6E 67 2F 69 6E 76 6F 6B 65 2F 4D 65 74 68 6F 64 48 61 6E 64 6C 65 3B 4C 6A 61 76 61 2F 6C 61 6E 67 2F 69 6E 76 6F 6B 65 2F 4D 65 74 68 6F 64 54 79 70 65 3B 29 4C 6A 61 76 61 2F 6C 61 6E 67 2F 69 6E 76 6F 6B 65 2F 43 61 6C 6C 53 69 74 65 3B</o>",
                "    <o base='bool' data='bytes'>00</o>",
                "  </o>",
                "  <o base='type' data='bytes'>28 4C 6A 61 76 61 2F 6C 61 6E 67 2F 4F 62 6A 65 63 74 3B 29 49</o>",
                "  <o base='handle'>",
                "    <o base='int' data='bytes'>00 00 00 00 00 00 00 06</o>",
                "    <o base='string' data='bytes'>6F 72 67 2F 6A 75 6E 69 74 2F 6A 75 70 69 74 65 72 2F 61 70 69 2F 43 6C 61 73 73 4F 72 64 65 72 65 72 24 4F 72 64 65 72 41 6E 6E 6F 74 61 74 69 6F 6E</o>",
                "    <o base='string' data='bytes'>67 65 74 4F 72 64 65 72</o>",
                "    <o base='string' data='bytes'>28 4C 6F 72 67 2F 6A 75 6E 69 74 2F 6A 75 70 69 74 65 72 2F 61 70 69 2F 43 6C 61 73 73 44 65 73 63 72 69 70 74 6F 72 3B 29 49</o>",
                "    <o base='bool' data='bytes'>00</o>",
                "  </o>",
                "  <o base='type' data='bytes'>28 4C 6F 72 67 2F 6A 75 6E 69 74 2F 6A 75 70 69 74 65 72 2F 61 70 69 2F 43 6C 61 73 73 44 65 73 63 72 69 70 74 6F 72 3B 29 49</o>",
                " </o>"
            )
        );
        MatcherAssert.assertThat(
            "We expect to retrieve exactly 1 opcode invokedynamic (without changes), but got something else instead",
            new XmirParser(Collections.singletonList(node)).toJeoNodes().get(0).toString(),
            new SameXml(node.toString())
        );
    }
}
