package org.eolang.opeo.ast;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

public final class Duplicate implements AstNode, Typed, Linked {

    private final AtomicBoolean convertedToOpcodes;
    private final AtomicBoolean convertedToXmir;

    private final AtomicReference<AstNode> original;

    public Duplicate(final AstNode original) {
        this.original = new AtomicReference<>(original);
        this.convertedToOpcodes = new AtomicBoolean(false);
        this.convertedToXmir = new AtomicBoolean(false);
    }

    @Override
    public List<AstNode> opcodes() {
        if (this.convertedToOpcodes.getAndSet(true)) {
            return Collections.emptyList();
        }
        return Stream.concat(
            this.original.get().opcodes().stream(),
            Stream.of(new Opcode(Opcodes.DUP))
        ).collect(Collectors.toList());
//        return this.original.get().opcodes().stream().collect(Collectors.toList());
    }

    @Override
    public Type type() {
        if (this.original.get() instanceof Typed) {
            return ((Typed) this.original.get()).type();
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Iterable<Directive> toXmir() {
        if (this.convertedToXmir.getAndSet(true)) {
            return Collections.emptyList();
        }
        return new Directives()
            .add("o")
            .attr("base", "duplicated")
            .append(this.original.get().toXmir())
            .up();
    }

    public AstNode origin() {
        return this.original.get();
    }

    @Override
    public void link(final AstNode node) {
        this.original.set(node);
    }

    @Override
    public AstNode current() {
        return this.original.get();
    }
}
