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

import org.eolang.opeo.LabelInstruction;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.Label;
import org.eolang.opeo.ast.Labeled;
import org.eolang.opeo.decompilation.DecompilerState;

/**
 * Label instruction handler.
 * @since 0.1
 */
public final class LabelAgent implements DecompilationAgent {

    @Override
    public boolean appropriate(final DecompilerState state) {
        return new OpcodesAgent(this).appropriate(state);
    }

    @Override
    public Supported supported() {
        return new Supported(LabelInstruction.LABEL_OPCODE);
    }

    @Override
    public void handle(final DecompilerState state) {
        if (this.appropriate(state)) {
            state.stack().push(
                new Labeled(
                    state.stack().first().orElse(new AstNode.Empty()),
                    new Label(String.class.cast(state.operand(0)))
                )
            );
            state.popInstruction();
        } else {
            throw new IllegalAgentException(this, state);
        }
    }
}
