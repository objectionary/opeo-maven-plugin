package org.eolang.opeo.ast;

public interface AstNode {

    /**
     * Print ast node and all it's children.
     * @return String output.
     */
    String print();

    /**
     * Node id.
     * @return Node id.
     */
    String id();
}
