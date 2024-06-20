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
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.compilation.Parser;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Constructor output node.
 * @since 0.1
 */
public final class Constructor implements AstNode, Typed {

    /**
     * Constructor type.
     */
    private final AstNode ctype;

    /**
     * Constructor attributes.
     */
    private final Attributes attributes;

    /**
     * Constructor arguments.
     */
    private final List<AstNode> arguments;

    /**
     * Constructor.
     * @param type Constructor type
     * @param arguments Constructor arguments
     */
    public Constructor(
        final String type,
        final AstNode... arguments
    ) {
        this(type, Arrays.asList(arguments));
    }

    /**
     * Constructor.
     * @param type Constructor type
     * @param arguments Constructor arguments
     */
    public Constructor(final String type, final List<AstNode> arguments) {
        this(type, new Attributes(), arguments);
    }

    /**
     * Constructor.
     * @param type Constructor type
     * @param attrs Constructor attributes
     * @param arguments Constructor arguments
     */
    public Constructor(
        final String type,
        final Attributes attrs,
        final AstNode... arguments
    ) {
        this(type, attrs, Arrays.asList(arguments));
    }

    /**
     * Constructor.
     * @param type Constructor type
     * @param attrs Constructor attributes
     * @param args Constructor arguments
     */
    public Constructor(
        final String type,
        final Attributes attrs,
        final List<AstNode> args
    ) {
        this(new NewAddress(type), attrs, args);
    }

    /**
     * Constructor.
     * @param node Xmir representation of constructor.
     */
    public Constructor(final XmlNode node, final Parser parser) {
        this(
            Constructor.xtarget(node, parser),
            new Attributes(node),
            Constructor.xargs(node, parser)
        );
    }

    private static AstNode xtarget(final XmlNode node, final Parser parser) {
        return null;
    }

    private static List<AstNode> xargs(final XmlNode node, final Parser parser) {
        return null;
    }

    /**
     * Constructor.
     * @param ctype Constructor type
     * @param attributes Constructor attributes
     * @param arguments Constructor arguments
     */
    public Constructor(
        final AstNode ctype,
        final Attributes attributes,
        final List<AstNode> arguments
    ) {
        this.ctype = ctype;
        this.attributes = attributes;
        this.arguments = arguments;
    }

    @Override
    public Iterable<Directive> toXmir() {
        final Directives directives = new Directives();
        directives.add("o")
            .attr("base", ".new")
            .attr("scope", this.attributes)
            .append(this.ctype.toXmir());
        this.arguments.stream().map(AstNode::toXmir).forEach(directives::append);
        return directives.up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(0);
        res.addAll(this.ctype.opcodes());
        this.arguments.stream().map(AstNode::opcodes).forEach(res::addAll);
        res.add(
            new Opcode(
                Opcodes.INVOKESPECIAL,
                this.type(this.ctype),
                "<init>",
                this.attributes.descriptor(),
                this.attributes.interfaced()
            )
        );
        return res;
    }

    @Override
    public Type type() {
        return ((Typed) this.ctype).type();
    }

    /**
     * Get a type of constructor.
     * @param node Constructor node.
     * @return Type of constructor.
     */
    private String type(final AstNode node) {
        final String result;
        if (node instanceof NewAddress) {
            result = ((NewAddress) node).typeAsString();
        } else if (node instanceof Reference) {
            result = this.type(((Reference) node).object());
        } else if (node instanceof Duplicate) {
            result = this.type(((Duplicate) node).origin());
        } else {
            throw new IllegalStateException(
                String.format(
                    "Unexpected node type: %s",
                    node.getClass().getCanonicalName()
                )
            );
        }
        return result;
    }
}
