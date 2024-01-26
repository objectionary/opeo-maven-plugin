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
package org.eolang.opeo.ast;

import java.util.List;
import org.eolang.opeo.compilation.HasInstructions;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link ArrayConstructor}.
 * @since 0.1
 */
class ArrayConstructorTest {

    @Test
    void compilesSimpleArrayCreation() {
        final int size = 10;
        final String type = "java/lang/Integer";
        final ArrayConstructor constructor = new ArrayConstructor(new Literal(size), type);
        MatcherAssert.assertThat(
            new OpcodeNodes(constructor).opcodes(),
            new HasInstructions(
                new HasInstructions.Instruction(Opcodes.BIPUSH, size),
                new HasInstructions.Instruction(Opcodes.ANEWARRAY, type),
                new HasInstructions.Instruction(Opcodes.DUP)
            )
        );
    }

    @Test
    void compilesArrayWithComplexLength() {
        final String type = "java/lang/Integer";
        final ArrayConstructor constructor = new ArrayConstructor(
            new Add(new Literal(1), new Literal(2)),
            type
        );
        MatcherAssert.assertThat(
            new OpcodeNodes(constructor).opcodes(),
            new HasInstructions(
                new HasInstructions.Instruction(Opcodes.ICONST_1),
                new HasInstructions.Instruction(Opcodes.ICONST_2),
                new HasInstructions.Instruction(Opcodes.IADD),
                new HasInstructions.Instruction(Opcodes.ANEWARRAY, type),
                new HasInstructions.Instruction(Opcodes.DUP)
            )
        );
    }

}