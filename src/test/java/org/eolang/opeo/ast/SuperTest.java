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
import org.eolang.opeo.compilation.HasInstructions;
import org.hamcrest.MatcherAssert;
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

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        final String xmir = new Xembler(new Super(new This(), new Literal(1)).toXmir()).xml();
        MatcherAssert.assertThat(
            String.format(
                "Can't convert 'super' statement to XMIR, result is wrong: %n%s%n",
                xmir
            ),
            xmir,
            XhtmlMatchers.hasXPaths(
                "./o[@base='.super']",
                "./o[@base='.super' and @scope='()V']",
                "./o[@base='.super']/o[@base='$']",
                "./o[@base='.super']/o[@base='int' and contains(text(), '1')]"
            )
        );
    }

    @Test
    void convertsToXmirWithCustomDescriptor() throws ImpossibleModificationException {
        final String xmir = new Xembler(
            new Super(new This(), "(I)V", new Literal(10)).toXmir()
        ).xml();
        MatcherAssert.assertThat(
            String.format(
                "Can't convert 'super' statement to XMIR, result is wrong: %n%s%n",
                xmir
            ),
            xmir,
            XhtmlMatchers.hasXPaths("./o[@base='.super' and @scope='(I)V']")
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
                    "(I)V"
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
                    "()V"
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
                    "(II)V"
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
                    "()V"
                )
            )
        );
    }
}
