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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests for {@link PrefixedName}.
 * @since 0.1
 */
final class PrefixedNameTest {

    @ParameterizedTest(name = "adds 'j$' prefix to {0}, expected {1}")
    @CsvSource({
        "java.util.Arrays,java.util.j$Arrays",
        "java.util.stream.IntStream,java.util.stream.j$IntStream",
        "java.util.stream.Stream,java.util.stream.j$Stream",
        "java.util.stream.Collectors,java.util.stream.j$Collectors",
        "java.util.stream.StreamSupport,java.util.stream.j$StreamSupport",
        "ArrayList,j$ArrayList",
        "java.util.List,java.util.j$List",
        "java/util/Arrays,java/util/j$Arrays",
        "java/util/stream/IntStream,java/util/stream/j$IntStream",
        "java/util/stream/Stream,java/util/stream/j$Stream",
        "java/util/stream/Collectors,java/util/stream/j$Collectors"
    })
    void addsPrefixes(final String initial, final String expected) {
        MatcherAssert.assertThat(
            "We expect that 'j$' prefix is added to the name",
            new PrefixedName(initial).withPrefix(),
            Matchers.equalTo(expected)
        );
    }

    @ParameterizedTest(name = "removes 'j$' prefix from {0}, expected {1}")
    @CsvSource({
        "java.util.j$Arrays,java.util.Arrays",
        "java.util.stream.j$IntStream,java.util.stream.IntStream",
        "java.util.stream.j$Stream,java.util.stream.Stream",
        "java.util.stream.j$Collectors,java.util.stream.Collectors",
        "java.util.stream.j$StreamSupport,java.util.stream.StreamSupport",
        "j$ArrayList,ArrayList",
        "java.util.j$List,java.util.List",
        "java/util/j$Arrays,java/util/Arrays",
        "java/util/stream/j$IntStream,java/util/stream/IntStream",
        "java/util/stream/j$Stream,java/util/stream/Stream",
        "java/util/stream/j$Collectors,java/util/stream/Collectors"
    })
    void removesPrefix(final String initial, final String expected) {
        MatcherAssert.assertThat(
            "We expect that 'j$' prefix is removed from the name",
            new PrefixedName(initial).withoutPrefix(),
            Matchers.equalTo(expected)
        );
    }
}
