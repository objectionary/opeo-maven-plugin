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
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Multiplication.
 * @since 0.1
 * @todo #284:90min Add types for Multiplication.
 *  Currently we support only integer multiplication.
 *  We need to add support for other types, such as floating point numbers, longs, etc.
 *  Don't forget to add tests for the new types.
 */
public final class Multiplication implements AstNode, Typed {

    /**
     * Left operand.
     */
    private final AstNode left;

    /**
     * Right operand.
     */
    private final AstNode right;

    /**
     * Constructor.
     * @param node XMIR node where to extract the value.
     * @param search Search function.
     */
    public Multiplication(final XmlNode node, final Function<XmlNode, AstNode> search) {
        this(Multiplication.xleft(node, search), xright(node, search));
    }

    /**
     * Constructor.
     * @param left Left operand
     * @param right Right operand
     */
    public Multiplication(final AstNode left, final AstNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives().add("o")
            .attr("base", "times")
            .append(this.left.toXmir())
            .append(this.right.toXmir())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(0);
        res.addAll(this.left.opcodes());
        res.addAll(this.right.opcodes());
        res.add(new Opcode(Opcodes.IMUL));
        return res;
    }

    /**
     * Extracts the first value.
     * @param node XMIR node where to extract the value.
     * @param search Search function
     * @return Value.
     */
    private static AstNode xright(final XmlNode node, final Function<XmlNode, AstNode> search) {
        final List<XmlNode> all = node.children().collect(Collectors.toList());
        return search.apply(all.get(all.size() - 1));
    }

    /**
     * Extracts the left value.
     * @param node XMIR node where to extract the value.
     * @param search Search function
     * @return Value.
     */
    private static AstNode xleft(final XmlNode node, final Function<XmlNode, AstNode> search) {
        return search.apply(node.firstChild());
    }

    @Override
    public Type type() {
        return new ExpressionType(this.left, this.right).type();
    }
}
