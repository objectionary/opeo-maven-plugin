package org.eolang.opeo.decompilation;

import org.eolang.opeo.Instruction;
import org.eolang.opeo.ast.AstNode;
import org.objectweb.asm.Type;

public final class MachineState {

    private final Instruction current;

    private final OperandStack operands;

    private final LocalVariables vars;

    public MachineState(final LocalVariables vars) {
        this(new Instruction.Nop(), new OperandStack(), vars);
    }

    public MachineState(
        final Instruction current,
        final OperandStack stack,
        final LocalVariables variables
    ) {
        this.current = current;
        this.operands = stack;
        this.vars = variables;
    }

    public MachineState next(final Instruction instruction) {
        return new MachineState(instruction, this.operands, this.vars);
    }

    public Instruction instruction() {
        return this.current;
    }

    public AstNode variable(final int index, final Type type) {
        return this.vars.variable(index, type);
    }

    public Object operand(final int index) {
        return this.current.operand(index);
    }

    public OperandStack stack() {
        return this.operands;
    }
}
