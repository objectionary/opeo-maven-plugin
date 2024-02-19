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
package org.eolang.opeo.decompilation;

import org.eolang.opeo.Instruction;
import org.eolang.opeo.ast.AstNode;
import org.objectweb.asm.Type;

public final class DecompilerState {

    /**
     * Current instruction.
     */
    private final Instruction current;

    /**
     * Current operand stack.
     * It might have some values inside.
     */
    private final OperandStack operands;

    /**
     * Method local variables.
     */
    private final LocalVariables vars;

    /**
     * Constructor.
     * @param vars Method local variables.
     */
    public DecompilerState(final LocalVariables vars) {
        this(new Instruction.Nop(), new OperandStack(), vars);
    }

    /**
     * Constructor.
     * @param current Current instruction.
     * @param stack Operand stack.
     * @param variables Method local variables.
     */
    private DecompilerState(
        final Instruction current,
        final OperandStack stack,
        final LocalVariables variables
    ) {
        this.current = current;
        this.operands = stack;
        this.vars = variables;
    }

    /**
     * Retrieve current bytecode instruction.
     * @return Current bytecode instruction.
     */
    public Instruction instruction() {
        return this.current;
    }

    /**
     * Instruction operand.
     * @param index Operand index.
     * @return Instruction operand.
     */
    public Object operand(final int index) {
        return this.current.operand(index);
    }

    /**
     * Retrieve variable by index and type.
     * @param index Variable index.
     * @param type Varaiable type.
     * @return Variable node.
     */
    public AstNode variable(final int index, final Type type) {
        return this.vars.variable(index, type);
    }

    /**
     * Retrieve current stack.
     * @return Operand stack.
     */
    public OperandStack stack() {
        return this.operands;
    }

    /**
     * Move the state to the next instruction.
     * @param instruction Next instruction.
     * @return New decompiler state with the next instruction.
     */
    DecompilerState next(final Instruction instruction) {
        return new DecompilerState(instruction, this.operands, this.vars);
    }
}
