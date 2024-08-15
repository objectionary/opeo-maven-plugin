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
import java.util.List;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link Multiplication}.
 * @since 0.1
 */
final class MultiplicationTest {

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        final String xmir = new Xembler(
            new Multiplication(
                new Literal(3),
                new Literal(4)
            ).toXmir()
        ).xml();
        MatcherAssert.assertThat(
            String.format(
                "Can't convert multiplication to XMIR, actual result is : %n%s%n",
                xmir
            ),
            xmir,
            XhtmlMatchers.hasXPaths(
                "./o[@base='times']",
                "./o[@base='times']/o[@base='int' and contains(text(),'3')]",
                "./o[@base='times']/o[@base='int' and contains(text(),'4')]"
            )
        );
    }

    @ParameterizedTest
    @MethodSource("multiplications")
    void convertsToOpcodesWithDifferentTypes(
        final AstNode left,
        final AstNode right,
        final int expected
    ) {
        final List<AstNode> opcodes = new Multiplication(left, right).opcodes();
        final AstNode last = opcodes.get(opcodes.size() - 1);
        MatcherAssert.assertThat(
            String.format(
                "We expect that multiplication with two arguments ('%s' and '%s') will be converted to a opcode '%s'",
                left,
                right,
                new OpcodeName(expected).simplified()
            ),
            last,
            Matchers.equalTo(new Opcode(expected))
        );
    }

    /**
     * Provide multiplication test cases.
     * For test case {@link #convertsToOpcodesWithDifferentTypes(AstNode, AstNode, int)}
     * @return Test cases.
     */
    private static Stream<org.junit.jupiter.params.provider.Arguments> multiplications() {
        return Stream.of(
            org.junit.jupiter.params.provider.Arguments.of(
                new Literal(1),
                new Literal(2),
                Opcodes.IMUL
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                new Literal(3L),
                new Literal(4L),
                Opcodes.LMUL
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                new Literal(5.0f),
                new Literal(6.0f),
                Opcodes.FMUL
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                new Literal(7.0),
                new Literal(8.0),
                Opcodes.DMUL
            )
        );
    }
}
