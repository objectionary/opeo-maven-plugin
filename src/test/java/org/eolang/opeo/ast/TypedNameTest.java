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

import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test case for {@link TypedName}.
 * @since 0.4
 */
final class TypedNameTest {

    @ParameterizedTest
    @MethodSource("types")
    void appendsType(final String name, final String expected, final Attributes attrs) {
        MatcherAssert.assertThat(
            "We expect that the type will be appended to the name",
            new TypedName(name).withType(attrs),
            Matchers.equalTo(expected)
        );
    }

    @ParameterizedTest
    @MethodSource("types")
    void removesType(final String expected, final String name) {
        MatcherAssert.assertThat(
            "We expect that the type will be removed from the name",
            new TypedName(name).withoutType(),
            Matchers.equalTo(expected)
        );
    }

    /**
     * Test cases for all methods.
     * Methods that use this test cases:
     * - {@link #appendsType(String, String, Attributes)}
     * - {@link #removesType(String, String)}
     * @return Test cases.
     */
    private static Stream<Arguments> types() {
        return Stream.of(
            Arguments.of(
                "foo",
                "java_lang_Object$foo",
                new Attributes().descriptor("()Ljava/lang/Object;")
            ),
            Arguments.of(
                "of",
                "java_lang_util_Stream$of",
                new Attributes().descriptor("()Ljava/lang/util/Stream;")
            ),
            Arguments.of(
                "map",
                "java_lang_util_Stream$map",
                new Attributes()
                    .descriptor("(Ljava/util/function/Function;)Ljava/util/stream/Stream;")
            ),
            Arguments.of(
                "filter",
                "java_lang_util_Stream$filter",
                new Attributes()
                    .descriptor("(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;")
            ),
            Arguments.of(
                "collect",
                "java_lang_util_Stream$collect",
                new Attributes()
                    .descriptor("(Ljava/util/stream/Collector;)Ljava/lang/Object;")
            )
        );
    }

}