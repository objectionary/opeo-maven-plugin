package org.eolang.opeo;

import java.util.Collections;
import java.util.List;
import org.eolang.jeo.representation.xmir.AllLabels;
import org.eolang.opeo.jeo.JeoLabel;
import org.objectweb.asm.Label;

public final class LabelInstruction implements Instruction {

    private final Label label;

    public LabelInstruction(final Label label) {
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
        return Collections.singletonList(
            new AllLabels().uid(this.label)
        );
    }
}
