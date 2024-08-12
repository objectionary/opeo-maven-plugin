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
import org.eolang.opeo.SelectiveDecompiler;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.Literal;
import org.eolang.opeo.decompilation.DecompilerState;
import org.eolang.opeo.decompilation.DecompilationAgent;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Iconst instruction handler.
 * @since 0.1
 */
public final class ConstAgent implements DecompilationAgent {

    private final Set<Integer> SUPPORTED = new HashSet<>(
        Arrays.asList(
            Opcodes.ICONST_M1,
            Opcodes.ICONST_0,
            Opcodes.ICONST_1,
            Opcodes.ICONST_2,
            Opcodes.ICONST_3,
            Opcodes.ICONST_4,
            Opcodes.ICONST_5,
            Opcodes.LCONST_0,
            Opcodes.LCONST_1,
            Opcodes.FCONST_0,
            Opcodes.FCONST_1,
            Opcodes.FCONST_2,
            Opcodes.DCONST_0,
            Opcodes.DCONST_1
        )
    );

    @Override
    public void handle(final DecompilerState state) {
        final int opcode = state.instruction().opcode();
        if (this.SUPPORTED.contains(opcode)) {
            final AstNode res;
            switch (opcode) {
                case Opcodes.ICONST_M1:
                case Opcodes.ICONST_0:
                case Opcodes.ICONST_1:
                case Opcodes.ICONST_2:
                case Opcodes.ICONST_3:
                case Opcodes.ICONST_4:
                case Opcodes.ICONST_5:
                    res = ConstAgent.intConstant(opcode);
                    state.stack().push(res);
                    break;
                case Opcodes.LCONST_0:
                case Opcodes.LCONST_1:
                    res = ConstAgent.longConstant(opcode);
                    state.stack().push(res);
                    break;
                case Opcodes.FCONST_0:
                case Opcodes.FCONST_1:
                case Opcodes.FCONST_2:
                    res = ConstAgent.floatConstant(opcode);
                    state.stack().push(res);
                    break;
                case Opcodes.DCONST_0:
                case Opcodes.DCONST_1:
                    res = ConstAgent.doubleConstant(opcode);
                    state.stack().push(res);
                    break;
            }
            state.move();
        }

    }

    /**
     * Create a constant node for double value.
     * @param opcode Opcode
     * @return Constant node
     */
    private static AstNode doubleConstant(final int opcode) {
        final AstNode res;
        switch (opcode) {
            case Opcodes.DCONST_0:
                res = new Literal(0.0);
                break;
            case Opcodes.DCONST_1:
                res = new Literal(1.0);
                break;
            default:
                throw new UnsupportedOperationException(
                    String.format(
                        "Double constant handler for opcode %s is not supported yet",
                        opcode
                    )
                );
        }
        return res;
    }

    /**
     * Create a constant node for float value.
     * @param opcode Opcode
     * @return Constant node
     */
    private static AstNode floatConstant(final int opcode) {
        final AstNode res;
        switch (opcode) {
            case Opcodes.FCONST_0:
                res = new Literal(0.0f);
                break;
            case Opcodes.FCONST_1:
                res = new Literal(1.0f);
                break;
            case Opcodes.FCONST_2:
                res = new Literal(2.0f);
                break;
            default:
                throw new UnsupportedOperationException(
                    String.format(
                        "Float constant handler for opcode %s is not supported yet",
                        opcode
                    )
                );
        }
        return res;
    }

    /**
     * Create a constant node for long value.
     * @param opcode Opcode
     * @return Constant node
     */
    private static AstNode longConstant(final int opcode) {
        final AstNode res;
        switch (opcode) {
            case Opcodes.LCONST_0:
                res = new Literal(0L);
                break;
            case Opcodes.LCONST_1:
                res = new Literal(1L);
                break;
            default:
                throw new UnsupportedOperationException(
                    String.format(
                        "Long constant handler for opcode %s is not supported yet",
                        opcode
                    )
                );
        }
        return res;
    }

    /**
     * Create a constant node for int value.
     * @param opcode Opcode
     * @return Constant node
     */
    private static AstNode intConstant(final int opcode) {
        final AstNode res;
        switch (opcode) {
            case Opcodes.ICONST_M1:
                res = new Literal(-1);
                break;
            case Opcodes.ICONST_0:
                res = new Literal(0);
                break;
            case Opcodes.ICONST_1:
                res = new Literal(1);
                break;
            case Opcodes.ICONST_2:
                res = new Literal(2);
                break;
            case Opcodes.ICONST_3:
                res = new Literal(3);
                break;
            case Opcodes.ICONST_4:
                res = new Literal(4);
                break;
            case Opcodes.ICONST_5:
                res = new Literal(5);
                break;
            default:
                throw new UnsupportedOperationException(
                    String.format(
                        "Int constant handler for opcode %s is not supported yet",
                        opcode
                    )
                );
        }
        return res;
    }
}
