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
import org.eolang.jeo.representation.HexData;
import org.eolang.jeo.representation.xmir.AllLabels;
import org.eolang.jeo.representation.xmir.HexString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * If ast node.
 * @since 0.2
 */
@ToString
@EqualsAndHashCode
public final class If implements AstNode {

    /**
     * First value to compare.
     */
    private final AstNode first;

    /**
     * Second value to compare.
     */
    private final AstNode second;

    /**
     * Where to jump if the comparison is true.
     */
    private final Label target;

    /**
     * Constructor.
     * @param node XMIR node.
     */
    public If(final XmlNode node, Function<XmlNode, AstNode> search) {
        this(If.xfirst(node, search), If.xsecond(node, search), If.xtarget(node));
    }

    /**
     * Constructor.
     * @param first First value.
     * @param second Second value.
     * @param target Target label.
     */
    public If(
        final AstNode first,
        final AstNode second,
        final org.objectweb.asm.Label target
    ) {
        this(first, second, new Label(new HexData(new AllLabels().uid(target)).value()));
    }

    /**
     * Constructor.
     * @param first First value.
     * @param second Second value.
     * @param target Target label.
     */
    public If(final AstNode first, final AstNode second, final Label target) {
        this.first = first;
        this.second = second;
        this.target = target;
    }

    @Override
    public List<AstNode> opcodes() {
        return Stream.concat(
            Stream.concat(
                this.first.opcodes().stream(),
                this.second.opcodes().stream()
            ),
            Stream.of(new Opcode(Opcodes.IF_ICMPGT, this.target.toAsmLabel()))
        ).collect(Collectors.toList());
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives()
            .add("o").attr("base", ".if")
            .add("o").attr("base", ".gt")
            .append(this.first.toXmir())
            .append(this.second.toXmir())
            .up()
            .append(this.target.toXmir())
            .add("o").attr("base", "nop").up()
            .up();
    }

    /**
     * Extracts the first value.
     * @param node XMIR node where to extract the value.
     * @return Value.
     */
    private static AstNode xfirst(final XmlNode node, Function<XmlNode, AstNode> search) {
        return search.apply(node.child("base", ".gt").firstChild());
    }

    /**
     * Extracts the second value.
     * @param node XMIR node where to extract the value.
     * @return Value.
     */
    private static AstNode xsecond(final XmlNode node, Function<XmlNode, AstNode> search) {
        final List<XmlNode> children = node.child("base", ".gt").children()
            .collect(Collectors.toList());
        return search.apply(children.get(children.size() - 1));
    }

    /**
     * Extracts the target label.
     * @param node XMIR node where to extract the label.
     * @return Label.
     */
    private static Label xtarget(final XmlNode node) {
        return new Label(node.child("base", "label"));
    }

}
