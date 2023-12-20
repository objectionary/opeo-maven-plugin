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
package org.eolang.opeo;

import java.util.Collections;
import java.util.List;
import lombok.ToString;

/**
 * Instruction.
 * @since 0.1
 * @todo #8:90min Use real source of opcode instructions.
 *  Currently we use this class as a source of instructions and their operands.
 *  We can use jeo plugin as a source of instructions and their operands instead.
 */
@ToString
public final class Instruction {
    /**
     * Opcode index.
     */
    private final int opcode;

    /**
     * Operands.
     */
    private final List<Object> operands;

    /**
     * Constructor.
     * @param code Opcode index
     * @param args Operands
     */
    public Instruction(final int code, final Object... args) {
        this(code, List.of(args));
    }

    /**
     * Constructor.
     * @param code Opcode index
     * @param args Operands
     */
    private Instruction(final int code, final List<Object> args) {
        this.opcode = code;
        this.operands = args;
    }

    /**
     * Opcode index.
     * @return Opcode index
     */
    public int code() {
        return this.opcode;
    }

    /**
     * Operands.
     * @param index Operand index
     * @return Operand
     */
    public Object operand(final int index) {
        return this.operands.get(index);
    }

    /**
     * Operands.
     * @return Operands.
     */
    public List<Object> operands() {
        return Collections.unmodifiableList(this.operands);
    }
}
