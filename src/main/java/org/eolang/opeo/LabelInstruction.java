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
import org.eolang.jeo.representation.xmir.AllLabels;
import org.eolang.opeo.jeo.JeoLabel;
import org.objectweb.asm.Label;

/**
 * Label instruction.
 * @since 0.1
 * @todo #122:90min Remove code duplication between LabelInstruction and JeoLabel.
 *  LabelInstruction and JeoLabel are almost the same. We need to refactor them
 *  and remove the code duplication. Don't forget to remove this puzzle after
 *  refactoring. Alongside with the removing code duplication, we need to
 *  add unit tests for LabelInstruction class.
 */
public final class LabelInstruction implements Instruction {

    /**
     * Label.
     */
    private final Label label;

    /**
     * Constructor.
     * @param label Label.
     */
    public LabelInstruction(final Label label) {
        this.label = label;
    }

    @Override
    public int opcode() {
        return JeoLabel.LABEL_OPCODE;
    }

    @Override
    public Object operand(final int index) {
        return this.operands().get(index);
    }

    @Override
    public List<Object> operands() {
        return Collections.singletonList(new AllLabels().uid(this.label));
    }
}
