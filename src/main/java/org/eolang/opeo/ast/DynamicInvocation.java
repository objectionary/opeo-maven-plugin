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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.directives.DirectivesData;
import org.eolang.jeo.representation.xmir.HexString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.compilation.Parser;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Dynamic invocation.
 * @since 0.5
 * @todo #329:90min Add Unit Test To Test DynamicInvocation Arguments.
 *  I added {@link DynamicInvocation#arguments} field althogether with parsing and translation
 *  logic. But I didn't test this logic. We need to add unit tests for arguments parsing
 *  and translation.
 */
public final class DynamicInvocation implements AstNode, Typed {

    /**
     * Name of the method.
     */
    private final String name;

    /**
     * Factory method reference.
     */
    private final Handle factory;

    /**
     * Method attributes.
     */
    private final Attributes attributes;

    /**
     * Factory method arguments.
     */
    private final List<Object> farguments;

    /**
     * Dynamic invocation arguments.
     */
    private final List<AstNode> arguments;

    /**
     * Constructor.
     * @param root XMIR node to parse.
     */
    public DynamicInvocation(final XmlNode root) {
        this(root, node -> new Empty());
    }

    /**
     * Constructor.
     * @param root XMIR node to parse.
     * @param parser Parser to find AST nodes of children.
     */
    public DynamicInvocation(final XmlNode root, final Parser parser) {
        this(root, root.children().collect(Collectors.toList()), parser);
    }

    /**
     * Constructor.
     * Added for efficiency to receive children nodes only once.
     * @param root XMIR node to parse.
     * @param chldren XMIR node children.
     * @param parser Parser to find AST nodes of children.
     */
    public DynamicInvocation(final XmlNode root, final List<XmlNode> chldren, final Parser parser) {
        this(
            DynamicInvocation.xname(root),
            DynamicInvocation.xfactory(chldren),
            DynamicInvocation.xdescriptor(chldren),
            DynamicInvocation.xfarguments(chldren),
            DynamicInvocation.xargs(chldren, parser)
        );
    }

    /**
     * Constructor.
     * @param name Name of the method.
     * @param factory Factory method reference.
     * @param descriptor Method descriptor.
     * @param arguments Factory method arguments.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DynamicInvocation(
        final String name,
        final Handle factory,
        final String descriptor,
        final List<Object> arguments
    ) {
        this(name, factory, descriptor, arguments, Collections.emptyList());
    }

    /**
     * Constructor.
     * @param name Name of the method.
     * @param factory Factory method reference.
     * @param descriptor Method descriptor.
     * @param farguments Factory method arguments.
     * @param arguments Dynamic invocation method arguments.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DynamicInvocation(
        final String name,
        final Handle factory,
        final String descriptor,
        final List<Object> farguments,
        final List<AstNode> arguments
    ) {
        this(
            name,
            factory,
            new Attributes().descriptor(descriptor).type("dynamic"),
            farguments,
            arguments
        );
    }

    /**
     * Constructor.
     * @param name Name of the method.
     * @param factory Factory method reference.
     * @param attributes Method attributes.
     * @param farguments Factory method arguments.
     * @param arguments Dynamic invocation arguments.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DynamicInvocation(
        final String name,
        final Handle factory,
        final Attributes attributes,
        final List<Object> farguments,
        final List<AstNode> arguments
    ) {
        this.name = name;
        this.factory = factory;
        this.attributes = attributes;
        this.farguments = farguments;
        this.arguments = arguments;
    }

    @Override
    public Iterable<Directive> toXmir() {
        final Directives directives = new Directives().add("o")
            .attr("base", String.format(".%s", this.name))
            .append(this.factory.toXmir())
            .append(this.attributes.toXmir());
        DynamicInvocation.xmirArgs(this.farguments).stream().map(Xmir::toXmir)
            .forEach(directives::append);
        this.arguments.stream().map(AstNode::toXmir).forEach(directives::append);
        return directives.up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(0);
        this.arguments.stream().map(AstNode::opcodes).forEach(res::addAll);
        res.add(
            new Opcode(
                Opcodes.INVOKEDYNAMIC,
                Stream.concat(
                    Stream.of(
                        this.name,
                        this.attributes.descriptor(),
                        this.factory.toAsm()
                    ),
                    this.farguments.stream()
                ).toArray()
            )
        );
        return res;
    }

    @Override
    public Type type() {
        return Type.getReturnType(this.attributes.descriptor());
    }

    /**
     * Parse the dynamic method name.
     * @param root XMIR root node.
     * @return Name.
     */
    private static String xname(final XmlNode root) {
        return root.attribute("base")
            .map(s -> s.substring(1))
            .orElseThrow(() -> new IllegalArgumentException("Name is required"));
    }

    /**
     * Parse a dynamic invocation descriptor.
     * @param children XMIR root node children which we parse.
     * @return Descriptor.
     */
    private static String xdescriptor(final List<XmlNode> children) {
        return new Attributes(children.get(1)).descriptor();
    }

    /**
     * Parse a factory method reference.
     * @param children XMIR root node children which we parse.
     * @return Factory method reference.
     */
    private static Handle xfactory(final List<XmlNode> children) {
        return new Handle(children.get(0));
    }

    /**
     * Parse a factory method arguments.
     * @param children XMIR children.
     * @return Arguments.
     */
    private static List<Object> xfarguments(final List<XmlNode> children) {
        final List<Object> res = new ArrayList<>(3);
        res.add(Type.getType(new HexString(children.get(2).text()).decode()));
        res.add(new Handle(children.get(3)).toAsm());
        res.add(Type.getType(new HexString(children.get(4).text()).decode()));
        return res;
    }

    /**
     * Parse the dynamic invocation arguments.
     * @param children XMIR children.
     * @param parser Parser to parse arguments if they exist.
     * @return Arguments.
     */
    private static List<AstNode> xargs(final List<XmlNode> children, final Parser parser) {
        final List<AstNode> result;
        if (children.size() > 5) {
            result = children.subList(5, children.size())
                .stream()
                .map(parser::parse)
                .collect(Collectors.toList());
        } else {
            result = Collections.emptyList();
        }
        return result;
    }

    /**
     * Convert the factory method arguments to XMIR.
     * @param args Arguments.
     * @return XMIR arguments.
     */
    private static List<Xmir> xmirArgs(final List<Object> args) {
        return args.stream().map(
            node -> {
                final Xmir result;
                if (node instanceof org.objectweb.asm.Handle) {
                    result = new Handle((org.objectweb.asm.Handle) node);
                } else {
                    result = () -> new DirectivesData(node);
                }
                return result;
            }
        ).collect(Collectors.toList());
    }
}
