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
package org.eolang.opeo.decompilation;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.Label;
import org.eolang.opeo.ast.Labeled;

/**
 * Operand stack.
 * <p>
 * You can read more about it
 * <a href="https://stackoverflow.com/questions/24427056/what-is-an-operand-stack">here</a>
 * </p>
 * It emulates the behaviour of JVM operand stack.
 * @since 0.2
 */
@ToString
@EqualsAndHashCode
public final class OperandStack {

    /**
     * Output Stack.
     */
    private final Deque<AstNode> stack;

    /**
     * Default constructor.
     */
    public OperandStack() {
        this(new ArrayDeque<>(0));
    }

    /**
     * Constructor.
     * @param nodes Initial stack nodes.
     */
    public OperandStack(final AstNode... nodes) {
        this(new ArrayDeque<>(Arrays.asList(nodes)));
    }

    /**
     * Constructor.
     * @param original Initial stack collection.
     */
    OperandStack(final Deque<AstNode> original) {
        this.stack = original;
    }

    /**
     * Pop one node from the stack or return empty.
     * @return Optional node.
     */
    public Optional<AstNode> first() {
        final Optional<AstNode> result;
        if (this.stack.isEmpty()) {
            result = Optional.empty();
        } else {
            result = Optional.ofNullable(this.stack.pop());
        }
        return result;
    }

    /**
     * Pop one node from the stack.
     * @return Node.
     */
    public AstNode pop() {
        final AstNode res;
        final AstNode pop = this.stack.pop();
        if (pop instanceof Label) {
            res = new Labeled(this.stack.pop(), (Label) pop);
        } else {
            res = pop;
        }
        return res;
    }

    /**
     * Pop N nodes from the stack.
     * @param number Number of nodes to pop.
     * @return Collection of nodes.
     */
    public List<AstNode> pop(final int number) {
        final List<AstNode> args = new LinkedList<>();
        for (int index = 0; index < number; ++index) {
            args.add(this.pop());
        }
        return args;
    }

    /**
     * Push one more node to the stack.
     * @param node Node to add to the stack.
     */
    public void push(final AstNode node) {
        this.stack.push(node);
    }

    /**
     * Duplicate the higher value on the stack.
     */
    public void dup() {
        this.stack.push(this.stack.peek());
    }

    /**
     * Retrieve iterator of the stack in the reverse order.
     * @return Nodes iterator.
     */
    Iterator<AstNode> descendingIterator() {
        return this.stack.descendingIterator();
    }


}
