package org.eolang.opeo.decompilation;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eolang.opeo.ast.AstNode;

public final class OperandStack {

    /**
     * Output Stack.
     */
    private final Deque<AstNode> stack;

    public OperandStack() {
        this(new ArrayDeque<>(0));
    }

    public OperandStack(final Deque<AstNode> stack) {
        this.stack = stack;
    }

    public AstNode pop() {
        return this.stack.pop();
    }


    public List<AstNode> pop(int number) {
        final List<AstNode> args = new LinkedList<>();
        for (int index = 0; index < number; ++index) {
            args.add(this.stack.pop());
        }
        return args;
    }

    public void push(final AstNode node) {
        this.stack.push(node);
    }

    public void dup() {
        this.stack.push(this.stack.peek());
    }

    public Iterator<AstNode> descendingIterator() {
        return this.stack.descendingIterator();
    }
}
