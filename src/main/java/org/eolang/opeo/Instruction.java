package org.eolang.opeo;

import java.util.List;

public class Instruction {
    private final int opcode;
    private final List<Object> operands;

    public Instruction(final int opcode, Object... operands) {
        this(opcode, List.of(operands));
    }

    public Instruction(final int opcode, final List<Object> operands) {
        this.opcode = opcode;
        this.operands = operands;
    }
}
