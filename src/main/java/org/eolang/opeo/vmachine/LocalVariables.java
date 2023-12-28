package org.eolang.opeo.vmachine;

import java.util.ArrayList;
import java.util.List;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.This;

public final class LocalVariables {

    private List<AstNode> variables;

    public LocalVariables() {
        this(List.of(new This()));
    }

    public LocalVariables(final List<AstNode> variables) {
        this.variables = variables;
    }

    public AstNode variable(final int index) {
        return this.variables.get(index);
    }

}
