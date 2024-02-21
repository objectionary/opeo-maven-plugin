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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.ToString;
import org.objectweb.asm.Type;

/**
 * Expression type.
 * Defines the type of expression.
 * @since 0.2
 */
@ToString
public final class ExpressionType {

    /**
     * Expression values.
     */
    private final List<AstNode> values;

    /**
     * Constructor.
     * @param values Expression values.
     */
    public ExpressionType(final AstNode... values) {
        this(Arrays.asList(values));
    }

    /**
     * Constructor.
     * @param values Expression values.
     */
    private ExpressionType(final List<AstNode> values) {
        this.values = values;
    }

    /**
     * Determine expression type.
     * @return Expression type.
     */
    public Type type() {
        final Set<Type> types = this.values.stream()
            .map(this::cast)
            .map(Typed::type).collect(Collectors.toSet());
        final Type result;
        if (types.contains(Type.DOUBLE_TYPE)) {
            result = Type.DOUBLE_TYPE;
        } else if (types.contains(Type.FLOAT_TYPE)) {
            result = Type.FLOAT_TYPE;
        } else if (types.contains(Type.LONG_TYPE)) {
            result = Type.LONG_TYPE;
        } else {
            result = Type.INT_TYPE;
        }
        return result;
    }


    /**
     * Cast node to a typed node.
     * @param node Node to cast.
     * @return Typed node.
     */
    private Typed cast(final AstNode node) {
        final Typed result;
        if (node instanceof Typed) {
            result = (Typed) node;
        } else {
            throw new IllegalStateException(
                String.format("Node %s is not typed inside %s", node, this)
            );
        }
        return result;
    }
}
