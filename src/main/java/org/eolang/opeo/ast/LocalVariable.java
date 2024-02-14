package org.eolang.opeo.ast;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * A local variable.
 * <p>{@code
 *   int local1;
 * }</p>
 * @since 0.2
 */
@ToString
@EqualsAndHashCode
public final class LocalVariable implements AstNode {

    /**
     * The prefix of the variable.
     */
    private static final String PREFIX = "local";

    /**
     * The identifier of the variable.
     */
    private final int identifier;

    /**
     * The attributes of the variable.
     * These attributes hold the type of the variable.
     */
    private final Attributes attributes;

    /**
     * Constructor.
     * @param node The XML node that represents variable.
     */
    public LocalVariable(final XmlNode node) {
        this(LocalVariable.videntifier(node), LocalVariable.vattributes(node));
    }

    /**
     * Constructor.
     * @param identifier The identifier of the variable.
     * @param type The type of the variable.
     */
    public LocalVariable(final int identifier, final Type type) {
        this(identifier, new Attributes().descriptor(type.getDescriptor()).type("local"));
    }

    /**
     * Constructor.
     * @param identifier The identifier of the variable.
     * @param attributes The attributes of the variable.
     */
    private LocalVariable(final int identifier, final Attributes attributes) {
        this.identifier = identifier;
        this.attributes = attributes;
    }

    /**
     * Store opcode for the variable.
     * See {@link org.objectweb.asm.Opcodes#ISTORE}.
     * @return Opcode to store the variable. See {@link Opcode}.
     */
    public AstNode store() {
        return new Opcode(
            Type.getType(this.attributes.descriptor()).getOpcode(Opcodes.ISTORE),
            this.identifier
        );
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives().add("o")
            .attr("base", String.format("%s%d", LocalVariable.PREFIX, this.identifier))
            .attr("scope", this.attributes)
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        return this.load();
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
            ).substring(LocalVariable.PREFIX.length())
        );
    }

    /**
     * Get the attributes of the variable.
     * @param node The XML node that represents variable.
     * @return The attributes.
     */
    private static Attributes vattributes(final XmlNode node) {
        return new Attributes(
            node.attribute("scope")
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
