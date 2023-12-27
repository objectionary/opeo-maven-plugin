package org.eolang.opeo.ast;

import java.util.concurrent.atomic.AtomicReference;
import org.xembly.Directive;

public final class Reference implements AstNode {

    private final AtomicReference<AstNode> ref;

    public Reference() {
        this(new AtomicReference<>());
    }

    public Reference(final AtomicReference<AstNode> ref) {
        this.ref = ref;
    }

    public void link(final AstNode node) {
        this.ref.set(node);
    }

    @Override
    public String print() {
        return this.ref.get().print();
    }

    @Override
    public Iterable<Directive> toXmir() {
        return this.ref.get().toXmir();
    }

    @Override
    public String identifier() {
        return this.ref.get().identifier();
    }
}
