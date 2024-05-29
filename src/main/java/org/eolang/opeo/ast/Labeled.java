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

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Node with an attached label.
 * This class needed to avoid considering labels as separate nodes.
 * Maybe it's wrong to do so, but it's easier to implement this way, at least for now.
 * Pay attention, that {@link Labeled} class violates class hierarchy.
 * It is the most visible within {@link org.eolang.opeo.decompilation.handlers.InvokespecialHandler}
 * implementation.
 * @since 0.2
 */
@EqualsAndHashCode
@ToString
public final class Labeled implements AstNode, Typed {

    /**
     * Original node.
     */
    private final AstNode node;

    /**
     * Attached label.
     */
    private final Label label;

    public Labeled(final XmlNode node, Function<XmlNode, AstNode> search) {
        this(xnode(node, search), xlabel(node));
    }

    private static AstNode xnode(final XmlNode root, final Function<XmlNode, AstNode> search) {
        if (root.children().count() > 1) {
            return search.apply(root.firstChild());
        } else {
            return new Empty();
        }
    }

    private static Label xlabel(final XmlNode root) {
        final List<XmlNode> all = root.children().collect(Collectors.toList());
        final XmlNode last = all.get(all.size() - 1);
        return new Label(last);
    }

    /**
     * Constructor.
     * @param node Original node
     * @param label Attached label
     */
    public Labeled(final AstNode node, final Label label) {
        this.node = node;
        this.label = label;
    }

    /**
     * Original node.
     * @return Node
     */
    public AstNode origin() {
        return this.node;
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> re = Stream.concat(
            this.node.opcodes().stream(),
            this.label.opcodes().stream()
        ).collect(Collectors.toList());

        return re;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives()
            .add("o").attr("base", "labeled")
            .append(this.node.toXmir())
            .append(this.label.toXmir())
            .up();
    }

    @Override
    public Type type() {
        if (this.node instanceof Typed) {
            return ((Typed) this.node).type();
        } else {
            throw new IllegalStateException(String.format("Node '%s' is not typed", this.node));
        }
    }
}
