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

import java.util.Arrays;
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
public final class LocalVariable implements AstNode, Typed {

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
        this(LocalVariable.videntifier(node), new Attributes(node.firstChild()));
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

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives().add("o")
            .attr("base", String.format("%s%d", LocalVariable.PREFIX, this.identifier))
            .append(this.attributes.toXmir())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        return Arrays.asList(new Opcode(this.type().getOpcode(Opcodes.ILOAD), this.identifier));
    }

    @Override
    public Type type() {
        return Type.getType(this.attributes.descriptor());
    }

    /**
     * Store opcode for the variable.
     * See {@link org.objectweb.asm.Opcodes#ISTORE}.
     * @return Opcode to store the variable. See {@link Opcode}.
     */
    public AstNode store() {
        return new Opcode(this.type().getOpcode(Opcodes.ISTORE), this.identifier);
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
}
