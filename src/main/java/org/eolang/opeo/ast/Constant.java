package org.eolang.opeo.ast;

import java.util.Collections;
import java.util.List;
import org.eolang.jeo.representation.DataType;
import org.eolang.jeo.representation.directives.DirectivesData;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

public final class Constant implements AstNode, Typed {

    private final Object value;

    public Constant(final XmlNode node) {
        this(parse(node));
    }

    public Constant(final Object value) {
        this.value = value;
    }

    @Override
    public List<AstNode> opcodes() {
//        if (this.value instanceof Type) {
//            return Collections.singletonList(
//                new Opcode(Opcodes.LDC, ((Type) this.value).getClass()));
//        } else {
            return Collections.singletonList(new Opcode(Opcodes.LDC, this.value));
//        }
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives().add("o")
            .attr("base", "load-constant")
            .append(new DirectivesData(this.value))
            .up();
    }

    @Override
    public Type type() {
        if (this.value instanceof Type) {
            return (Type) this.value;
        } else {
            return Type.getType(this.value.getClass());
        }
    }

    private static Object parse(final XmlNode node) {
        final XmlNode child = node.firstChild();
        return DataType.find(child.attribute("base").orElseThrow())
            .decode(child.text());
    }
}
