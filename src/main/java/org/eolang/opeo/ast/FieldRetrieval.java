/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.opeo.ast;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.compilation.Parser;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Field retrieval.
 * @since 0.2
 */
@ToString
@EqualsAndHashCode
public final class FieldRetrieval implements AstNode, Typed {

    /**
     * The field to access.
     */
    private final Field field;

    /**
     * Constructor.
     * @param node XML node
     * @param parser Parser
     */
    public FieldRetrieval(final XmlNode node, final Parser parser) {
        this(new Field(node.firstChild(), parser));
    }

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
        this(new Field(instance, attributes));
    }

    /**
     * Constructor.
     * @param field The field to access
     */
    public FieldRetrieval(final Field field) {
        this.field = field;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives().add("o")
            .attr("base", ".get-field")
            .append(this.field.toXmir())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        return this.field.load();
    }

    @Override
    public Type type() {
        return this.field.type();
    }
}
