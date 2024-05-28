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
package org.eolang.opeo.decompilation.handlers;

import org.eolang.opeo.decompilation.DecompilerState;
import org.eolang.opeo.decompilation.InstructionHandler;
import org.objectweb.asm.Type;

/**
 * Instruction handler.
 * This handler might understand the following instructions:
 * aload: 1: index | → objectref | load a reference onto the stack from a local variable #index
 * aload_0: → objectref | load a reference onto the stack from local variable 0
 * ...
 * @since 0.1
 */
public final class LoadHandler implements InstructionHandler {

    /**
     * Type of the variable.
     */
    private final Type type;

    /**
     * Constructor.
     * @param type Type of the variable.
     */
    public LoadHandler(final Type type) {
        this.type = type;
    }

    @Override
    public void handle(final DecompilerState state) {
        final Integer index = (Integer) state.operand(0);
        state.stack().push(
            state.variable(index, this.type)
        );
    }
}
