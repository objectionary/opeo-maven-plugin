package org.eolang.opeo.ast;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

public final class VariableAssignment implements AstNode {

    private final LocalVariable left;
    private final AstNode right;

    private final Attributes attributes;

    public VariableAssignment(final LocalVariable left, final AstNode right) {
        this(left, right, new Attributes());
    }

    public VariableAssignment(
        final LocalVariable left,
        final AstNode right,
        final Attributes attributes
    ) {
        this.left = left;
        this.right = right;
        this.attributes = attributes;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives().add("o")
            .attr("base", ".write")
            .attr("scope", this.attributes)
            .append(this.left.toXmir())
            .append(this.right.toXmir())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(3);
        res.addAll(this.right.opcodes());
        res.add(new Opcode(this.opcode(), this.left.id()));
        return res;
    }

    private int opcode() {
        final Type type = this.left.type();
        return type.getOpcode(Opcodes.ISTORE);
//        if (type.equals(Type.INT_TYPE)) {
//            return Opcodes.ISTORE;
//        } else if (type.equals(Type.DOUBLE_TYPE)) {
//            return Opcodes.DSTORE;
//        } else if (type.equals(Type.LONG_TYPE)) {
//            return Opcodes.LSTORE;
//        } else if (type.equals(Type.FLOAT_TYPE)) {
//            return Opcodes.FSTORE;
//        } else if (type.equals(Type.OBJECT)) {
//            return Opcodes.ASTORE;
//        } else {
//            throw new IllegalStateException(
//                String.format("Unknown type: %s", type)
//            );
//        }
    }
}
