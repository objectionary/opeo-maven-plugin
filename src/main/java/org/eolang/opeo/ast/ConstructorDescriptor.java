package org.eolang.opeo.ast;

import java.util.Arrays;
import java.util.List;
import org.objectweb.asm.Type;

public final class ConstructorDescriptor {

    private final List<AstNode> args;

    public ConstructorDescriptor(final List<AstNode> arguments) {
        this.args = arguments;
    }

    @Override
    public String toString() {
        return Type.getMethodDescriptor(
            Type.VOID_TYPE,
            this.args.stream()
                .peek(this::verify)
                .map(Typed.class::cast)
                .map(Typed::type)
                .toArray(Type[]::new)
        );
    }

    private void verify(final AstNode node) {
        if (!(node instanceof Typed)) {
            throw new IllegalArgumentException(
                String.format(
                    "Node %s is not typed, all constructor arguments must be typed: %s",
                    node,
                    Arrays.toString(this.args.toArray())
                )
            );
        }
    }
}
