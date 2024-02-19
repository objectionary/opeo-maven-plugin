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

import org.eolang.opeo.ast.Add;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.Attributes;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.decompilation.DecompilerState;
import org.eolang.opeo.decompilation.InstructionHandler;
import org.objectweb.asm.Opcodes;

/**
 * Add instruction handler.
 * @since 0.1
 */
public class AddHandler implements InstructionHandler {

    private final boolean counting;

    public AddHandler(final boolean counting) {
        this.counting = counting;
    }

    @Override
    public void handle(final DecompilerState state) {
        if (state.instruction().opcode() == Opcodes.IADD) {
            final AstNode right = state.stack().pop();
            final AstNode left = state.stack().pop();
            state.stack().push(new Add(left, right));
        } else if (state.instruction().opcode() == Opcodes.LADD) {
            final AstNode right = state.stack().pop();
            final AstNode left = state.stack().pop();
            state.stack().push(
                new Add(left, right, new Attributes().type("long"))
            );
        } else {
            state.stack().push(
                new Opcode(
                    state.instruction().opcode(),
                    state.instruction().operands(),
                    this.counting
                )
            );
        }
    }

}
