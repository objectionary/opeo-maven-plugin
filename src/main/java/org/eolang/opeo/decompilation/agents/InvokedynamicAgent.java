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
import org.eolang.opeo.ast.DynamicInvocation;
import org.eolang.opeo.decompilation.DecompilerState;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Invokedynamic instruction handler.
 * @since 0.5
 */
public final class InvokedynamicAgent implements DecompilationAgent {

    @Override
    public Supported supported() {
        return new Supported(Opcodes.INVOKEDYNAMIC);
    }

    @Override
    public void handle(final DecompilerState state) {
        if (this.supported().isSupported(state.current())) {
            final List<Object> operands = state.current().params();
            final String descriptor = (String) operands.get(1);
            final List<AstNode> args = state.stack().pop(Type.getArgumentTypes(descriptor).length);
            Collections.reverse(args);
            final DynamicInvocation node = new DynamicInvocation(
                (String) operands.get(0),
                new org.eolang.opeo.ast.Handle((Handle) operands.get(2)),
                descriptor,
                operands.subList(3, operands.size()),
                args
            );
            state.stack().push(node);
            state.popInstruction();
        }
    }

}
