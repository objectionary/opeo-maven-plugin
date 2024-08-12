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
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.CheckCast;
import org.eolang.opeo.decompilation.DecompilationAgent;
import org.eolang.opeo.decompilation.DecompilerState;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * CheckCast instruction handler.
 *
 * @since 0.5
 */
public final class CheckCastAgent implements DecompilationAgent {

    /**
     * Supported opcodes.
     */
    private static final Set<Integer> SUPPORTED = new HashSet<>(
        Arrays.asList(
            Opcodes.CHECKCAST
        )
    );

    @Override
    public void handle(final DecompilerState state) {
        if (CheckCastAgent.SUPPORTED.contains(state.instruction().opcode())) {
            final AstNode value = state.stack().pop();
            final Object type = state.operand(0);
            state.stack().push(new CheckCast(Type.getObjectType((String) type), value));
            state.popInstruction();
        }
    }
}