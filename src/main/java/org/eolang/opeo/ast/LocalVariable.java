package org.eolang.opeo.ast;

import java.util.List;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

public final class LocalVariable implements AstNode {

    /**
     * The prefix of the variable.
     */
    private static final String PREFIX = "local";

    private final int identifier;

    private final Attributes attributes;

    /**
     * Constructor.
     * @param identifier The identifier of the variable.
     * @param type The type of the variable.
     */
    public LocalVariable(final int identifier, final Type type) {
        this(
            identifier,
            new Attributes().descriptor(type.getDescriptor())
        );
    }

    public LocalVariable(final int identifier, final Attributes attributes) {
        this.identifier = identifier;
        this.attributes = attributes;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives().add("o")
            .attr("base", this.name()).up();
    }

    @Override
    public List<AstNode> opcodes() {
        return this.load();
    }

    public String name() {
        return String.format("%s%d", LocalVariable.PREFIX, this.identifier);
    }

    /**
     * Load the variable opcodes.
     * @return Opcodes.
     */
    private List<AstNode> load() {
        final List<AstNode> result;
        final Type type = Type.getType(this.attributes.descriptor());
        if (type.equals(Type.INT_TYPE)) {
            result = List.of(new Opcode(Opcodes.ILOAD, this.identifier));
        } else if (type.equals(Type.DOUBLE_TYPE)) {
            result = List.of(new Opcode(Opcodes.DLOAD, this.identifier));
        } else if (type.equals(Type.LONG_TYPE)) {
            result = List.of(new Opcode(Opcodes.LLOAD, this.identifier));
        } else if (type.equals(Type.FLOAT_TYPE)) {
            result = List.of(new Opcode(Opcodes.FLOAD, this.identifier));
        } else if (type.equals(Type.BOOLEAN_TYPE)) {
            result = List.of(new Opcode(Opcodes.ILOAD, this.identifier));
        } else if (type.equals(Type.CHAR_TYPE)) {
            result = List.of(new Opcode(Opcodes.ILOAD, this.identifier));
        } else if (type.equals(Type.BYTE_TYPE)) {
            result = List.of(new Opcode(Opcodes.ILOAD, this.identifier));
        } else if (type.equals(Type.SHORT_TYPE)) {
            result = List.of(new Opcode(Opcodes.ILOAD, this.identifier));
        } else {
            result = List.of(new Opcode(Opcodes.ALOAD, this.identifier));
        }
        return result;
    }

}
