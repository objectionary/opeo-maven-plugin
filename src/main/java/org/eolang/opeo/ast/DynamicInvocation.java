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
import org.eolang.jeo.representation.directives.DirectivesData;
import org.eolang.jeo.representation.xmir.HexString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Dynamic invocation.
 * @since 0.5
 */
public final class DynamicInvocation implements AstNode, Typed {

    private final String name;
    private final Handle factory;

    private final Attributes attributes;
    private final List<Xmir> arguments;

    public DynamicInvocation(final XmlNode root) {
        this(root, root.children().collect(Collectors.toList()));
    }

    public DynamicInvocation(final XmlNode root, final List<XmlNode> chldren) {
        this(
            DynamicInvocation.xname(root),
            DynamicInvocation.xfactory(chldren),
            DynamicInvocation.xdescriptor(chldren),
            DynamicInvocation.xarguments(chldren)
        );
    }

    private static List<Object> xarguments(final List<XmlNode> children) {
        List<Object> res = new ArrayList<>(3);
        res.add(new HexString(children.get(2).text()).decode());
        res.add(new Handle(children.get(3)).toAsm());
        res.add(new HexString(children.get(4).text()).decode());
        return res;
    }

    private static String xdescriptor(final List<XmlNode> root) {
        return new Attributes(root.get(0)).descriptor();
    }

    private static Handle xfactory(final List<XmlNode> root) {
        return new Handle(root.get(1));
    }

    private static String xname(final XmlNode root) {
        return root.attribute("base")
            .map(s -> s.substring(1))
            .orElseThrow(() -> new IllegalArgumentException("Name is required"));
    }

    public DynamicInvocation(
        final String name,
        final Handle factory,
        final String descriptor,
        final List<Object> arguments
    ) {
        this.factory = factory;
        this.attributes = new Attributes()
            .descriptor(descriptor)
            .type("dynamic");
        this.name = name;
        this.arguments = this.toXmlArg(arguments);
    }

    private List<Xmir> toXmlArg(final List<Object> arguments) {
        return arguments.stream().map(
            node -> {
                if (node instanceof Handle) {
                    return (Xmir) node;
                } else {
                    return (Xmir) () -> new DirectivesData(node);
                }
            }
        ).collect(Collectors.toList());
    }

    @Override
    public Iterable<Directive> toXmir() {
        final Directives directives = new Directives().add("o")
            .attr("base", String.format(".%s", this.name))
            .append(this.attributes.toXmir())
            .append(this.factory.toXmir());
        this.arguments.stream()
            .map(Xmir::toXmir)
            .forEach(directives::append);
        return directives.up();
    }

    @Override
    public List<AstNode> opcodes() {
        return Collections.emptyList();
    }

    @Override
    public Type type() {
        return Type.getReturnType(this.attributes.descriptor());
    }
}
