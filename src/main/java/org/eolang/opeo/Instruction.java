package org.eolang.opeo;

import java.util.List;
import lombok.ToString;

@ToString
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

    public int opcode() {
        return this.opcode;
    }

    public Object operand(int index) {
        return this.operands.get(index);
    }
}
