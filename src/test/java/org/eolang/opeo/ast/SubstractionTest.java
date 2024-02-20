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
import org.eolang.opeo.compilation.HasInstructions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Substraction test.
 * Test case for {@link Substraction}.
 *
 * @since 0.2
 */
class SubstractionTest {

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't convert Substraction with two literals to XMIR",
            new Xembler(
                new Substraction(
                    new Literal(1),
                    new Literal(2)
                ).toXmir()
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "/o[@base='.minus']",
                "/o[@base='.minus']/o[@base='int' and contains(text(), '1')]",
                "/o[@base='.minus']/o[@base='int' and contains(text(), '2')]"
            )
        );
    }

    @Test
    void determinesPrimitiveTypesCorrectly() {
        MatcherAssert.assertThat(
            "Can't determine the type of Substraction with two integer literals",
            new Substraction(new Literal(1), new Literal(2)).type(),
            Matchers.equalTo(Type.INT_TYPE)
        );
        MatcherAssert.assertThat(
            "Can't determine the type of Substraction with two long literals",
            new Substraction(new Literal(1L), new Literal(2L)).type(),
            Matchers.equalTo(Type.LONG_TYPE)
        );
        MatcherAssert.assertThat(
            "Can't determine the type of Substraction with two float literals",
            new Substraction(new Literal(1.0f), new Literal(2.0f)).type(),
            Matchers.equalTo(Type.FLOAT_TYPE)
        );
        MatcherAssert.assertThat(
            "Can't determine the type of Substraction with two double literals",
            new Substraction(new Literal(1.0), new Literal(2.0)).type(),
            Matchers.equalTo(Type.DOUBLE_TYPE)
        );
        MatcherAssert.assertThat(
            "Can't determine the type of Substraction with two integer and long literals",
            new Substraction(new Literal(1), new Literal(2L)).type(),
            Matchers.equalTo(Type.LONG_TYPE)
        );
        MatcherAssert.assertThat(
            "Can't determine the type of Substraction with two integer and float literals",
            new Substraction(new Literal(1), new Literal(2.0f)).type(),
            Matchers.equalTo(Type.FLOAT_TYPE)
        );
        MatcherAssert.assertThat(
            "Can't determine the type of Substraction with two integer and double literals",
            new Substraction(new Literal(1), new Literal(2.0)).type(),
            Matchers.equalTo(Type.DOUBLE_TYPE)
        );
        MatcherAssert.assertThat(
            "Can't determine the type of Substraction with two long and float literals",
            new Substraction(new Literal(1L), new Literal(2.0f)).type(),
            Matchers.equalTo(Type.FLOAT_TYPE)
        );
    }

    @Test
    void retrievesOpcodesWithLeftAndRightNodesWithTheSameType() {
        MatcherAssert.assertThat(
            "Can't retrieve opcodes from Substraction with two literals",
            new OpcodeNodes(
                new Substraction(
                    new Literal(1),
                    new Literal(2)
                )
            ).opcodes(),
            new HasInstructions(
                new HasInstructions.Instruction(Opcodes.ICONST_1),
                new HasInstructions.Instruction(Opcodes.ICONST_2),
                new HasInstructions.Instruction(Opcodes.ISUB)
            )
        );
    }

    @Test
    void retrievesOpcodesWithLeftAndRightNodesWithDifferentTypes() {
        MatcherAssert.assertThat(
            "Can't retrieve opcodes from Substraction with two literals of different types",
            new OpcodeNodes(
                new Substraction(
                    new Literal(1L),
                    new Literal(1)
                )
            ).opcodes(),
            new HasInstructions(
                new HasInstructions.Instruction(Opcodes.LCONST_1),
                new HasInstructions.Instruction(Opcodes.ICONST_1),
                new HasInstructions.Instruction(Opcodes.LSUB)
            )
        );
    }
}
