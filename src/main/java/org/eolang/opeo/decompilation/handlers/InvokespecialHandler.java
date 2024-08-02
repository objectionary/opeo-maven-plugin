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
import org.eolang.opeo.ast.NewAddress;
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
 * @checkstyle NoJavadocForOverriddenMethodsCheck (500 lines)
 * @checkstyle MultilineJavadocTagsCheck (500 lines)
 */
public final class InvokespecialHandler implements InstructionHandler {

    /**
     * Handle invokespecial instruction.
     * @param state Current instruction to handle together with operand stack and variables.
     * @todo #344:90min Super and Constructor classes ambiguity.
     *  Recently I removed the following code from this method:
     *  {@code
     *    state.stack().push(
     *      new Constructor(
     *        target,
     *        new Attributes().descriptor(descriptor).interfaced(interfaced),
     *        args
     *      )
     *    );
     *  }
     *  The reason behind this integration test with
     *  WebProperties$Resources$Chain$Strategy$Content.xmir class.
     *  See {@link JeoAndOpeoTest} for more info.
     *  We need to use both Super and This classes.
     *  Moreover, we might need to add some other representations.
     *  Don't forget to remove suppressions.
     */
    @Override
    @SuppressWarnings("PMD.UnusedLocalVariable")
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
        final boolean istarget = InvokespecialHandler.isThis(target);
        if (istarget) {
            state.stack().push(
                new Super(target, args, descriptor, type, name)
            );
        } else if (this.isNewAddress(target)) {
            state.stack().push(
                new Constructor(
                    target,
                    new Attributes().descriptor(descriptor).interfaced(interfaced),
                    args
                )
            );

        } else {
            state.stack().push(
                new Super(target, args, descriptor, type, name)
            );
//            state.stack().push(
//                new Super(target, args, descriptor, type, name)
//            );
        }
    }

    //todo wtf?
    private boolean isNewAddress(final AstNode target) {
        if (target instanceof NewAddress) {
            return true;
        } else if (target instanceof Duplicate) {
            return this.isNewAddress(((Duplicate) target).origin());
        } else if (target instanceof Labeled) {
            return this.isNewAddress(((Labeled) target).origin());
        } else {
            return false;
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
        } else if (candidate instanceof Duplicate) {
            result = InvokespecialHandler.isThis(((Duplicate) candidate).origin());
        } else {
            result = false;
        }
        return result;
    }

}
