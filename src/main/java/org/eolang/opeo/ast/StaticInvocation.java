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
 * Static invocation ast node.
 * @since 0.1
 */
@ToString
@EqualsAndHashCode
public final class StaticInvocation implements AstNode, Typed {

    /**
     * Method attributes.
     */
    private final Attributes attributes;

    /**
     * Class on which the method is invoked.
     */
    private final Owner owner;

    /**
     * Arguments.
     */
    private final List<AstNode> args;

    /**
     * Constructor.
     * @param owner Owner class name
     * @param name Method name
     * @param descriptor Method descriptor
     * @param arguments Arguments
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public StaticInvocation(
        final String owner,
        final String name,
        final String descriptor,
        final AstNode... arguments
    ) {
        this(owner, name, descriptor, Arrays.asList(arguments));
    }

    /**
     * Constructor.
     * @param owner Owner class name
     * @param name Method name
     * @param descriptor Method descriptor
     * @param arguments Arguments
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public StaticInvocation(
        final String owner,
        final String name,
        final String descriptor,
        final List<AstNode> arguments
    ) {
        this(
            new Attributes().name(name).descriptor(descriptor).type("static"),
            new Owner(owner),
            arguments
        );
    }

    /**
     * Constructor.
     * @param node XML node
     * @param arguments Arguments
     */
    public StaticInvocation(final XmlNode node, final List<AstNode> arguments) {
        this(
            StaticInvocation.xattrs(node),
            StaticInvocation.xowner(node),
            arguments
        );
    }

    /**
     * Constructor.
     * @param node XML node
     * @param arguments Arguments
     */
    public StaticInvocation(final XmlNode node, final AstNode... arguments) {
        this(node, Arrays.asList(arguments));
    }

    /**
     * Constructor.
     * @param attributes Method attributes
     * @param owner Owner class name
     * @param arguments Arguments
     */
    public StaticInvocation(
        final Attributes attributes,
        final Owner owner,
        final AstNode... arguments
    ) {
        this(attributes, owner, List.of(arguments));
    }

    /**
     * Constructor.
     * @param attributes Method attributes
     * @param owner Owner class name
     * @param arguments Arguments
     */
    public StaticInvocation(
        final Attributes attributes,
        final Owner owner,
        final List<AstNode> arguments
    ) {
        this.attributes = attributes.type("static");
        this.owner = owner;
        this.args = arguments;
    }

    @Override
    public Iterable<Directive> toXmir() {
        final Directives directives = new Directives();
        directives.add("o")
            .attr("base", String.format(".%s", this.attributes.name()))
            .attr("scope", this.attributes);
        directives.append(this.owner.toXmir());
        this.args.stream().map(AstNode::toXmir).forEach(directives::append);
        return directives.up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(0);
        this.args.stream().map(AstNode::opcodes).forEach(res::addAll);
        res.add(
            new Opcode(
                Opcodes.INVOKESTATIC,
                this.owner.toString().replace('.', '/'),
                this.attributes.name(),
                this.attributes.descriptor()
            )
        );
        return res;
    }

    @Override
    public Type type() {
        return Type.getReturnType(this.attributes.descriptor());
    }

    /**
     * Extracts attributes from the node.
     * @param node XML node
     * @return Attributes
     */
    private static Attributes xattrs(final XmlNode node) {
        return node.attribute("scope").map(Attributes::new).orElseThrow(
            () -> new IllegalArgumentException(
                String.format("Can't retrieve static invocation attributes from the node %s", node)
            )
        );
    }

    /**
     * Extracts owner from the node.
     * @param node XML node
     * @return Owner
     */
    private static Owner xowner(final XmlNode node) {
        return node.child("o").attribute("base").map(Owner::new).orElseThrow(
            () -> new IllegalArgumentException(
                String.format("Can't retrieve static invocation owner from the node %s", node)
            )
        );
    }
}
