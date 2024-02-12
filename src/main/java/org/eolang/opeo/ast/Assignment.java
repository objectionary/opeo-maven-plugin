package org.eolang.opeo.ast;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;
import org.xembly.Directives;

public final class Assignment implements AstNode {

    private final AstNode left;
    private final AstNode right;

    private final Attributes attributes;

    public Assignment(final AstNode left, final AstNode right) {
        this(left, right, new Attributes().type("field"));
    }

    public Assignment(
        final AstNode left,
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
            .add("o")
            .attr("base", String.format(".%s", this.attributes.name()))
            .append(this.left.toXmir())
            .up()
            .append(this.right.toXmir())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(1);
        res.addAll(this.left.opcodes());
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
