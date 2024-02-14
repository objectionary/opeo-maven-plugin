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

import java.util.ArrayList;
import java.util.List;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Field assignment.
 * <p>{@code
 *   x.y = 2;
 * }</p>
 * @since 0.2
 */
public final class FieldAssignment implements AstNode {

    /**
     * The field to assign to.
     */
    private final InstanceField field;

    /**
     * The value to assign.
     */
    private final AstNode value;

    /**
     * Field attributes.
     */
    private final Attributes attributes;

    /**
     * Constructor.
     * @param left The field to assign to
     * @param right The value to assign
     * @param attributes Field attributes
     */
    public FieldAssignment(
        final InstanceField left,
        final AstNode right,
        final Attributes attributes
    ) {
        this.field = left;
        this.value = right;
        this.attributes = attributes.type("field");
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives().add("o")
            .attr("base", ".writefield")
            .attr("scope", this.attributes)
            .append(this.field.toXmir())
            .append(this.value.toXmir())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(3);
        res.addAll(this.field.instance().opcodes());
        res.addAll(this.value.opcodes());
        res.add(this.field.store());
        return res;
    }
}
