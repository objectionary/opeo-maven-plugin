package org.eolang.opeo.decompilation.agents;

import org.eolang.opeo.decompilation.DecompilerState;

public final class SupportedOpcodes {

    private final DecompilationAgent agent;

    public SupportedOpcodes(final DecompilationAgent agent) {
        this.agent = agent;
    }

    public boolean isSupported(final DecompilerState state) {
        return state.hasInstructions() && this.agent.supported().isSupported(state.current());
    }

}
