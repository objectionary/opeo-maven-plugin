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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test for {@link LocalVariables}.
 * @since 0.1
 */
class LocalVariablesTest {

    @Test
    void createsForEmptyStaticMethod() {
        final int res = new LocalVariables(Opcodes.ACC_STATIC + Opcodes.ACC_PUBLIC, "()V").size();
        MatcherAssert.assertThat(
            String.format(
                "Local variables size for static method with no arguments should be 0, but was %d",
                res
            ),
            res,
            Matchers.equalTo(0)
        );
    }

    @Test
    void createsForEmptyInstanceMethod() {
        MatcherAssert.assertThat(
            "Local variables size for instance method with no arguments should contain 'this', but wasn't",
            new LocalVariables(Opcodes.ACC_PUBLIC, "()V").size(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void createsForNonEmptyStaticMethod() {
        MatcherAssert.assertThat(
            "Local variables size for static method with arguments should be equal to arguments count from descriptor",
            new LocalVariables(Opcodes.ACC_STATIC + Opcodes.ACC_PUBLIC, "(II)V").size(),
            Matchers.equalTo(2)
        );
    }

    @Test
    void createsForNonEmptyInstanceMethod() {
        MatcherAssert.assertThat(
            "Local variables size for instance method with arguments should be equal to arguments count from descriptor + 1 for 'this'",
            new LocalVariables(Opcodes.ACC_PUBLIC, "(II)V").size(),
            Matchers.equalTo(3)
        );
    }
}
