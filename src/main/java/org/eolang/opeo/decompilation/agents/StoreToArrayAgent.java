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
import org.eolang.opeo.ast.StoreArray;
import org.eolang.opeo.decompilation.DecompilationAgent;
import org.eolang.opeo.decompilation.DecompilerState;

/**
 * Store to array instruction handler.
 * Store a reference in an array
 * Opcodes: aastore
 * Stack [before]->[after]: "arrayref, index, value →"
 * @since 0.1
 * @todo: Conflict between {@link StoreToArrayAgent} and {@link DupAgent}.
 *  We have a strange conflict between these two agents.
 *  - {@link StoreToArrayAgent} is supposed to leave stack empty, or at least do not push anything,
 *  but it pushes a new {@link StoreArray} object. Which is wrong.
 *  - {@link DupAgent} is supposed to push a new object, but it does not push anything.
 *  However both this bugs compensate each other somehow and our transformations work
 *  as expected. But this is not a good practice. We should adhere to bytecode specification for
 *  both agents: {@link DupAgent} should push a new object and {@link StoreToArrayAgent} should
 *  not push anything.
 */
public final class StoreToArrayAgent implements DecompilationAgent {

    /**
     * Supported opcodes.
     */
    private static final Set<Integer> SUPPORTED = new HashSet<>(
        Arrays.asList(
            org.objectweb.asm.Opcodes.AASTORE
        )
    );

    @Override
    public void handle(final DecompilerState state) {
        if (StoreToArrayAgent.SUPPORTED.contains(state.instruction().opcode())) {
            final AstNode value = state.stack().pop();
            final AstNode index = state.stack().pop();
            final AstNode array = state.stack().pop();
            state.stack().push(new StoreArray(array, index, value));
            state.popInstruction();
        }
    }
}
