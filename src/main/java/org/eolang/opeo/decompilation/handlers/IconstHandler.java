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

import org.eolang.opeo.ast.Literal;
import org.eolang.opeo.decompilation.InstructionHandler;
import org.eolang.opeo.decompilation.MachineState;
import org.objectweb.asm.Opcodes;

/**
 * Iconst instruction handler.
 * @since 0.1
 */
public class IconstHandler implements InstructionHandler {

    @Override
    public void handle(final MachineState state) {
        switch (state.instruction().opcode()) {
            case Opcodes.ICONST_0:
                state.stack().push(new Literal(0));
                break;
            case Opcodes.ICONST_1:
                state.stack().push(new Literal(1));
                break;
            case Opcodes.ICONST_2:
                state.stack().push(new Literal(2));
                break;
            case Opcodes.ICONST_3:
                state.stack().push(new Literal(3));
                break;
            case Opcodes.ICONST_4:
                state.stack().push(new Literal(4));
                break;
            case Opcodes.ICONST_5:
                state.stack().push(new Literal(5));
                break;
            default:
                throw new UnsupportedOperationException(
                    String.format("Instruction %s is not supported yet", state.instruction())
                );
        }
    }

}
