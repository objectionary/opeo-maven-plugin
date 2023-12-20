package org.eolang.opeo;

import java.util.List;

public interface Instruction {

    /**
     * Opcode number.
     * @return Opcode number.
     */
    int opcode();

    /**
     * Retrieve operand by position index.
     * @param index Operand index
     * @return Operand
     */
    Object operand(final int index);

    /**
     * Full list of operands.
     * @return Operands.
     */
    List<Object> operands();
}
