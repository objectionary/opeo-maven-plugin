package org.eolang.opeo.ast;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class InstanceInvokation implements AstNode {

    private final AstNode source;

    private final String method;
    private final List<AstNode> arguments;

    public InstanceInvokation(
        final AstNode source,
        final String method,
        final List<AstNode> arguments
    ) {
        this.source = source;
        this.method = method;
        this.arguments = arguments;
    }

    @Override
    public String print() {
        return String.format(
            "(%s).%s%s",
            this.source.print(),
            this.method,
            this.args()
        );
    }

    /**
     * Print arguments.
     * @return Arguments
     */
    private String args() {
        final String result;
        if (this.arguments.isEmpty()) {
            result = "";
        } else {
            result = this.arguments.stream().map(AstNode::print)
                .collect(Collectors.joining(" ", " ", ""));
        }
        return result;
    }

    @Override
    public String identifier() {
        return UUID.randomUUID().toString();
    }
}
