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
package org.eolang.opeo.decompilation.agents;

import java.util.ArrayDeque;
import java.util.Collections;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.decompilation.DecompilerState;
import org.eolang.opeo.decompilation.LocalVariables;
import org.eolang.opeo.decompilation.OperandStack;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link OpcodesAgent}.
 * @since 0.4
 */
final class OpcodesAgentTest {

    @Test
    void checksAppropritate() {
        MatcherAssert.assertThat(
            "We expect the agent will be appropriate for the current state",
            new OpcodesAgent(new AddAgent())
                .appropriate(
                    new DecompilerState(
                        new ArrayDeque<>(Collections.singletonList(new Opcode(Opcodes.IADD))),
                        new OperandStack(),
                        new LocalVariables()
                    )
                ),
            Matchers.is(true)
        );
    }

    @Test
    void delegatesSupported() {
        MatcherAssert.assertThat(
            "We expect the supported opcodes will be delegated to the original agent",
            new OpcodesAgent(new AddAgent()).supported().names(),
            Matchers.hasItemInArray("IADD")
        );
    }
}
