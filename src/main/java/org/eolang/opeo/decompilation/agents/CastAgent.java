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
import org.eolang.opeo.ast.Cast;
import org.eolang.opeo.decompilation.DecompilationAgent;
import org.eolang.opeo.decompilation.DecompilerState;
import org.objectweb.asm.Type;

/**
 * Cast instruction handler.
 * @since 0.2
 */
public final class CastAgent implements DecompilationAgent {

    /**
     * Supported opcodes.
     */
    private static final Set<Integer> SUPPROTED = new HashSet<>(
        Arrays.asList(
            org.objectweb.asm.Opcodes.I2B,
            org.objectweb.asm.Opcodes.I2C,
            org.objectweb.asm.Opcodes.I2S,
            org.objectweb.asm.Opcodes.I2L,
            org.objectweb.asm.Opcodes.I2F,
            org.objectweb.asm.Opcodes.I2D,
            org.objectweb.asm.Opcodes.L2I,
            org.objectweb.asm.Opcodes.L2F,
            org.objectweb.asm.Opcodes.L2D,
            org.objectweb.asm.Opcodes.F2I,
            org.objectweb.asm.Opcodes.F2L,
            org.objectweb.asm.Opcodes.F2D,
            org.objectweb.asm.Opcodes.D2I,
            org.objectweb.asm.Opcodes.D2L,
            org.objectweb.asm.Opcodes.D2F
        )
    );

    @Override
    public void handle(final DecompilerState state) {
        final int opcode = state.instruction().opcode();
        if (CastAgent.SUPPROTED.contains(opcode)) {
            state.stack().push(
                new Cast(
                    CastAgent.target(opcode),
                    state.stack().pop()
                )
            );
            state.decompileInstruction();
        }
    }

    /**
     * Target type.
     * @param opcode Opcode to handle.
     * @return Target type.
     */
    private static Type target(final int opcode) {
        final Type result;
        switch (opcode) {
            case org.objectweb.asm.Opcodes.I2B:
                result = Type.BYTE_TYPE;
                break;
            case org.objectweb.asm.Opcodes.I2C:
                result = Type.CHAR_TYPE;
                break;
            case org.objectweb.asm.Opcodes.I2S:
                result = Type.SHORT_TYPE;
                break;
            case org.objectweb.asm.Opcodes.I2L:
            case org.objectweb.asm.Opcodes.F2L:
            case org.objectweb.asm.Opcodes.D2L:
                result = Type.LONG_TYPE;
                break;
            case org.objectweb.asm.Opcodes.I2F:
            case org.objectweb.asm.Opcodes.L2F:
            case org.objectweb.asm.Opcodes.D2F:
                result = Type.FLOAT_TYPE;
                break;
            case org.objectweb.asm.Opcodes.I2D:
            case org.objectweb.asm.Opcodes.L2D:
            case org.objectweb.asm.Opcodes.F2D:
                result = Type.DOUBLE_TYPE;
                break;
            case org.objectweb.asm.Opcodes.L2I:
            case org.objectweb.asm.Opcodes.F2I:
            case org.objectweb.asm.Opcodes.D2I:
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
