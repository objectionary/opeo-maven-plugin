package org.eolang.opeo.ast;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class Root implements AstNode {
    private final List<AstNode> children;

    public Root() {
        this.children = new LinkedList<>();
    }

    @Override
    public String print() {
        return children.stream().map(AstNode::print).collect(Collectors.joining(";"));
    }

    public Optional<AstNode> child(final String id) {
        return this.children.stream().filter(node -> node.id().equals(id)).findFirst();
    }

    public void append(final AstNode node) {
        this.children.add(node);
    }

    public void disconnect(final AstNode node) {
        this.children.remove(node);
    }

    @Override
    public String id() {
        return UUID.randomUUID().toString();
    }
}
