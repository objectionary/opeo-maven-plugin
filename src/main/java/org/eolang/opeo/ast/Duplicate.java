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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Duplicate.
 * This class represents the DUP instruction in the JVM bytecode.
 * @since 0.2
 */
public final class Duplicate implements AstNode, Typed {

    /**
     * Alias.
     * Used as a reference.
     */
    private final String alias;

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
        this(
            Duplicate.randomName(),
            new AtomicBoolean(false),
            new AtomicBoolean(false),
            new AtomicReference<>(original)
        );
    }

    /**
     * Constructor.
     * @param alias Reference name that will be used to refer to this node.
     * @param original The original node to duplicate.
     */
    Duplicate(final String alias, final AstNode original) {
        this(
            alias,
            new AtomicBoolean(false),
            new AtomicBoolean(false),
            new AtomicReference<>(original)
        );
    }

    /**
     * Constructor.
     * @param alias Reference name that will be used to refer to this node.
     * @param compiled Flag to indicate if the node was compiled.
     * @param decompiled Flag to indicate if the node was decompiled.
     * @param original The original node to duplicate.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    Duplicate(
        final String alias,
        final AtomicBoolean compiled,
        final AtomicBoolean decompiled,
        final AtomicReference<AstNode> original
    ) {
        this.alias = alias;
        this.compiled = compiled;
        this.decompiled = decompiled;
        this.original = original;
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> result;
        if (this.compiled.getAndSet(true)) {
            result = Collections.emptyList();
        } else {
            result = new ArrayList<>(3);
            result.addAll(this.original.get().opcodes());
            result.add(new Opcode(Opcodes.DUP));
        }
        return result;
    }

    @Override
    public Iterable<Directive> toXmir() {
        final int line = new Random().nextInt(Integer.MAX_VALUE);
        final Iterable<Directive> result;
        if (this.decompiled.getAndSet(true)) {
            result = new Directives().add("o")
                .attr("base", this.alias)
                .attr("line", line)
                .up();
        } else {
            result = new Directives()
                .add("o")
                .attr("base", "duplicated")
                .attr("name", this.alias)
                .attr("line", line)
                .append(this.original.get().toXmir())
                .up();
        }
        return result;
    }

    @Override
    public Type type() {
        if (this.original.get() instanceof Typed) {
            return ((Typed) this.original.get()).type();
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Link the node.
     * @param node The node to link.
     */
    public void link(final AstNode node) {
        this.original.set(node);
    }

    /**
     * Retrieve the current node.
     * @return The current node.
     */
    public AstNode current() {
        return this.original.get();
    }

    /**
     * Generates random string alias.
     * @return Random string.
     */
    private static String randomName() {
        final char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        final StringBuilder name = new StringBuilder(14);
        final SecureRandom random = new SecureRandom();
        for (int index = 0; index < 10; ++index) {
            name.append(alphabet[random.nextInt(alphabet.length)]);
        }
        return String.format("ref-%s", name);
    }
}
