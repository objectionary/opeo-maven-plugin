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

import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.Literal;
import org.eolang.opeo.decompilation.DecompilerState;
import org.eolang.opeo.decompilation.InstructionHandler;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Iconst instruction handler.
 * @since 0.1
 */
public final class ConstHandler implements InstructionHandler {

    /**
     * Type of constant.
     */
    private final Type type;

    /**
     * Constructor.
     * @param type Type of constant
     */
    ConstHandler(final Type type) {
        this.type = type;
    }

    @Override
    public void handle(final DecompilerState state) {
        final int opcode = state.instruction().opcode();
        final AstNode res;
        if (this.type.equals(Type.INT_TYPE)) {
            res = this.intConstant(opcode);
        } else if (this.type.equals(Type.LONG_TYPE)) {
            res = this.longConstant(opcode);
        } else if (this.type.equals(Type.FLOAT_TYPE)) {
            res = this.floatConstant(opcode);
        } else if (this.type.equals(Type.DOUBLE_TYPE)) {
            res = this.doubleConstant(opcode);
        } else {
            throw new UnsupportedOperationException(
                String.format("Type %s is not supported yet", this.type)
            );
        }
        state.stack().push(res);
    }

    /**
     * Create a constant node for double value.
     * @param opcode Opcode
     * @return Constant node
     */
    private AstNode doubleConstant(final int opcode) {
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
    private AstNode floatConstant(final int opcode) {
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
    private AstNode longConstant(final int opcode) {
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
    private AstNode intConstant(final int opcode) {
        final AstNode res;
        switch (opcode) {
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
