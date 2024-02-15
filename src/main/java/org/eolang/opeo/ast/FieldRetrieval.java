package org.eolang.opeo.ast;

import java.util.List;
import org.xembly.Directive;
import org.xembly.Directives;

public final class FieldRetrieval implements AstNode {

    private final InstanceField field;

    /**
     * Constructor.
     * @param instance Object reference from which the field is accessed
     * @param name Field name
     */
    public FieldRetrieval(final AstNode instance, final String name) {
        this(instance, name, "I");
    }

    /**
     * Constructor.
     * @param instance Object reference from which the field is accessed
     * @param name Field name
     * @param descriptor Field descriptor
     */
    public FieldRetrieval(final AstNode instance, final String name, final String descriptor) {
        this(instance, new Attributes().name(name).type("field").descriptor(descriptor));
    }

    /**
     * Constructor.
     * @param instance Object reference from which the field is accessed
     * @param attributes Field attributes
     */
    public FieldRetrieval(final AstNode instance, final Attributes attributes) {
        this(new InstanceField(instance, attributes));
    }


    public FieldRetrieval(final InstanceField field) {
        this.field = field;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives().add("o")
            .attr("base", ".getfield")
            .append(this.field.toXmir())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        return this.field.load();
    }
}
