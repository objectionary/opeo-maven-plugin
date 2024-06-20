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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link org.eolang.opeo.LabelInstruction}.
 * @since 0.2
 */
final class LabelInstructionTest {

    @Test
    void retrievesCorrectOpcode() {
        MatcherAssert.assertThat(
            "Label instruction should have correct opcode",
            new LabelInstruction().opcode(),
            Matchers.equalTo(LabelInstruction.LABEL_OPCODE)
        );
    }

    @Test
    void retrievesOperand() {
        final String identifier = "expected";
        MatcherAssert.assertThat(
            "Label instruction should have correct operand and this operand is a label identifier",
            new LabelInstruction(identifier).operand(0),
            Matchers.equalTo(identifier)
        );
    }

    @Test
    void doesNotRetrieveUnknownOperand() {
        Assertions.assertThrows(
            IndexOutOfBoundsException.class,
            () -> new LabelInstruction("unexpected").operand(1),
            "Label instruction should not have unknown operand"
        );
    }

    @Test
    void retrievesAllOperands() {
        MatcherAssert.assertThat(
            "Label instruction should have exactly one operand",
            new LabelInstruction().operands(),
            Matchers.hasSize(1)
        );
    }
}
