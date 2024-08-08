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
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.compilation.Parser;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Invocation output node.
 * @since 0.1
 */
@ToString
@EqualsAndHashCode
public final class Invocation implements AstNode, Typed {

    /**
     * Source or target on which the invocation is performed.
     */
    private final AstNode source;

    /**
     * Method attributes.
     */
    private final Attributes attributes;

    /**
     * Arguments.
     */
    private final List<AstNode> arguments;

    /**
     * Constructor.
     * @param node XML node
     * @param parser Parser for child nodes.
     */
    public Invocation(final XmlNode node, final Parser parser) {
        this(
            parser.parse(node.children().collect(Collectors.toList()).get(1)),
            new Attributes(node.firstChild()),
            new Arguments(node, parser, 2).toList()
        );
    }

    /**
     * Constructor.
     * @param source Source or target on which the invocation is performed
     * @param method Method name
     * @param args Arguments
     */
    public Invocation(
        final AstNode source,
        final String method,
        final AstNode... args
    ) {
        this(source, method, Arrays.asList(args));
    }

    /**
     * Constructor.
     * @param source Source or target on which the invocation is performed
     * @param attributes Method attributes
     * @param args Arguments
     */
    public Invocation(
        final AstNode source,
        final Attributes attributes,
        final AstNode... args
    ) {
        this(source, attributes, Arrays.asList(args));
    }

    /**
     * Constructor.
     * @param source Source or target on which the invocation is performed
     * @param method Method name
     * @param arguments Arguments
     */
    public Invocation(
        final AstNode source,
        final String method,
        final List<AstNode> arguments
    ) {
        this(source, method, arguments, "V()");
    }

    /**
     * Constructor.
     * @param source Source or target on which the invocation is performed
     * @param method Method name
     * @param arguments Arguments
     * @param descriptor Descriptor
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public Invocation(
        final AstNode source,
        final String method,
        final List<AstNode> arguments,
        final String descriptor
    ) {
        this(source, new Attributes().name(method).descriptor(descriptor), arguments);
    }

    /**
     * Constructor.
     * @param source Source or target on which the invocation is performed
     * @param attributes Method attributes
     * @param arguments Arguments
     */
    public Invocation(
        final AstNode source,
        final Attributes attributes,
        final List<AstNode> arguments
    ) {
        this.source = source;
        this.attributes = attributes.type("method");
        this.arguments = arguments;
    }

    @Override
    public Iterable<Directive> toXmir() {
        //fixme
        if (Objects.isNull(this.source)) {
            throw new IllegalArgumentException(
                String.format(
                    "Source and method must not be null, but they are %s",
                    this
                )
            );
        }
        final Directives directives = new Directives();
        directives.add("o")
            .attr("base", String.format(".%s", this.attributes.name()))
            .append(this.attributes.toXmir())
            .append(this.source.toXmir());
        this.arguments.stream().map(AstNode::toXmir).forEach(directives::append);
        return directives.up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(0);
        res.addAll(this.source.opcodes());
        this.arguments.stream().map(AstNode::opcodes).forEach(res::addAll);
        if (!(this.source instanceof Typed)) {
            throw new IllegalArgumentException(
                String.format(
                    "Source must be of type Typed, but it is %s. Most probably, we don't implement the type of the source yet.",
                    this.source
                )
            );
        }
        //@checkstyle MethodBodyCommentsCheck (10 line)
        // @todo #229:90min Avoid using the owner from the attributes.
        //  Instead, use the owner from the source.
        //  This will allow us to avoid using the owner in the attributes.
        //  Right now we have ad-hoc logic to determine the owner.
        final String owner;
        if (this.attributes.toString().contains("owner")) {
            owner = this.attributes.owner();
        } else {
            owner = ((Typed) this.source).type().getClassName();
        }
        res.add(
            new Opcode(
                Opcodes.INVOKEVIRTUAL,
                owner.replace('.', '/'),
                this.attributes.name(),
                this.attributes.descriptor(),
                this.attributes.interfaced()
            )
        );
        return res;
    }

    @Override
    public Type type() {
        return Type.getReturnType(this.attributes.descriptor());
    }
}
