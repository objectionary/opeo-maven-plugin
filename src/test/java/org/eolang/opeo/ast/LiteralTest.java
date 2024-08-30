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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test case for {@link Literal}.
 * @since 0.1
 */
final class LiteralTest {

    private static final Object[] EMPTY = {};

    @Test
    void transformsToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect the following XML to be generated: <o base='string' data='bytes'>4E 65 6F</o>",
            new Xembler(new Literal("Neo").toXmir(), new Transformers.Node()).xml(),
            XhtmlMatchers.hasXPath(
                "/o[@base='string' and @data='bytes' and text()='4E 65 6F']/text()"
            )
        );
    }

    @Test
    void dereminesType() {
        MatcherAssert.assertThat(
            "We expect the type to be determined as string",
            new Literal("Neo").type(),
            Matchers.equalTo(Type.getType(String.class))
        );
        MatcherAssert.assertThat(
            "We expect the type to be determined as int",
            new Literal(1).type(),
            Matchers.equalTo(Type.INT_TYPE)
        );
        MatcherAssert.assertThat(
            "We expect the type to be determined as char",
            new Literal('a').type(),
            Matchers.equalTo(Type.CHAR_TYPE)
        );
        MatcherAssert.assertThat(
            "We expect the type to be determined as long",
            new Literal(1L).type(),
            Matchers.equalTo(Type.LONG_TYPE)
        );
        MatcherAssert.assertThat(
            "We expect the type to be determined as float",
            new Literal(1.0f).type(),
            Matchers.equalTo(Type.FLOAT_TYPE)
        );
        MatcherAssert.assertThat(
            "We expect the type to be determined as double",
            new Literal(1.0d).type(),
            Matchers.equalTo(Type.DOUBLE_TYPE)
        );
        MatcherAssert.assertThat(
            "We expect the type to be determined as boolean",
            new Literal(true).type(),
            Matchers.equalTo(Type.BOOLEAN_TYPE)
        );
        MatcherAssert.assertThat(
            "We expect the type to be determined as byte",
            new Literal((byte) 1).type(),
            Matchers.equalTo(Type.BYTE_TYPE)
        );
        MatcherAssert.assertThat(
            "We expect the type to be determined as short",
            new Literal((short) 1).type(),
            Matchers.equalTo(Type.SHORT_TYPE)
        );
    }

    @Test
    void convertsToOpcodesForBipush() {
        MatcherAssert.assertThat(
            "We expect the following opcodes to be generated: bipush 10",
            new Literal(10).opcodes(),
            Matchers.contains(
                new Opcode(Opcodes.BIPUSH, 10)
            )
        );
    }

    @Test
    void convertsToOpcodesForNull() {
        MatcherAssert.assertThat(
            "We expect the following opcodes to be generated: aconst_null",
            new Literal().opcodes(),
            Matchers.contains(
                new Opcode(Opcodes.ACONST_NULL)
            )
        );
    }

    @ParameterizedTest
    @MethodSource("ldc")
    void generatesLdcInstruction(final Object value) {
        final Opcode opcode = (Opcode) new Literal(value).opcodes().get(0);
        MatcherAssert.assertThat(
            "We expect the following opcode to be generated: LDC",
            opcode.opcode(),
            Matchers.equalTo(Opcodes.LDC)
        );
        MatcherAssert.assertThat(
            "We expect the following value to be passed to the LDC instruction as the first parameter",
            opcode.params().get(0),
            Matchers.equalTo(value)
        );
    }

    @ParameterizedTest
    @CsvSource({
        "FF FF FF FF FF FF FF FF, 2, ICONST_M1",
        "00 00 00 00 00 00 00 00, 3, ICONST_0",
        "00 00 00 00 00 00 00 01, 4, ICONST_1",
        "00 00 00 00 00 00 00 02, 5, ICONST_2",
        "00 00 00 00 00 00 00 03, 6, ICONST_3",
        "00 00 00 00 00 00 00 04, 7, ICONST_4",
        "00 00 00 00 00 00 00 05, 8, ICONST_5"
    })
    void constructsIntFromXmir(final String input, final int opcode, final String expected) {
        final AstNode actual = new Literal(
            new XmlNode(String.format("<o base='int' data='bytes'>%s</o>", input))
        ).opcodes().get(0);
        MatcherAssert.assertThat(
            String.format(
                "We expect the following opcodes to be generated: '%s', but was '%s'",
                expected,
                actual
            ),
            actual,
            Matchers.equalTo(new Opcode(opcode))
        );
    }

    @ParameterizedTest
    @CsvSource({
        "00 00 00 00 00 00 00 00, 9, LCONST_0",
        "00 00 00 00 00 00 00 01, 10, LCONST_1"
    })
    void constructsLongFromXmir(final String input, final int opcode, final String expected) {
        final AstNode actual = new Literal(
            new XmlNode(String.format("<o base='long' data='bytes'>%s</o>", input))
        ).opcodes().get(0);
        MatcherAssert.assertThat(
            String.format(
                "We expect the following opcodes to be generated: '%s', but was '%s'",
                expected,
                actual
            ),
            actual,
            Matchers.equalTo(new Opcode(opcode))
        );
    }

    /**
     * Test cases for {@link #generatesLdcInstruction(Object)} test.
     * @return Different values that might be converted to LDC instruction.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Object> ldc() {
        return Stream.of(
            "Load string from constant pool",
            29,
            29L,
            29.0f,
            29.0d
        );
    }

    @ParameterizedTest
    @MethodSource("opcodes")
    void generatesCorrectOpcodesForDifferentTypes(
        final Literal constant, final int expected, Object[] params
    ) {
        final Opcode opcode = (Opcode) constant.opcodes().get(0);
        MatcherAssert.assertThat(
            String.format(
                "We expect that '%s' opcode will be generated for '%s', but was '%s'",
                new OpcodeName(expected).simplified(),
                constant,
                new OpcodeName(opcode.opcode()).simplified()
            ),
            opcode.opcode(),
            Matchers.equalTo(expected)
        );
        final List<Object> all = opcode.params();
        for (int index = 0; index < params.length; ++index) {
            final Object act = all.get(index);
            final Object expect = params[index];
            MatcherAssert.assertThat(
                String.format(
                    "We expect that '%s' (id='%d) parameter will be generated, but was '%s'",
                    expect,
                    index,
                    act
                ),
                act,
                Matchers.equalTo(expect)
            );
        }
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> opcodes() {
        return Stream.of(
            Arguments.of(new Literal(-1), Opcodes.ICONST_M1, LiteralTest.EMPTY),
            Arguments.of(new Literal(0), Opcodes.ICONST_0, LiteralTest.EMPTY),
            Arguments.of(new Literal(1), Opcodes.ICONST_1, LiteralTest.EMPTY),
            Arguments.of(new Literal(2), Opcodes.ICONST_2, LiteralTest.EMPTY),
            Arguments.of(new Literal(3), Opcodes.ICONST_3, LiteralTest.EMPTY),
            Arguments.of(new Literal(4), Opcodes.ICONST_4, LiteralTest.EMPTY),
            Arguments.of(new Literal(5), Opcodes.ICONST_5, LiteralTest.EMPTY),
            Arguments.of(new Literal(0L), Opcodes.LCONST_0, LiteralTest.EMPTY),
            Arguments.of(new Literal(1L), Opcodes.LCONST_1, LiteralTest.EMPTY),
            Arguments.of(new Literal(0.0f), Opcodes.FCONST_0, LiteralTest.EMPTY),
            Arguments.of(new Literal(1.0f), Opcodes.FCONST_1, LiteralTest.EMPTY),
            Arguments.of(new Literal(2.0f), Opcodes.FCONST_2, LiteralTest.EMPTY),
            Arguments.of(new Literal(0.0d), Opcodes.DCONST_0, LiteralTest.EMPTY),
            Arguments.of(new Literal(1.0d), Opcodes.DCONST_1, LiteralTest.EMPTY),
            Arguments.of(new Literal(-128), Opcodes.BIPUSH, new Object[]{-128}),
            Arguments.of(new Literal(-42), Opcodes.BIPUSH, new Object[]{-42}),
            Arguments.of(new Literal(42), Opcodes.BIPUSH, new Object[]{42}),
            Arguments.of(new Literal(127), Opcodes.BIPUSH, new Object[]{127}),
            Arguments.of(new Literal((short) 128), Opcodes.SIPUSH, new Object[]{(short) 128}),
            Arguments.of(new Literal((short) -129), Opcodes.SIPUSH, new Object[]{(short) -129}),
            Arguments.of(new Literal((short) 32767), Opcodes.SIPUSH, new Object[]{(short) 32767}),
            Arguments.of(new Literal((short) -32768), Opcodes.SIPUSH, new Object[]{(short) -32768}),
            Arguments.of(new Literal('a'), Opcodes.BIPUSH, new Object[]{'a'}),
            Arguments.of(new Literal('b'), Opcodes.BIPUSH, new Object[]{'b'}),
            Arguments.of(new Literal('c'), Opcodes.BIPUSH, new Object[]{'c'}),
            Arguments.of(new Literal("Hello!"), Opcodes.LDC, new Object[]{"Hello!"}),
            Arguments.of(new Literal(100.0f), Opcodes.LDC, new Object[]{100f}),
            Arguments.of(new Literal(100.0d), Opcodes.LDC, new Object[]{100d}),
            Arguments.of(new Literal(100L), Opcodes.LDC, new Object[]{100L}),
            Arguments.of(new Literal(true), Opcodes.ICONST_1, LiteralTest.EMPTY),
            Arguments.of(new Literal(false), Opcodes.ICONST_0, LiteralTest.EMPTY)
        );
    }
}
