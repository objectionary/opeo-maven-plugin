package org.eolang.opeo.ast;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.xembly.Directive;
import org.xembly.Directives;

public final class Labeled implements AstNode {

    private final AstNode node;
    private final Label label;

    public Labeled(final AstNode node, final Label label) {
        this.node = node;
        this.label = label;
    }

    @Override
    public List<AstNode> opcodes() {
        return Stream.concat(
            this.node.opcodes().stream(),
            this.label.opcodes().stream()
        ).collect(Collectors.toList());
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives().append(this.node.toXmir()).append(this.label.toXmir());
    }
}
