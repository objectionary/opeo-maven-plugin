package org.eolang.opeo.ast;

import org.objectweb.asm.Type;
import org.xembly.Directive;

public final class Variable implements AstNode {
    private final Type type;
    private final int identifier;

    public Variable(
        final Type type,
        final int identifier
    ) {
        this.type = type;
        this.identifier = identifier;
    }

    @Override
    public String print() {
        return null;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return null;
    }
}
