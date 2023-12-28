package org.eolang.opeo.ast;

import org.xembly.Directive;

public final class Add implements AstNode {

    private final AstNode left;
    private final AstNode right;

    public Add(final AstNode left, final AstNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String print() {
        return String.format("(%s) + (%s)", this.left.print(), this.right.print());
    }

    @Override
    public Iterable<Directive> toXmir() {
        throw new UnsupportedOperationException(
            "#toXmir() not yet implemented for Add instruction"
        );
    }
}
