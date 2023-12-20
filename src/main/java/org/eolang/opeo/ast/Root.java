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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Root node.
 * @since 0.1
 */
public final class Root implements AstNode {

    /**
     * Children.
     */
    private final List<AstNode> children;

    /**
     * Constructor.
     */
    public Root() {
        this.children = new LinkedList<>();
    }

    @Override
    public String print() {
        return this.children.stream().map(AstNode::print).collect(Collectors.joining("\n"));
    }

    @Override
    public Iterable<Directive> toXmir() {
        final Iterable<Directive> result;
        if (this.children.isEmpty()) {
            result = Collections.emptyList();
        } else {
            final Directives directives = new Directives();
            directives.add("o").attr("base", "tuple");
            this.children.stream().map(AstNode::toXmir).forEach(directives::append);
            directives.up();
            result = directives;
        }
        return result;
    }

    /**
     * Find child by ID.
     * @param identifier ID
     * @return Child
     */
    public Optional<AstNode> child(final String identifier) {
        return this.children.stream()
            .filter(node -> node.identifier().equals(identifier)).findFirst();
    }

    /**
     * Append child.
     * @param node Child
     */
    public void append(final AstNode node) {
        this.children.add(node);
    }

    /**
     * Disconnect child.
     * @param node Child
     */
    public void disconnect(final AstNode node) {
        this.children.remove(node);
    }

    @Override
    public String identifier() {
        return UUID.randomUUID().toString();
    }
}
