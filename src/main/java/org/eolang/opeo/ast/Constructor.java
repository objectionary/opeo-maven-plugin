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
import java.util.stream.Collectors;

/**
 * Constructor output node.
 * @since 0.1
 */
public final class Constructor implements AstNode {

    /**
     * Constructor type.
     */
    private final String type;

    /**
     * Object reference.
     */
    private final String reference;

    /**
     * Constructor arguments.
     */
    private final List<AstNode> arguments;

    /**
     * Constructor.
     * @param type Constructor type
     * @param reference Object reference
     * @param arguments Constructor arguments
     */
    public Constructor(
        final String type,
        final String reference,
        final List<AstNode> arguments
    ) {
        this.type = type;
        this.reference = reference;
        this.arguments = arguments;
    }

    @Override
    public String print() {
        return String.format(
            "%s.new %s",
            this.type,
            this.arguments.stream()
                .map(AstNode::print)
                .map(s -> String.format("(%s)", s))
                .collect(Collectors.joining(" "))
        );
    }

    @Override
    public String identifier() {
        return this.reference;
    }
}
