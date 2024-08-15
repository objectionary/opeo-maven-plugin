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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.cactoos.map.MapEntry;
import org.eolang.opeo.LabelInstruction;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.decompilation.DecompilerState;
import org.objectweb.asm.Opcodes;

/**
 * All agents that try to decompile incoming instructions.
 * @since 0.2
 * @todo #376:90min Decompilation Finish Condition.
 *  Currently we decompile until we out of instructions.
 *  But this might be incorrect when we start decompile high-level constructs.
 *  We should check decompilation stack instead.
 *  If it changes, we should continue. Otherwise, we should stop.
 *  The current implementation you can find here {@link #handle(DecompilerState)}.
 */
public final class AllAgents implements DecompilationAgent {

    /**
     * ALl instruction handlers.
     */
    private final Set<? extends DecompilationAgent> agents;

    /**
     * Constructor.
     */
    public AllAgents() {
        this(false);
    }

    /**
     * Constructor.
     * @param counting Do we put numbers to opcodes?
     */
    public AllAgents(final boolean counting) {
        this(
            new HashSet<>(
                Arrays.asList(
                    new ConstAgent(),
                    new AddAgent(),
                    new SubAgent(),
                    new MulAgent(),
                    new IfAgent(),
                    new CastAgent(),
                    new LoadAgent(),
                    new StoreAgent(),
                    new StoreToArrayAgent(),
                    new NewArrayAgent(),
                    new CheckCastAgent(),
                    new NewAgent(),
                    new DupAgent(),
                    new BipushAgent(),
                    new InvokespecialAgent(),
                    new InvokevirtualAgent(),
                    new InvokestaticAgent(),
                    new InvokeinterfaceAgent(),
                    new InvokedynamicAgent(),
                    new GetFieldAgent(),
                    new PutFieldAgent(),
                    new GetStaticAgent(),
                    new LdcAgent(),
                    new PopAgent(),
                    new ReturnAgent(),
                    new LabelAgent(),
                    new UnimplementedAgent(counting)
                )
            )
        );
    }

    /**
     * Constructor.
     * @param agents All handlers that will try to handle incoming instructions.
     */
    private AllAgents(final Set<? extends DecompilationAgent> agents) {
        this.agents = agents;
    }

    @Override
    public void handle(final DecompilerState state) {
        while (state.hasInstructions()) {
            this.agents.forEach(agent -> agent.handle(state));
        }
    }

    @Override
    public Supported supported() {
        return this.agents.stream()
            .map(DecompilationAgent::supported).
            reduce(new Supported(), Supported::merge);
    }

    /**
     * Get supported opcodes.
     * @return Supported opcodes.
     */
    public String[] supportedOpcodes() {
        return this.supported().names();
    }

    /**
     * Unimplemented instruction handler.
     * @since 0.1
     */
    private static final class UnimplementedAgent implements DecompilationAgent {

        /**
         * Do we put numbers to opcodes?
         */
        private final boolean counting;

        /**
         * Constructor.
         * @param counting Flag which decides if we need to count opcodes.
         */
        private UnimplementedAgent(final boolean counting) {
            this.counting = counting;
        }

        @Override
        public void handle(final DecompilerState state) {
            if (!new AllAgents().supported().isSupported(state.current())) {
                state.stack().push(
                    new Opcode(
                        state.current().opcode(),
                        state.current().params(),
                        this.counting
                    )
                );
                state.popInstruction();
            }
        }

        @Override
        public Supported supported() {
            return new Supported();
        }
    }
}
