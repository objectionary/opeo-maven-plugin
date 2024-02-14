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

import java.util.List;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.ast.Add;
import org.eolang.opeo.ast.Attributes;
import org.eolang.opeo.ast.FieldAssignment;
import org.eolang.opeo.ast.InstanceField;
import org.eolang.opeo.ast.Literal;
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
            new Add(new Literal(1), new Literal(2))
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
                new Add(
                    new Add(
                        new Literal(1),
                        new Literal(2)
                    ),
                    new Add(
                        new Literal(3),
                        new Literal(4)
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
            new XmirParser(
                new FieldAssignment(
                    new InstanceField(new This(), attrs),
                    new LocalVariable(1, Type.INT_TYPE),
                    attrs
                )
            ).toJeoNodes(),
            new HasInstructions(
                new HasInstructions.Instruction(Opcodes.ALOAD, 0),
                new HasInstructions.Instruction(Opcodes.ILOAD, 1),
                new HasInstructions.Instruction(Opcodes.PUTFIELD, owner, name, dscr)
            )
        );
    }
}
