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
package org.eolang.opeo.decompilation.agents;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.eolang.opeo.ast.Return;
import org.eolang.opeo.decompilation.DecompilerState;
import org.eolang.opeo.decompilation.DecompilationAgent;
import org.eolang.opeo.decompilation.OperandStack;
import org.objectweb.asm.Opcodes;

/**
 * Return instruction handler.
 * @since 0.1
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 */
public final class ReturnAgent implements DecompilationAgent {

    private final Set<Integer> SUPPORTED = new HashSet<>(
        Arrays.asList(
            Opcodes.RETURN,
            Opcodes.IRETURN,
            Opcodes.LRETURN,
            Opcodes.FRETURN,
            Opcodes.DRETURN,
            Opcodes.ARETURN
        )
    );

    @Override
    public void handle(final DecompilerState state) {
        final int opcode = state.instruction().opcode();
        if (this.SUPPORTED.contains(opcode)) {
            final OperandStack stack = state.stack();
            if (opcode == Opcodes.RETURN) {
                stack.push(new Return());
            } else if (opcode == Opcodes.IRETURN) {
                stack.push(new Return(stack.pop()));
            } else if (opcode == Opcodes.LRETURN) {
                stack.push(new Return(stack.pop()));
            } else if (opcode == Opcodes.FRETURN) {
                stack.push(new Return(stack.pop()));
            } else if (opcode == Opcodes.DRETURN) {
                stack.push(new Return(stack.pop()));
            } else if (opcode == Opcodes.ARETURN) {
                stack.push(new Return(stack.pop()));
            } else {
                throw new IllegalStateException(
                    String.format("Unexpected opcode: %d", opcode)
                );
            }
        }
    }
}
