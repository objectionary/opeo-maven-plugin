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
import lombok.ToString;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Invocation output node.
 * @since 0.1
 */
@ToString
public final class Invocation implements AstNode {

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
     * @param method Method name
     * @param descriptor Method descriptor
     * @param args Arguments
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public Invocation(
        final AstNode source,
        final String method,
        final String descriptor,
        final AstNode... args
    ) {
        this(source, method, Arrays.asList(args), descriptor);
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
    public String print() {
        return String.format(
            "(%s).%s%s",
            this.source.print(),
            this.attributes.name(),
            this.args()
        );
    }

    @Override
    public Iterable<Directive> toXmir() {
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
            .attr("scope", this.attributes)
            .append(this.source.toXmir());
        this.arguments.stream().map(AstNode::toXmir).forEach(directives::append);
        return directives.up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(0);
        res.addAll(this.source.opcodes());
        this.arguments.stream().map(AstNode::opcodes).forEach(res::addAll);
        res.add(
            new Opcode(
                Opcodes.INVOKEVIRTUAL,
                this.attributes.owner(),
                this.attributes.name(),
                this.attributes.descriptor()
            )
        );
        return res;
    }

    /**
     * Print arguments.
     * @return Arguments
     */
    private String args() {
        final String result;
        if (this.arguments.isEmpty()) {
            result = "";
        } else {
            result = this.arguments.stream().map(AstNode::print)
                .collect(Collectors.joining(" ", " ", ""));
        }
        return result;
    }
}
