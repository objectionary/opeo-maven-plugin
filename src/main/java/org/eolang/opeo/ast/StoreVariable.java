package org.eolang.opeo.ast;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

@ToString
@EqualsAndHashCode
public final class StoreVariable implements AstNode {
    /**
     * The prefix of the variable.
     */
    private static final String PREFIX = "slocal";

    /**
     * The type of the variable.
     */
    private final Type type;

    /**
     * The identifier of the variable.
     */
    private final int identifier;

    /**
     * Constructor.
     * @param node The XML node that represents variable.
     */
    public StoreVariable(final XmlNode node) {
        this(StoreVariable.vtype(node), StoreVariable.videntifier(node));
    }

    /**
     * Constructor.
     * @param type The type of the variable.
     * @param identifier The identifier of the variable.
     */
    public StoreVariable(
        final Type type,
        final int identifier
    ) {
        this.type = type;
        this.identifier = identifier;
    }

    @ToString.Include
    @Override
    public String print() {
        return String.format(
            "%s%d%s", StoreVariable.PREFIX, this.identifier, this.type.getClassName());
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives()
            .add("o")
            .attr("base", String.format("%s%d", StoreVariable.PREFIX, this.identifier))
            .attr("scope", this.type.getDescriptor())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> result;
        if (this.type.equals(Type.INT_TYPE)) {
            result = List.of(new Opcode(Opcodes.ISTORE, this.identifier));
        } else if (this.type.equals(Type.DOUBLE_TYPE)) {
            result = List.of(new Opcode(Opcodes.DSTORE, this.identifier));
        } else if (this.type.equals(Type.LONG_TYPE)) {
            result = List.of(new Opcode(Opcodes.LSTORE, this.identifier));
        } else if (this.type.equals(Type.FLOAT_TYPE)) {
            result = List.of(new Opcode(Opcodes.FSTORE, this.identifier));
        } else if (this.type.equals(Type.BOOLEAN_TYPE)) {
            result = List.of(new Opcode(Opcodes.ISTORE, this.identifier));
        } else if (this.type.equals(Type.CHAR_TYPE)) {
            result = List.of(new Opcode(Opcodes.ISTORE, this.identifier));
        } else if (this.type.equals(Type.BYTE_TYPE)) {
            result = List.of(new Opcode(Opcodes.ISTORE, this.identifier));
        } else if (this.type.equals(Type.SHORT_TYPE)) {
            result = List.of(new Opcode(Opcodes.ISTORE, this.identifier));
        } else if (this.type.equals(Type.VOID_TYPE)) {
            result = List.of(new Opcode(Opcodes.ISTORE, this.identifier));
        } else {
            result = List.of(new Opcode(Opcodes.ASTORE, this.identifier));
        }
        return result;
    }

    /**
     * Get the identifier of the variable.
     * @param node The XML node that represents variable.
     * @return The identifier.
     */
    private static int videntifier(final XmlNode node) {
        return Integer.parseInt(
            node.attribute("base").orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "Can't recognize variable node: %n%s%nWe expected to find 'base' attribute",
                        node
                    )
                )
            ).substring(StoreVariable.PREFIX.length())
        );
    }

    /**
     * Get the type of the variable.
     * @param node The XML node that represents variable.
     * @return The type.
     */
    private static Type vtype(final XmlNode node) {
        return Type.getType(node.attribute("scope")
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "Can't recognize variable node: %n%s%nWe expected to find 'scope' attribute",
                        node
                    )
                )
            )
        );
    }
}
