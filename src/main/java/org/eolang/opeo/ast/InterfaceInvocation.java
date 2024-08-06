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
 * Interface invocation.
 * @since 0.2
 * @todo #173:90min Remove code duplication between Invocations.
 *  I just copied some code from other invocations and changed the opcode.
 *  It means that we have a code duplication. We need to remove it somehow.
 *  Also pay attention that {@link #xargs} method is duplicate of the same method from
 *  {@link org.eolang.opeo.compilation.XmirParser#args} class.
 */
@ToString
@EqualsAndHashCode
public final class InterfaceInvocation implements AstNode, Typed {
    /**
     * Source or target on which the invocation is performed.
     */
    private final AstNode source;

    /**
     * Method attributes.
     */
    private final Attributes attrs;

    /**
     * Arguments of the method.
     */
    private final List<AstNode> arguments;

    /**
     * Constructor.
     * @param node XML node.
     * @param parser Parser, which can extract AstNode from XmlNode.
     */
    public InterfaceInvocation(final XmlNode node, final Parser parser) {
        this(
            InterfaceInvocation.xsource(node, parser),
            new Attributes(node.firstChild()),
            new Arguments(node, parser, 2).toList()
        );
    }

    /**
     * Constructor.
     * @param source Source or target on which the invocation is performed
     * @param attributes Method attributes.
     * @param args Arguments of the method.
     */
    public InterfaceInvocation(
        final AstNode source,
        final Attributes attributes,
        final AstNode... args
    ) {
        this(source, attributes, Arrays.asList(args));
    }

    /**
     * Constructor.
     * @param source Source or target on which the invocation is performed
     * @param attributes Method attributes.
     * @param args Arguments of the method.
     */
    public InterfaceInvocation(
        final AstNode source,
        final Attributes attributes,
        final List<AstNode> args
    ) {
        this.source = source;
        this.attrs = attributes.type("interface");
        this.arguments = args;
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
        res.add(
            new Opcode(
                Opcodes.INVOKEINTERFACE,
                this.attrs.owner(),
                this.attrs.name(),
                this.attrs.descriptor(),
                this.attrs.interfaced()
            )
        );


//        final Typed owner = (Typed) this.source;
//
//        res.add(
//            new Opcode(
//                Opcodes.INVOKEINTERFACE,
//                owner.type().getClassName().replace('.', '/'),
//                this.attrs.name(),
//                this.attrs.descriptor(),
//                this.attrs.interfaced()
//            )
//        );
        return res;
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
            .attr("base", String.format(".%s", this.attrs.name()))
            .append(this.attrs.toXmir())
            .append(this.source.toXmir());
        this.arguments.stream().map(AstNode::toXmir).forEach(directives::append);
        return directives.up();
    }

    @Override
    public Type type() {
        return Type.getReturnType(this.attrs.descriptor());
    }

    /**
     * Extracts source from the node.
     * @param node XML node.
     * @param parser Parser.
     * @return Source.
     */
    private static AstNode xsource(final XmlNode node, final Parser parser) {
        return parser.parse(node.children().collect(Collectors.toList()).get(1));
    }
}
