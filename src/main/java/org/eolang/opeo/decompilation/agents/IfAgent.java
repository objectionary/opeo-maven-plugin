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

import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.If;
import org.eolang.opeo.decompilation.DecompilerState;
import org.eolang.opeo.decompilation.OperandStack;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

/**
 * If instruction handler.
 * [value1, value2] → []
 * If value1 is greater than value2, branch to instruction at branchoffset
 * (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
 * @since 0.2
 */
public final class IfAgent implements DecompilationAgent {

    /**
     * Supported opcodes.
     */
    private static final Supported OPCODES = new Supported(Opcodes.IF_ICMPGT);

    @Override
    public boolean appropriate(final DecompilerState state) {
        return new OpcodesAgent(this).appropriate(state);
    }

    @Override
    public Supported supported() {
        return IfAgent.OPCODES;
    }

    @Override
    public void handle(final DecompilerState state) {
        if (this.appropriate(state)) {
            final OperandStack stack = state.stack();
            final AstNode second = stack.pop();
            final AstNode first = stack.pop();
            final Label operand = (Label) state.operand(0);
            stack.push(new If(first, second, operand));
            state.popInstruction();
        } else {
            throw new IllegalAgentException(this, state);
        }
    }
}
