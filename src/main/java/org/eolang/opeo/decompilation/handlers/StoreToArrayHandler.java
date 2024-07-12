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

import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.Duplicate;
import org.eolang.opeo.ast.FieldRetrieval;
import org.eolang.opeo.ast.Labeled;
import org.eolang.opeo.ast.Reference;
import org.eolang.opeo.ast.StoreArray;
import org.eolang.opeo.decompilation.DecompilerState;
import org.eolang.opeo.decompilation.InstructionHandler;

/**
 * Store to array instruction handler.
 * Store a reference in an array
 * Opcodes: aastore
 * Stack [before]->[after]: "arrayref, index, value →"
 * @since 0.1
 * @todo ! references? wtf?
 */
public final class StoreToArrayHandler implements InstructionHandler {

    @Override
    public void handle(final DecompilerState state) {
        final AstNode value = state.stack().pop();
        final AstNode index = state.stack().pop();
        final AstNode array = state.stack().pop();
//        try {
            final Reference ref = this.findRef(array);
            ref.link(new StoreArray(ref.object(), index, value));
            state.stack().push(ref);
//        } catch (final IllegalStateException exception) {
//            state.stack().push(new Reference(new StoreArray(array, index, value)));
//        }
    }

    /**
     * Find reference.
     * @param node Node where to search for reference.
     * @return Reference.
     */
    private Reference findRef(final AstNode node) {
        final Reference result;
        if (node instanceof Reference) {
            result = (Reference) node;
        } else if (node instanceof Labeled) {
            result = this.findRef(((Labeled) node).origin());
        } else if (node instanceof Duplicate) {
            result = this.findRef(((Duplicate) node).origin());
        } else if (node instanceof FieldRetrieval) {
            result = new Reference(node);
        } else {
            throw new IllegalStateException(String.format("Can find reference for node %s", node));
        }
        return result;
    }

}
