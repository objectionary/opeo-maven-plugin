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
        this(false, new TracedAgent.Log());
    }

    /**
     * Constructor.
     * @param counting Do we put numbers to opcodes?
     */
    public AllAgents(final boolean counting, final TracedAgent.Output output) {
        this(
            new HashSet<>(
                Arrays.asList(
                    new TracedAgent(new ConstAgent(), output),
                    new TracedAgent(new AddAgent(), output),
                    new TracedAgent(new SubAgent(), output),
                    new TracedAgent(new MulAgent(), output),
                    new TracedAgent(new IfAgent(), output),
                    new TracedAgent(new CastAgent(), output),
                    new TracedAgent(new LoadAgent(), output),
                    new TracedAgent(new StoreAgent(), output),
                    new TracedAgent(new StoreToArrayAgent(), output),
                    new TracedAgent(new NewArrayAgent(), output),
                    new TracedAgent(new CheckCastAgent(), output),
                    new TracedAgent(new NewAgent(), output),
                    new TracedAgent(new DupAgent(), output),
                    new TracedAgent(new BipushAgent(), output),
                    new TracedAgent(new InvokespecialAgent(), output),
                    new TracedAgent(new InvokevirtualAgent(), output),
                    new TracedAgent(new InvokestaticAgent(), output),
                    new TracedAgent(new InvokeinterfaceAgent(), output),
                    new TracedAgent(new InvokedynamicAgent(), output),
                    new TracedAgent(new GetFieldAgent(), output),
                    new TracedAgent(new PutFieldAgent(), output),
                    new TracedAgent(new GetStaticAgent(), output),
                    new TracedAgent(new LdcAgent(), output),
                    new TracedAgent(new PopAgent(), output),
                    new TracedAgent(new ReturnAgent(), output),
                    new TracedAgent(new LabelAgent(), output),
                    new TracedAgent(new UnimplementedAgent(counting), output)
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
