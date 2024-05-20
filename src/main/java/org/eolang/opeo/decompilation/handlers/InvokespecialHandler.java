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

import com.jcabi.log.Logger;
import java.util.Collections;
import java.util.List;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.Attributes;
import org.eolang.opeo.ast.Constructor;
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
 */
public final class InvokespecialHandler implements InstructionHandler {
    @Override
    public void handle(final DecompilerState state) {
        final String type = (String) state.operand(0);
        final String name = (String) state.operand(1);
        final String descriptor = (String) state.operand(2);
        final boolean interfaced = (boolean) state.operand(3);

//        if (!"<init>".equals(name)) {
//            throw new UnsupportedOperationException(
//                String.format("Instruction %s is not supported yet", state)
//            );
//        }
        final List<AstNode> args = state.stack().pop(
            Type.getArgumentCount(descriptor)
        );
        Collections.reverse(args);

        //@checkstyle MethodBodyCommentsCheck (10 lines)
        // @todo #76:90min Target might not be an Object.
        //  Here we just compare with object, but if the current class has a parent, the
        //  target might not be an Object. We should compare with the current class name
        //  instead. Moreover, we have to pass the 'target' as an argument to the
        //  constructor of the 'Super' class somehow.
        final AstNode tar = state.stack().pop();
//        if ("java/lang/Object".equals(type)) {
//            state.stack().push(
//                new Super(tar, args, descriptor)
//            );
//        } else
        if (tar instanceof This) {
            Logger.info(
                this,
                "Invokespecial %s.%s%s",
                type,
                name,
                descriptor
            );
            state.stack().push(
                new Super(tar, args, descriptor, type, name)
            );
        } else {
            final AstNode constructor = new Constructor(
                type,
                new Attributes().descriptor(descriptor).interfaced(interfaced),
                args
            );
            ((Reference) tar).link(constructor);
        }
    }

}
