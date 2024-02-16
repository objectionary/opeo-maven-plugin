package org.eolang.opeo.ast;

import lombok.EqualsAndHashCode;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

@EqualsAndHashCode
public final class Owner implements Xmir {

    @EqualsAndHashCode.Exclude
    private final Type type;

    public Owner(final String owner) {
        this(Type.getObjectType(owner));
    }

    public Owner(final Type type) {
        this.type = type;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives()
            .add("o")
            .attr("base", this.toString())
            .up();
    }

    @EqualsAndHashCode.Include
    @Override
    public String toString() {
        return this.type.getClassName();
    }
}
