package org.eolang.opeo.ast;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;
import org.xembly.Directives;

public final class FieldAssignment implements AstNode {

    private final InstanceField left;
    private final AstNode right;

    private final Attributes attributes;

    public FieldAssignment(
        final InstanceField left,
        final AstNode right,
        final Attributes attributes
    ) {
        this.left = left;
        this.right = right;
        this.attributes = attributes.type("field");
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives().add("o")
            .attr("base", ".writefield")
            .attr("scope", this.attributes)
            .append(this.left.toXmir())
            .append(this.right.toXmir())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(3);
        res.addAll(this.left.instance().opcodes());
        res.addAll(this.right.opcodes());
        res.add(
            new Opcode(
                Opcodes.PUTFIELD,
                this.attributes.owner(),
                this.attributes.name(),
                this.attributes.descriptor()
            )
        );
        return res;
    }
}
