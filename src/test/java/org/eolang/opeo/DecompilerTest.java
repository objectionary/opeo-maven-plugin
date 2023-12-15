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
package org.eolang.opeo;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link Decompiler}.
 * @since 0.1
 */
final class DecompilerTest {

    /**
     * Test decompilation of new instructions.
     * <p>
     *     {@code
     *       new B(new A(42));
     *     }
     * </p>
     */
    @Test
    public void decompilesNewInstructions() {
        MatcherAssert.assertThat(
            "Can't decompile new instructions",
            new Decompiler().decompile(
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
                "B.new (A.new 42);\nreturn;\n"
            )
        );
    }


}
