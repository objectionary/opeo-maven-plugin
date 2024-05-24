package org.eolang.opeo.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

public final class Popped implements AstNode, Typed {

    private final AstNode node;

    public Popped(final AstNode node) {
        this.node = node;
    }

    @Override
    public List<AstNode> opcodes() {
        return Stream.concat(
            this.node.opcodes().stream(),
            Stream.of(new Opcode(Opcodes.POP))
        ).collect(Collectors.toList());
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives()
            .add("o").attr("base", ".ignore-result")
            .append(this.node.toXmir())
            .up();
    }

    @Override
    public Type type() {
        if (this.node instanceof Typed) {
            return ((Typed) this.node).type();
        } else {
            throw new IllegalStateException(String.format("Node '%s' is not typed", this.node));
        }
    }
}
