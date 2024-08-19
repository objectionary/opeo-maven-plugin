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
import org.eolang.opeo.decompilation.DecompilerState;

/**
 * All agents that try to decompile incoming instructions.
 * @since 0.2
 * @todo #376:90min Decompilation Finish Condition.
 *  Currently we decompile until we out of instructions.
 *  But this might be incorrect when we start decompile high-level constructs.
 *  We should check decompilation stack instead.
 *  If it changes, we should continue. Otherwise, we should stop.
 *  The current implementation you can find here {@link #handle(DecompilerState)}.
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
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
                    new TracedAgent(new ConstAgent()),
                    new TracedAgent(new AddAgent()),
                    new TracedAgent(new SubAgent()),
                    new TracedAgent(new MulAgent()),
                    new TracedAgent(new IfAgent()),
                    new TracedAgent(new CastAgent()),
                    new TracedAgent(new LoadAgent()),
                    new TracedAgent(new StoreAgent()),
                    new TracedAgent(new StoreToArrayAgent()),
                    new TracedAgent(new NewArrayAgent()),
                    new TracedAgent(new CheckCastAgent()),
                    new TracedAgent(new NewAgent()),
                    new TracedAgent(new DupAgent()),
                    new TracedAgent(new BipushAgent()),
                    new TracedAgent(new InvokespecialAgent()),
                    new TracedAgent(new InvokevirtualAgent()),
                    new TracedAgent(new InvokestaticAgent()),
                    new TracedAgent(new InvokeinterfaceAgent()),
                    new TracedAgent(new InvokedynamicAgent()),
                    new TracedAgent(new GetFieldAgent()),
                    new TracedAgent(new PutFieldAgent()),
                    new TracedAgent(new GetStaticAgent()),
                    new TracedAgent(new LdcAgent()),
                    new TracedAgent(new PopAgent()),
                    new TracedAgent(new ReturnAgent()),
                    new TracedAgent(new LabelAgent()),
                    new TracedAgent(new UnimplementedAgent(counting))
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
    public boolean appropriate(final DecompilerState state) {
        return this.agents.stream().anyMatch(agent -> agent.appropriate(state));
    }

    @Override
    public Supported supported() {
        return this.agents.stream()
            .map(DecompilationAgent::supported)
            .reduce(new Supported(), Supported::merge);
    }

    @Override
    public void handle(final DecompilerState state) {
        while (this.appropriate(state)) {
            this.agents.stream().filter(agent -> agent.appropriate(state))
                .findFirst()
                .ifPresent(agent -> agent.handle(state));
        }
    }

    /**
     * Get supported opcodes.
     * @return Supported opcodes.
     */
    public String[] supportedOpcodes() {
        return this.supported().names();
    }

}
