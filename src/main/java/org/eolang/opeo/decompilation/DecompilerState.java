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

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.Opcode;
import org.objectweb.asm.Type;

/**
 * Internal state of decompiler.
 * This state changes all the time between instruction handlers.
 * @since 0.2
 */
@ToString
@EqualsAndHashCode
public final class DecompilerState {

    private final Deque<Opcode> opcodes;

    /**
     * Current operand stack.
     * It might have some values inside.
     */
    private final OperandStack stack;

    /**
     * Method local variables.
     */
    private final LocalVariables vars;

    /**
     * Constructor.
     * @param vars Method local variables.
     */
    public DecompilerState(final LocalVariables vars) {
        this(new OperandStack(), vars);
    }

    /**
     * Constructor.
     * @param operands Operand stack.
     * @param vars Method local variables.
     */
    public DecompilerState(final OperandStack operands, final LocalVariables vars) {
        this(new LinkedList<>(), operands, vars);
    }

    public DecompilerState(
        final Deque<Opcode> opcodes,
        final OperandStack stack,
        final LocalVariables vars
    ) {
        this.opcodes = opcodes;
        this.stack = stack;
        this.vars = vars;
    }

    public boolean hasInstructions() {
        return !this.opcodes.isEmpty();
    }

    public void move() {
        this.opcodes.pop();
    }

    /**
     * Retrieve current bytecode instruction.
     * @return Current bytecode instruction.
     */
    public Opcode instruction() {
        return this.opcodes.peek();
//        final Iterator<AstNode> iterator = this.stack().iterator();
//        while (iterator.hasNext()) {
//            final AstNode next = iterator.next();
//            if (next instanceof Opcode) {
//                return (Opcode) next;
//            }
//        }
//        throw new IllegalStateException(
//            "No opcode found in the stack"
//        );
//        return (Opcode) this.stack().peek();
    }

    /**
     * Instruction operand.
     * @param index Operand index.
     * @return Instruction operand.
     */
    public Object operand(final int index) {
        return this.instruction().operand(index);
//        if (this.current.operands().size() <= index) {
//            throw new IllegalStateException(
//                String.format(
//                    "Instruction '%s' doesn't have operand at index '%d'",
//                    this.current,
//                    index
//                )
//            );
//        }
//        return this.current.operand(index);
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
        return this.stack;
    }
}
