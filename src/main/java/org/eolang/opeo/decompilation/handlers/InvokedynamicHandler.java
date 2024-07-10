package org.eolang.opeo.decompilation.handlers;

import java.util.List;
import org.eolang.opeo.ast.DynamicInvocation;
import org.eolang.opeo.decompilation.DecompilerState;
import org.eolang.opeo.decompilation.InstructionHandler;
import org.objectweb.asm.Handle;

public final class InvokedynamicHandler implements InstructionHandler {
    @Override
    public void handle(final DecompilerState state) {
        final int opcode = state.instruction().opcode();
        final List<Object> operands = state.instruction().operands();
        final String name = (String) operands.get(0);
        final String descriptor = (String) operands.get(1);
        final Handle factory = (Handle) operands.get(2);
        final List<Object> args = operands.subList(3, operands.size());
        final DynamicInvocation node = new DynamicInvocation(
            name,
            new org.eolang.opeo.ast.Handle(factory),
            descriptor,
            args
        );
        state.stack().push(node);
    }
}
