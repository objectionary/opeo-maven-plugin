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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Duplicate.
 * This class represents the DUP instruction in the JVM bytecode.
 * @since 0.2
 * @todo #229:90min Do we need {@link Duplicate} class?
 *  We have three classes with rather similar semantics: {@link Duplicate},
 *  {@link Reference} and {@link Linked}.
 *  They have similar methods and fields. We need to investigate if we can
 *  remove one or two of these classes and use the remaining one instead.
 */
public final class Duplicate implements AstNode, Typed, Linked {

    /**
     * Flag to indicate if the node was compiled.
     */
    private final AtomicBoolean compiled;

    /**
     * Flag to indicate if the node was decompiled.
     */
    private final AtomicBoolean decompiled;

    /**
     * The original node which was duplicated.
     */
    private final AtomicReference<AstNode> original;

    /**
     * Constructor.
     * @param original The original node to duplicate.
     */
    public Duplicate(final AstNode original) {
        this.original = new AtomicReference<>(original);
        this.compiled = new AtomicBoolean(false);
        this.decompiled = new AtomicBoolean(false);
    }

    @Override
    public List<AstNode> opcodes() {
        if (this.compiled.getAndSet(true)) {
            return Collections.emptyList();
        }
        return Stream.concat(
            this.original.get().opcodes().stream(),
            Stream.of(new Opcode(Opcodes.DUP))
        ).collect(Collectors.toList());
    }

    @Override
    public Type type() {
        if (this.original.get() instanceof Typed) {
            return ((Typed) this.original.get()).type();
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Iterable<Directive> toXmir() {
        if (this.decompiled.getAndSet(true)) {
            return Collections.emptyList();
        }
        return new Directives()
            .add("o")
            .attr("base", "duplicated")
            .append(this.original.get().toXmir())
            .up();
    }

    @Override
    public void link(final AstNode node) {
        this.original.set(node);
    }

    @Override
    public AstNode current() {
        return this.original.get();
    }

    /**
     * Get the original node which was duplicated.
     * @return The original node.
     */
    public AstNode origin() {
        return this.original.get();
    }
}
