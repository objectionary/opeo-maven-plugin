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

import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * This output node.
 * In java, it is represented as the keyword "this".
 * In EO, it is represented as the keyword "$".
 * @since 0.1
 */
@ToString
@EqualsAndHashCode
public final class This implements AstNode, Typed {

    /**
     * Attributes.
     */
    private final Attributes attributes;

    /**
     * Default ctor.
     * If no type is provided, the default type is {@link Object}.
     * @since 0.2
     */
    public This() {
        this(Type.getType(Object.class));
    }

    /**
     * Constructor.
     * @param node XML node.
     */
    public This(final XmlNode node) {
        this(new Attributes(node));
    }

    /**
     * Constructor.
     * @param type Type of this node.
     */
    public This(final Type type) {
        this(new Attributes().descriptor(type.getClassName()));
    }

    /**
     * Constructor.
     * @param attributes Attributes.
     */
    public This(final Attributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives()
            .add("o")
            .attr("base", "$")
            .append(this.attributes.toXmir())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        return Collections.singletonList(new Opcode(Opcodes.ALOAD, 0));
    }

    @Override
    public Type type() {
        return Type.getObjectType(this.attributes.descriptor());
    }
}
