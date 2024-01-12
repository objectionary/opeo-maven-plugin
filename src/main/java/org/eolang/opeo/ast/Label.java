package org.eolang.opeo.ast;

import java.util.Collections;
import java.util.List;
import org.xembly.Directive;
import org.xembly.Directives;

public final class Label implements AstNode {

    private final AstNode identifier;

    public Label(final AstNode identifier) {
        this.identifier = identifier;
    }

    @Override
    public String print() {
        return String.format(": %s", this.identifier.print());
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives()
            .add("o")
            .attr("base", "label")
            .append(this.identifier.toXmir())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        return Collections.singletonList(this);
    }
}
