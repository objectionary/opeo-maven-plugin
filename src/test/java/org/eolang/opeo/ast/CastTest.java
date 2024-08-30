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
import java.util.LinkedList;
import java.util.stream.Stream;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link Cast}.
 * @since 0.2
 */
final class CastTest {

    @Test
    void createsFromXmir() {
        final XmlNode node = new XmlNode(
            String.join(
                "\n",
                "<o base='cast'>",
                "   <o base='int' data='bytes'>00 00 00 00 00 00 00 01</o>",
                "   <o base='string' data='bytes'>49</o>",
                "</o>"
            )
        );
        MatcherAssert.assertThat(
            "Can't create correct Cast from XMIR",
            new Cast(node, Constant::new),
            Matchers.equalTo(new Cast(Type.INT_TYPE, new Constant(1)))
        );
    }

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't convert Cast to XMIR",
            new Xembler(new Cast(Type.INT_TYPE, new Constant(1)).toXmir()).xml(),
            XhtmlMatchers.hasXPaths(
                "/o[@base='cast']",
                "/o[@base='cast']/o[@base='int' and contains(text(), '1')]",
                "/o[@base='cast']/o[@base='string']"
            )
        );
    }

    @ParameterizedTest(name = "detects type {0}")
    @MethodSource("types")
    void detectsType(final Type type) {
        MatcherAssert.assertThat(
            String.format("Can't detect type of casted value, should be %s", type),
            new Cast(type, new Constant(1)).type(),
            Matchers.equalTo(type)
        );
    }

    @ParameterizedTest(name = "converts {1} to {0} with opcode {2}")
    @MethodSource("conversions")
    void convertsToOpcodes(final Type target, final AstNode value, final int expected) {
        final AstNode actual = new LinkedList<>(new Cast(target, value).opcodes()).getLast();
        MatcherAssert.assertThat(
            String.format(
                "Can't convert Cast %s to %s. Expected opcode is %s, but we retrieved %s instead. Check org.objectweb.asm.Opcodes to find corresponding bytecode instructions",
                value, target, new Opcode(expected), actual
            ),
            actual,
            Matchers.equalTo(new Opcode(expected))
        );
    }

    /**
     * Converts values to opcodes.
     * @return Arguments for the test {@link #convertsToOpcodes}.
     */
    static Stream<Arguments> conversions() {
        return Stream.of(
            Arguments.of(Type.LONG_TYPE, new Constant(1), Opcodes.I2L),
            Arguments.of(Type.FLOAT_TYPE, new Constant(1), Opcodes.I2F),
            Arguments.of(Type.DOUBLE_TYPE, new Constant(1), Opcodes.I2D),
            Arguments.of(Type.INT_TYPE, new Constant(1L), Opcodes.L2I),
            Arguments.of(Type.FLOAT_TYPE, new Constant(1L), Opcodes.L2F),
            Arguments.of(Type.DOUBLE_TYPE, new Constant(1L), Opcodes.L2D),
            Arguments.of(Type.INT_TYPE, new Constant(1.0f), Opcodes.F2I),
            Arguments.of(Type.LONG_TYPE, new Constant(1.0f), Opcodes.F2L),
            Arguments.of(Type.DOUBLE_TYPE, new Constant(1.0f), Opcodes.F2D),
            Arguments.of(Type.INT_TYPE, new Constant(1.0), Opcodes.D2I),
            Arguments.of(Type.LONG_TYPE, new Constant(1.0), Opcodes.D2L),
            Arguments.of(Type.FLOAT_TYPE, new Constant(1.0), Opcodes.D2F),
            Arguments.of(Type.BYTE_TYPE, new Constant(1), Opcodes.I2B),
            Arguments.of(Type.CHAR_TYPE, new Constant(1), Opcodes.I2C),
            Arguments.of(Type.SHORT_TYPE, new Constant(1), Opcodes.I2S)
        );
    }

    /**
     * Types for the test {@link #detectsType}.
     * @return Arguments for the test {@link #detectsType}.
     */
    static Stream<Arguments> types() {
        return Stream.of(
            Arguments.of(Type.VOID_TYPE),
            Arguments.of(Type.BOOLEAN_TYPE),
            Arguments.of(Type.CHAR_TYPE),
            Arguments.of(Type.BYTE_TYPE),
            Arguments.of(Type.SHORT_TYPE),
            Arguments.of(Type.INT_TYPE),
            Arguments.of(Type.LONG_TYPE),
            Arguments.of(Type.FLOAT_TYPE),
            Arguments.of(Type.DOUBLE_TYPE),
            Arguments.of(Type.getType(String.class))
        );
    }
}
