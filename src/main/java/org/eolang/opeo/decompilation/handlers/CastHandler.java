package org.eolang.opeo.decompilation.handlers;

import org.eolang.opeo.ast.Cast;
import org.eolang.opeo.decompilation.DecompilerState;
import org.eolang.opeo.decompilation.InstructionHandler;
import org.objectweb.asm.Type;

public final class CastHandler implements InstructionHandler {

    private final Type target;

    public CastHandler(final Type target) {
        this.target = target;
    }

    @Override
    public void handle(final DecompilerState state) {
        state.stack().push(
            new Cast(
                this.target,
                state.stack().pop()
            )
        );
    }
}
