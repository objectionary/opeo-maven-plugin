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
package org.eolang.opeo.jeo;

import org.eolang.jeo.representation.xmir.XmlInstruction;
import org.eolang.jeo.representation.xmir.XmlMethod;
import org.eolang.opeo.Instruction;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link JeoInstructions}.
 * @since 0.1
 */
final class JeoInstructionsTest {

    @Test
    void parsesJeoInstructions() {
        final XmlMethod method = new XmlMethod();
        method.replaceInstructions(
            new XmlInstruction(Opcodes.LDC, "Hello, world!").toNode(),
            new XmlInstruction(
                Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V"
            ).toNode(),
            new XmlInstruction(Opcodes.RETURN).toNode()
        );
        final Instruction[] instructions = new JeoInstructions(method).instructions();
        MatcherAssert.assertThat(
            "The resulting array of instructions should have exactly 3 elements",
            instructions,
            Matchers.arrayWithSize(3)
        );
        MatcherAssert.assertThat(
            "The first instruction should be LDC",
            instructions[0].opcode(),
            Matchers.equalTo(Opcodes.LDC)
        );
        MatcherAssert.assertThat(
            "The second instruction should be INVOKEVIRTUAL",
            instructions[1].opcode(),
            Matchers.equalTo(Opcodes.INVOKEVIRTUAL)
        );
        MatcherAssert.assertThat(
            "The third instruction should be RETURN",
            instructions[2].opcode(),
            Matchers.equalTo(Opcodes.RETURN)
        );
    }

}
