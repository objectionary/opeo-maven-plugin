package org.eolang.opeo.ast;

import java.util.List;
import java.util.stream.Collectors;

public class Constructor implements AstNode {

    private final String type;

    private final String reference;

    private final List<AstNode> arguments;

    public Constructor(
        final String type,
        final String reference,
        final List<AstNode> arguments
    ) {
        this.type = type;
        this.reference = reference;
        this.arguments = arguments;
    }

    @Override
    public String print() {
        return String.format(
            "new %s(%s)",
            this.type,
            this.arguments.stream().map(AstNode::print).collect(Collectors.joining(","))
        );
    }

    @Override
    public String id() {
        return this.reference;
    }
}
