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
package org.eolang.opeo.decompilation.agents;

import java.util.Collections;
import java.util.List;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.Attributes;
import org.eolang.opeo.ast.Invocation;
import org.eolang.opeo.decompilation.DecompilerState;
import org.eolang.opeo.decompilation.DecompilationAgent;
import org.objectweb.asm.Type;

/**
 * Invokevirtual instruction handler.
 * <p>
 *     Other bytes: 2: indexbyte1, indexbyte2
 * </p>
 * <p>
 *     objectref, [arg1, arg2, ...] → result
 * </p>
 * <p>
 *     Invoke virtual method on object objectref and puts the result on the stack (might be void).
 *     The method is identified by method reference index in constant pool
 *     (indexbyte1 << 8 | indexbyte2)
 * </p>
 * @since 0.1
 */
public final class InvokevirtualAgent implements DecompilationAgent {

    @Override
    public void handle(final DecompilerState state) {
        final String owner = (String) state.operand(0);
        final String method = (String) state.operand(1);
        final String descriptor = (String) state.operand(2);
        final boolean interfaced = (Boolean) state.operand(3);
        final List<AstNode> args = state.stack().pop(
            Type.getArgumentCount(descriptor)
        );
        Collections.reverse(args);
        final AstNode source = state.stack().pop();
        state.stack().push(
            new Invocation(
                source,
                new Attributes()
                    .name(method)
                    .descriptor(descriptor)
                    .owner(owner)
                    .interfaced(interfaced),
                args
            )
        );
    }
}
