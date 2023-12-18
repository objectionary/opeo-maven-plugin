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

import org.eolang.opeo.Instruction;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link DecompilerMachine}.
 * @since 0.1
 */
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
                new Instruction(Opcodes.NEW, "B"),
                new Instruction(Opcodes.DUP),
                new Instruction(Opcodes.NEW, "A"),
                new Instruction(Opcodes.DUP),
                new Instruction(Opcodes.BIPUSH, 42),
                new Instruction(Opcodes.INVOKESPECIAL, "A", "<init>", "(I)V"),
                new Instruction(Opcodes.INVOKESPECIAL, "B", "<init>", "(LA;)V"),
                new Instruction(Opcodes.POP),
                new Instruction(Opcodes.RETURN)
            ),
            Matchers.equalTo(
                "B.new (A.new (42))\nreturn"
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
                new Instruction(Opcodes.NEW, "D"),
                new Instruction(Opcodes.DUP),
                new Instruction(Opcodes.BIPUSH, 45),
                new Instruction(Opcodes.BIPUSH, 44),
                new Instruction(Opcodes.NEW, "C"),
                new Instruction(Opcodes.DUP),
                new Instruction(Opcodes.BIPUSH, 43),
                new Instruction(Opcodes.INVOKESPECIAL, "C", "<init>", "(I)V"),
                new Instruction(Opcodes.INVOKESPECIAL, "D", "<init>", "(LC;II)V"),
                new Instruction(Opcodes.POP),
                new Instruction(Opcodes.RETURN)
            ),
            Matchers.equalTo(
                "D.new (C.new (43)) (44) (45)\nreturn"
            )
        );
    }
}
