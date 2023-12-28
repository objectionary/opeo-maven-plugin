package org.eolang.opeo.ast;

import org.xembly.Directive;

public final class This implements AstNode {

    @Override
    public String print() {
        return "this";
    }

    @Override
    public Iterable<Directive> toXmir() {
        return null;
    }

}
