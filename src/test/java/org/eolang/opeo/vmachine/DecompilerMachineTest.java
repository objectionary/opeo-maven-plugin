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
package org.eolang.opeo.vmachine;

import org.eolang.opeo.OpcodeInstruction;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link DecompilerMachine}.
 * @since 0.1
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class DecompilerMachineTest {

    /**
     * Test decompilation of new instructions.
     * <p>
     *     {@code
     *       new B(new A(42));
     *     }
     * </p>
     */
    @Test
    void decompilesNewInstructions() {
        MatcherAssert.assertThat(
            "Can't decompile bytecode instructions for 'new B(new A(42));'",
            new DecompilerMachine().decompile(
                new OpcodeInstruction(Opcodes.NEW, "B"),
                new OpcodeInstruction(Opcodes.DUP),
                new OpcodeInstruction(Opcodes.NEW, "A"),
                new OpcodeInstruction(Opcodes.DUP),
                new OpcodeInstruction(Opcodes.BIPUSH, 42),
                new OpcodeInstruction(Opcodes.INVOKESPECIAL, "A", "<init>", "(I)V"),
                new OpcodeInstruction(Opcodes.INVOKESPECIAL, "B", "<init>", "(LA;)V"),
                new OpcodeInstruction(Opcodes.POP),
                new OpcodeInstruction(Opcodes.RETURN)
            ),
            Matchers.allOf(
                Matchers.containsString("B.new (A.new (42))"),
                Matchers.containsString("opcode > RETURN")
            )
        );
    }

    /**
     * Test decompilation of new instructions.
     * <p>
     *     {@code
     *       new D(new C(43), 44, 45);
     *     }
     * </p>
     */
    @Test
    void decompilesNewInstructionsEachWithParam() {
        MatcherAssert.assertThat(
            "Can't decompile new instructions for 'new D(new C(43), 44, 45);'",
            new DecompilerMachine().decompile(
                new OpcodeInstruction(Opcodes.NEW, "D"),
                new OpcodeInstruction(Opcodes.DUP),
                new OpcodeInstruction(Opcodes.BIPUSH, 45),
                new OpcodeInstruction(Opcodes.BIPUSH, 44),
                new OpcodeInstruction(Opcodes.NEW, "C"),
                new OpcodeInstruction(Opcodes.DUP),
                new OpcodeInstruction(Opcodes.BIPUSH, 43),
                new OpcodeInstruction(Opcodes.INVOKESPECIAL, "C", "<init>", "(I)V"),
                new OpcodeInstruction(Opcodes.INVOKESPECIAL, "D", "<init>", "(LC;II)V"),
                new OpcodeInstruction(Opcodes.POP),
                new OpcodeInstruction(Opcodes.RETURN)
            ),
            Matchers.allOf(
                Matchers.containsString("D.new (C.new (43)) (44) (45)"),
                Matchers.containsString("opcode > RETURN")
            )
        );
    }

    /**
     * Test decompilation of instance call instructions.
     * <p>
     *     {@code
     *       new A().bar();
     *     }
     * </p>
     */
    @Test
    void decompilesSimpleInstanceCall() {
        MatcherAssert.assertThat(
            "Can't decompile method call instructions for 'new A().bar();'",
            new DecompilerMachine().decompile(
                new OpcodeInstruction(Opcodes.NEW, "A"),
                new OpcodeInstruction(Opcodes.DUP),
                new OpcodeInstruction(Opcodes.INVOKESPECIAL, "A", "<init>", "()V"),
                new OpcodeInstruction(Opcodes.INVOKEVIRTUAL, "A", "bar", "()V")
            ),
            Matchers.equalTo(
                "(A.new).bar"
            )
        );
    }

    /**
     * Test decompilation of instance call instructions with arguments.
     * <p>
     *     {@code
     *       new A(28).bar(29);
     *     }
     * </p>
     */
    @Test
    void decompilesInstanceCallWithArguments() {
        MatcherAssert.assertThat(
            "Can't decompile method call instructions for 'new A(28).bar(29);'",
            new DecompilerMachine().decompile(
                new OpcodeInstruction(Opcodes.NEW, "A"),
                new OpcodeInstruction(Opcodes.DUP),
                new OpcodeInstruction(Opcodes.BIPUSH, 28),
                new OpcodeInstruction(Opcodes.INVOKESPECIAL, "A", "<init>", "(I)V"),
                new OpcodeInstruction(Opcodes.BIPUSH, 29),
                new OpcodeInstruction(Opcodes.INVOKEVIRTUAL, "A", "bar", "(I)V")
            ),
            Matchers.equalTo(
                "(A.new (28)).bar 29"
            )
        );
    }

    /**
     * Test decompilation of instance call instructions with arguments.
     * <p>
     *     {@code
     *       new StringBuilder('a').append('b');
     *     }
     * </p>
     */
    @Test
    void decompilesStringBuilder() {
        MatcherAssert.assertThat(
            "Can't decompile StringBuilder instructions for 'new StringBuilder('a').append('b');",
            new DecompilerMachine().decompile(
                new OpcodeInstruction(Opcodes.NEW, "java/lang/StringBuilder"),
                new OpcodeInstruction(Opcodes.DUP),
                new OpcodeInstruction(Opcodes.LDC, "a"),
                new OpcodeInstruction(
                    Opcodes.INVOKESPECIAL,
                    "java/lang/StringBuilder",
                    "<init>",
                    "(Ljava/lang/String;)V"
                ),
                new OpcodeInstruction(Opcodes.LDC, "b"),
                new OpcodeInstruction(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(Ljava/lang/String;)Ljava/lang/StringBuilder"
                )
            ),
            Matchers.equalTo(
                "(java/lang/StringBuilder.new (\"a\")).append \"b\""
            )
        );
    }
}