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
import java.util.concurrent.atomic.AtomicReference;
import org.xembly.Directive;

/**
 * Object reference in the stack.
 * @since 0.1
 */
public final class Reference implements AstNode {

    /**
     * Object itself.
     * The current reference connects with this object.
     */
    private final AtomicReference<AstNode> ref;

    /**
     * Constructor.
     */
    public Reference() {
        this(new AtomicReference<>());
    }

    /**
     * Constructor.
     * @param ref Object reference.
     */
    public Reference(final AtomicReference<AstNode> ref) {
        this.ref = ref;
    }

    /**
     * Link this reference with the given object.
     * @param node Object to link with.
     */
    public void link(final AstNode node) {
        this.ref.set(node);
    }

    /**
     * Get the object.
     * @return Object.
     */
    public AstNode object() {
        return this.ref.get();
    }

    @Override
    public Iterable<Directive> toXmir() {
        return this.ref.get().toXmir();
    }

    @Override
    public List<AstNode> opcodes() {
        return this.ref.get().opcodes();
    }
}
