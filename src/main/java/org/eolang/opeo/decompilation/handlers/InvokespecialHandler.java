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
package org.eolang.opeo.decompilation.handlers;

import java.util.Collections;
import java.util.List;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.Attributes;
import org.eolang.opeo.ast.Constructor;
import org.eolang.opeo.ast.Duplicate;
import org.eolang.opeo.ast.Labeled;
import org.eolang.opeo.ast.Linked;
import org.eolang.opeo.ast.Reference;
import org.eolang.opeo.ast.Super;
import org.eolang.opeo.ast.This;
import org.eolang.opeo.decompilation.DecompilerState;
import org.eolang.opeo.decompilation.InstructionHandler;
import org.objectweb.asm.Type;

/**
 * Invokespecial instruction handler.
 *
 * @since 0.1
 * @todo #229:90min Is Labeled Class an Abstraction Failure?
 *  As you can see in {@link InvokespecialHandler} we use many 'instanceof' checks.
 *  This is a clear sign that the class hierarchy is not well designed.
 *  The original problem lies in the {@link Labeled} class.
 *  We need to find more elegant solutions to handle labels in AST.
 */
public final class InvokespecialHandler implements InstructionHandler {

    @Override
    public void handle(final DecompilerState state) {
        final String type = (String) state.operand(0);
        final String name = (String) state.operand(1);
        final String descriptor = (String) state.operand(2);
        final boolean interfaced = (boolean) state.operand(3);
        final List<AstNode> args = state.stack().pop(
            Type.getArgumentCount(descriptor)
        );
        Collections.reverse(args);
        final AstNode target = state.stack().pop();
        if (InvokespecialHandler.isThis(target)) {
            state.stack().push(
                new Super(target, args, descriptor, type, name)
            );
        } else {
//            final Linked linked = this.findLinked(target);
            final AstNode constructor = new Constructor(
                target,
                new Attributes().descriptor(descriptor).interfaced(interfaced),
                args
            );
//            linked.link(constructor);
            state.stack().push(constructor);


//            if (target instanceof Reference) {
//                ((Reference) target).link(constructor);
//            } else if (target instanceof Labeled) {
//                ((Reference) ((Labeled) target).origin()).link(constructor);
//            } else if (target instanceof Duplicate) {
//
//            } else {
//                throw new IllegalStateException(
//                    String.format(
//                        "Unexpected target type: %s",
//                        target.getClass().getCanonicalName()
//                    )
//                );
//            }
        }
    }

    private Linked findLinked(final AstNode node) {
        if (node instanceof Linked) {
            return (Linked) node;
        } else if (node instanceof Labeled) {
            return this.findLinked((((Labeled) node).origin()));
        } else {
            throw new IllegalStateException(String.format("Can find reference for node %s", node));
        }
    }


    /**
     * Check if the node is "this".
     * @param candidate Node to check
     * @return True if the node is "this"
     */
    private static boolean isThis(final AstNode candidate) {
        final boolean result;
        if (candidate instanceof This) {
            result = true;
        } else if (candidate instanceof Labeled) {
            result = InvokespecialHandler.isThis(((Labeled) candidate).origin());
        } else {
            result = false;
        }
        return result;
    }

}
