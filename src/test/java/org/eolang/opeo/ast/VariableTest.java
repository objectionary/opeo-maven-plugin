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

import com.jcabi.matchers.XhtmlMatchers;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Tests for {@link Variable}.
 * @since 0.1
 */
class VariableTest {

    @Test
    void prints() {
        final String actual = new Variable(Type.INT_TYPE, 0).print();
        final String expected = "local0int";
        MatcherAssert.assertThat(
            String.format(
                "We expect the printed variable to be equal to '%s', but it wasn't, current value is '%s'",
                expected,
                actual
            ),
            actual,
            Matchers.equalTo(expected)
        );
    }

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect the xmir variable to be equal to <o base='local1'/>, but it wasn't",
            new Xembler(new Variable(Type.INT_TYPE, 1).toXmir()).xml(),
            XhtmlMatchers.hasXPath("./o[@base='local1']")
        );
    }

    @ParameterizedTest(name = "[{index}] {0} => {1}")
    @MethodSource("typesArguments")
    void convertsType(
        final Type type, final String expected
    ) throws ImpossibleModificationException {
        final String xml = new Xembler(new Variable(type, 1).toXmir()).xml();
        MatcherAssert.assertThat(
            String.format(
                "We expect the xmir variable type to be equal to <o base='local1' scope='%s'/>, but it wasn't, current value is '%s'",
                expected,
                xml
            ),
            xml,
            XhtmlMatchers.hasXPath(String.format("./o[@scope='%s']", expected))
        );
    }

    /**
     * Types arguments.
     * @return Arguments.
     */
    private static Stream<Arguments> typesArguments() {
        return Stream.of(
            Arguments.of(Type.INT_TYPE, "I"),
            Arguments.of(Type.BOOLEAN_TYPE, "Z"),
            Arguments.of(Type.BYTE_TYPE, "B"),
            Arguments.of(Type.CHAR_TYPE, "C"),
            Arguments.of(Type.DOUBLE_TYPE, "D"),
            Arguments.of(Type.FLOAT_TYPE, "F"),
            Arguments.of(Type.LONG_TYPE, "J"),
            Arguments.of(Type.SHORT_TYPE, "S"),
            Arguments.of(Type.VOID_TYPE, "V"),
            Arguments.of(Type.getType("Ljava/lang/String;"), "Ljava/lang/String;")
        );
    }
}
