package org.eolang.opeo.decompilation.agents;

import org.eolang.opeo.decompilation.DecompilerState;

public final class OpcodeAgent implements DecompilationAgent {
    private final DecompilationAgent original;

    public OpcodeAgent(final DecompilationAgent original) {
        this.original = original;
    }

    @Override
    public void handle(final DecompilerState state) {
        if (state.hasInstructions() && this.original.supported().isSupported(state.current())) {
            this.original.handle(state);
        }
    }

    @Override
    public Supported supported() {
        return this.original.supported();
    }
}
