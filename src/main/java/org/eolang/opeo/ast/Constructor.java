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
    private final String ctype;

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
        this.ctype = type;
        this.attributes = attrs;
        this.arguments = args;
    }

    @Override
    public Iterable<Directive> toXmir() {
        final Directives directives = new Directives();
        directives.add("o")
            .attr("base", ".new")
            .attr("scope", this.attributes)
            .add("o")
            .attr("base", this.ctype)
            .up();
        this.arguments.stream().map(AstNode::toXmir).forEach(directives::append);
        return directives.up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(0);
        res.add(new Opcode(Opcodes.NEW, this.ctype));
        res.add(new Opcode(Opcodes.DUP));
        this.arguments.stream().map(AstNode::opcodes).forEach(res::addAll);
        res.add(
            new Opcode(
                Opcodes.INVOKESPECIAL,
                this.ctype,
                "<init>",
                this.attributes.descriptor()
            )
        );
        return res;
    }

    @Override
    public Type type() {
        return Type.getObjectType(this.ctype);
    }
}
