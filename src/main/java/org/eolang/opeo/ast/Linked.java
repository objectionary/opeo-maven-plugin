package org.eolang.opeo.ast;

public interface Linked {

    void link(AstNode node);

    AstNode current();
}
