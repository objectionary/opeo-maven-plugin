package org.eolang.opeo.ast;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

public final class Duplicate implements AstNode, Typed {

    private final AtomicBoolean convertedToOpcodes;
    private final AtomicBoolean convertedToXmir;

    private final AstNode original;

    public Duplicate(final AstNode original) {
        this.original = original;
        this.convertedToOpcodes = new AtomicBoolean(false);
        this.convertedToXmir = new AtomicBoolean(false);
    }

    @Override
    public List<AstNode> opcodes() {
        if (this.convertedToOpcodes.getAndSet(true)) {
            return Collections.emptyList();
        }
        return Stream.concat(
            this.original.opcodes().stream(),
            Stream.of(new Opcode(Opcodes.DUP))
        ).collect(Collectors.toList());
    }

    @Override
    public Type type() {
        if (this.original instanceof Typed) {
            return ((Typed) this.original).type();
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Iterable<Directive> toXmir() {
        if (convertedToXmir.getAndSet(true)) {
            return Collections.emptyList();
        }
        return new Directives()
            .add("o")
            .attr("base", "duplicated")
            .append(this.original.toXmir())
            .up();
    }

    public AstNode origin() {
        return this.original;
    }
}
