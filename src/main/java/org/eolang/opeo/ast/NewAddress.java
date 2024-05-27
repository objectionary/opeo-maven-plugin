package org.eolang.opeo.ast;

import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesData;
import org.eolang.jeo.representation.xmir.HexString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Same as reference?
 */
@ToString
@EqualsAndHashCode
public final class NewAddress implements AstNode, Typed {

    private final String type;

    public NewAddress(final XmlNode node) {
        this(NewAddress.parse(node));
    }

    public NewAddress(final String type) {
        this.type = type;
    }

    @Override
    public List<AstNode> opcodes() {
        return Collections.singletonList(new Opcode(Opcodes.NEW, this.type));
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives()
            .add("o")
            .attr("base", ".new-type")
            .append(new DirectivesData(this.type))
            .up();
    }

    private static String parse(final XmlNode node) {
        return new HexString(node.firstChild().text()).decode();
    }

    @Override
    public Type type() {
        return Type.getObjectType(this.type);
    }

    public String typeString() {
        return this.type;
    }
}
