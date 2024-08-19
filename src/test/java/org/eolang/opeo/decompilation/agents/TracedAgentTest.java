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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.decompilation.DecompilerState;
import org.eolang.opeo.decompilation.LocalVariables;
import org.eolang.opeo.decompilation.OperandStack;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link TracedAgent}.
 *
 * @since 0.4
 */
final class TracedAgentTest {

    @Test
    void tracesDecompilation() {
        final TracedAgent.Container output = new TracedAgent.Container();
        new TracedAgent(new DummyAgent(), output).handle(
            new DecompilerState(
                Stream.of(
                    new Opcode(Opcodes.LCONST_1),
                    new Opcode(Opcodes.LRETURN)
                ).collect(Collectors.toCollection(ArrayDeque::new)),
                new OperandStack(),
                new LocalVariables()
            )
        );
        MatcherAssert.assertThat(
            "We should see the start and end messages together with the decompliation state",
            output.messages(),
            Matchers.containsInAnyOrder(
                "Stack before DummyAgent: []",
                "Instructions before DummyAgent: [LCONST_1 LRETURN]",
                "Stack after DummyAgent: []",
                "Instructions after DummyAgent: [LCONST_1 LRETURN]"
            )
        );
    }

    @Test
    void printsDecompilationTraceToLogs() {
        Assertions.assertDoesNotThrow(
            () -> new TracedAgent(new DummyAgent()).handle(
                new DecompilerState(
                    new OperandStack(
                        new Opcode(Opcodes.LCONST_1), new Opcode(Opcodes.LRETURN)
                    )
                )
            ),
            "Should not throw any exceptions"
        );
    }

}
