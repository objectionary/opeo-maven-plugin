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

import org.eolang.opeo.ast.Cast;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.decompilation.DecompilerState;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Cast instruction handler.
 * @since 0.2
 */
public final class CastAgent implements DecompilationAgent {

    /**
     * Supported opcodes.
     */
    private static final Supported OPCODES = new Supported(
        Opcodes.I2B,
        Opcodes.I2C,
        Opcodes.I2S,
        Opcodes.I2L,
        Opcodes.I2F,
        Opcodes.I2D,
        Opcodes.L2I,
        Opcodes.L2F,
        Opcodes.L2D,
        Opcodes.F2I,
        Opcodes.F2L,
        Opcodes.F2D,
        Opcodes.D2I,
        Opcodes.D2L,
        Opcodes.D2F
    );

    @Override
    public void handle(final DecompilerState state) {
        final Opcode instruction = state.current();
        if (this.supported().isSupported(instruction)) {
            state.stack().push(
                new Cast(
                    CastAgent.target(instruction.opcode()),
                    state.stack().pop()
                )
            );
            state.popInstruction();
        }
    }

    @Override
    public Supported supported() {
        return CastAgent.OPCODES;
    }

    /**
     * Target type.
     * @param opcode Opcode to handle.
     * @return Target type.
     * @checkstyle CyclomaticComplexityCheck (100 lines)
     */
    private static Type target(final int opcode) {
        final Type result;
        switch (opcode) {
            case Opcodes.I2B:
                result = Type.BYTE_TYPE;
                break;
            case Opcodes.I2C:
                result = Type.CHAR_TYPE;
                break;
            case Opcodes.I2S:
                result = Type.SHORT_TYPE;
                break;
            case Opcodes.I2L:
            case Opcodes.F2L:
            case Opcodes.D2L:
                result = Type.LONG_TYPE;
                break;
            case Opcodes.I2F:
            case Opcodes.L2F:
            case Opcodes.D2F:
                result = Type.FLOAT_TYPE;
                break;
            case Opcodes.I2D:
            case Opcodes.L2D:
            case Opcodes.F2D:
                result = Type.DOUBLE_TYPE;
                break;
            case Opcodes.L2I:
            case Opcodes.F2I:
            case Opcodes.D2I:
                result = Type.INT_TYPE;
                break;
            default:
                throw new IllegalArgumentException(
                    String.format(
                        "Unsupported opcode: %d",
                        opcode
                    )
                );
        }
        return result;
    }
}
