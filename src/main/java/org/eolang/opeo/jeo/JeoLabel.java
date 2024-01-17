package org.eolang.opeo.jeo;

import java.util.List;
import org.eolang.jeo.representation.xmir.XmlLabel;
import org.eolang.opeo.Instruction;

public final class JeoLabel implements Instruction {

    public static final int LABEL_OPCODE = 1001;
    private final XmlLabel label;

    public JeoLabel(final XmlLabel label) {
        this.label = label;
    }

    @Override
    public int opcode() {
        return JeoLabel.LABEL_OPCODE;
    }

    @Override
    public Object operand(final int index) {
        return this.operands().get(index);
    }

    @Override
    public List<Object> operands() {
        return List.of(this.label.identifier());
    }
}
