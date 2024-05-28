package org.eolang.opeo.decompilation.handlers;

import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.If;
import org.eolang.opeo.ast.Label;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.decompilation.DecompilerState;
import org.eolang.opeo.decompilation.InstructionHandler;
import org.eolang.opeo.decompilation.OperandStack;
import org.objectweb.asm.Opcodes;

/**
 * If instruction handler.
 * [value1, value2] → []
 * If value1 is greater than value2, branch to instruction at branchoffset
 * (signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
 * @since 0.2
 */
public final class IfHandler implements InstructionHandler {
    @Override
    public void handle(final DecompilerState state) {
        if (state.instruction().opcode() == Opcodes.IF_ICMPGT) {
            final OperandStack stack = state.stack();
            final AstNode first = stack.pop();
            final AstNode second = stack.pop();
            final Label operand = (Label) state.operand(0);
            stack.push(new If(first, second, operand));

        }
    }
}
