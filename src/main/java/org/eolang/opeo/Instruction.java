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

import java.util.List;
import org.objectweb.asm.Opcodes;

/**
 * Instruction abstraction.
 * @since 0.1
 */
public interface Instruction {

    /**
     * Opcode number.
     * @return Opcode number.
     */
    int opcode();

    /**
     * Retrieve operand by position index.
     * @param index Operand index
     * @return Operand
     */
    Object operand(int index);

    /**
     * Full list of operands.
     * @return Operands.
     */
    List<Object> operands();

    /**
     * Not an operation instruction.
     * Stub class that is useful for some cases.
     * @since 0.2
     */
    final class Nop implements Instruction {

        @Override
        public int opcode() {
            return Opcodes.NOP;
        }

        @Override
        public Object operand(final int index) {
            throw new UnsupportedOperationException(
                String.format("NOP instruction doesn't have %d operand", index)
            );
        }

        @Override
        public List<Object> operands() {
            throw new UnsupportedOperationException("NOP instruction doesn't have operands");
        }
    }
}
