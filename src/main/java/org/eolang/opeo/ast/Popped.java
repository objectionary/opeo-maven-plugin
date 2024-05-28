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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Popped.
 * This node represents a node that is popped from the stack.
 * @since 0.2
 * @todo #229:90min Do we need to implement the `Popped` node?
 *  The `Popped` node is a node that represents a node that is popped from the stack.
 *  Maybe it's better to just silently pop the node from the stack and not represent it in the AST.
 *  Let's decide if we need to implement the `Popped` node or not.
 *  If we decide to implement it, we need to write tests for it.
 */
public final class Popped implements AstNode, Typed {

    /**
     * The popped node.
     */
    private final AstNode node;

    /**
     * Constructor.
     * @param node The popped node.
     */
    public Popped(final AstNode node) {
        this.node = node;
    }

    @Override
    public List<AstNode> opcodes() {
        return Stream.concat(
            this.node.opcodes().stream(),
            Stream.of(new Opcode(Opcodes.POP))
        ).collect(Collectors.toList());
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives()
            .add("o").attr("base", ".ignore-result")
            .append(this.node.toXmir())
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
