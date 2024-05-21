package org.eolang.opeo.ast;

import java.util.Collections;
import java.util.List;
import org.xembly.Directive;

public final class Empty implements AstNode {
    @Override
    public List<AstNode> opcodes() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<Directive> toXmir() {
        return Collections.emptyList();
    }
}
