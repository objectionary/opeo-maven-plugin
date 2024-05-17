package org.eolang.opeo.ast;

import java.util.Collections;
import java.util.List;
import org.eolang.jeo.representation.directives.DirectivesData;
import org.eolang.jeo.representation.xmir.HexString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;

public final class ClassConstant implements AstNode {

    private final String name;


    public ClassConstant(final XmlNode node) {
        this(ClassConstant.xname(node));
    }

    public ClassConstant(final String name) {
        this.name = name;
    }

    @Override
    public List<AstNode> opcodes() {
        return Collections.singletonList(
            new Opcode(
                Opcodes.LDC,
                Type.getType(this.name)
            )
        );
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new DirectivesData(this.name);
    }

    private static String xname(final XmlNode node) {
        return new HexString(node.text()).decode();
    }
}
