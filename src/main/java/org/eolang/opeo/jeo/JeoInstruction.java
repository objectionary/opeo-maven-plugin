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
package org.eolang.opeo.jeo;

import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.xmir.XmlInstruction;
import org.eolang.jeo.representation.xmir.XmlOperand;
import org.eolang.opeo.Instruction;

/**
 * Class that represents the instruction provided by jeo maven plugin.
 * @since 0.1
 */
public final class JeoInstruction implements Instruction {

    /**
     * Jeo instruction.
     */
    private final XmlInstruction instruction;

    /**
     * Constructor.
     * @param instruction Instruction.
     */
    public JeoInstruction(final XmlInstruction instruction) {
        this.instruction = instruction;
    }

    @Override
    public int opcode() {
        return this.instruction.opcode();
    }

    @Override
    public Object operand(final int index) {
        return this.operands().get(index);
    }

    @Override
    public List<Object> operands() {
        return this.instruction.operands()
            .stream()
            .map(XmlOperand::asObject)
            .collect(Collectors.toList());
    }
}
