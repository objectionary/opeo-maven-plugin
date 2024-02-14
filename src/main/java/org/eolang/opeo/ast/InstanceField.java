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

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Access to a field.
 * @since 0.1
 */
public final class InstanceField implements AstNode {

    /**
     * Object reference from which the field is accessed.
     */
    private final AstNode source;

    /**
     * Field attributes.
     */
    private final Attributes attributes;

    /**
     * Constructor.
     * @param source Object reference from which the field is accessed
     * @param name Field name
     */
    public InstanceField(
        final AstNode source,
        final String name
    ) {
        this(source, name, "I");
    }

    /**
     * Constructor.
     * @param source Object reference from which the field is accessed
     * @param name Field name
     * @param descriptor Field descriptor
     */
    public InstanceField(
        final AstNode source,
        final String name,
        final String descriptor
    ) {
        this(source, new Attributes().name(name).type("field").descriptor(descriptor));
    }

    /**
     * Constructor.
     * @param source Object reference from which the field is accessed
     * @param attributes Field attributes
     */
    public InstanceField(final AstNode source, final Attributes attributes) {
        this.source = source;
        this.attributes = attributes;
    }

    /**
     * Store the field opcode. See {@link Opcodes#PUTFIELD}.
     * @return Opcode node to store the field. See {@link Opcode}
     */
    public AstNode store() {
        return new Opcode(
            Opcodes.PUTFIELD,
            this.attributes.owner(),
            this.attributes.name(),
            this.attributes.descriptor()
        );
    }

    /**
     * Get the object reference.
     * @return Object reference.
     */
    public AstNode instance() {
        return this.source;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives()
            .add("o")
            .attr("base", String.format(".%s", this.attributes.name()))
            .attr("scope", this.attributes)
            .append(this.source.toXmir())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(0);
        res.addAll(this.source.opcodes());
        res.add(
            new Opcode(
                Opcodes.GETFIELD,
                this.attributes.owner(),
                this.attributes.name(),
                this.attributes.descriptor()
            )
        );
        return res;
    }
}
