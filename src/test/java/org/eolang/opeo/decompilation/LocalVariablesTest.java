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

import org.eolang.opeo.ast.LocalVariable;
import org.eolang.opeo.ast.This;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Test for {@link LocalVariables}.
 * @since 0.1
 */
class LocalVariablesTest {

    @Test
    void returnsVariable() {
        final int index = 1;
        final Type type = Type.INT_TYPE;
        MatcherAssert.assertThat(
            "Local variables should return correct variable by index",
            new LocalVariables().variable(index, type),
            Matchers.equalTo(
                new LocalVariable(index, type)
            )
        );
    }

    @Test
    void returnsThisForInstanceMethod() {
        final int index = 0;
        final Type type = Type.getType("Lorg/eolang/benchmark/A;");
        MatcherAssert.assertThat(
            "Since it is local variables in an instance method, the first variable is always `this`",
            new LocalVariables(Opcodes.ACC_PUBLIC, "()V").variable(index, type),
            Matchers.equalTo(new This(type))
        );
    }

    @Test
    void returnsArgumentWithTypeFromDescriptor() {
        final int index = 1;
        MatcherAssert.assertThat(
            "Since it is local variable in an instance method, it should have 1 index, not 0. Moreover since we have a descriptor `(J)V`, the type should be `long` despite the fact that the passing type is `short`",
            new LocalVariables(Opcodes.ACC_PUBLIC, "(J)V").variable(index, Type.SHORT_TYPE),
            Matchers.equalTo(new LocalVariable(index, Type.LONG_TYPE))
        );
    }

    @Test
    void returnsArgumentForStaticMethod() {
        final int index = 0;
        MatcherAssert.assertThat(
            "Since it is local variables in a static method, the first variable is a local variable",
            new LocalVariables(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "(S)V")
                .variable(index, Type.DOUBLE_TYPE),
            Matchers.equalTo(new LocalVariable(index, Type.SHORT_TYPE))
        );
    }

    @Test
    void createsNewLocalVariable() {
        final int index = 1;
        final Type type = Type.INT_TYPE;
        MatcherAssert.assertThat(
            "Local variables should create a new variable if it is not in the cache",
            new LocalVariables(Opcodes.ACC_PUBLIC, "()V").variable(index, type),
            Matchers.equalTo(new LocalVariable(index, type))
        );
    }
}
