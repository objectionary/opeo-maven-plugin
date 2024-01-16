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
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Access to a field.
 * @since 0.1
 */
public final class InstanceField implements AstNode {

    /**
     * Object reference from which the field is accessed.
     */
    private final AstNode source;

    /**
     * Field name.
     */
    private final String name;

    /**
     * Constructor.
     * @param source Object reference from which the field is accessed
     * @param name Field name
     */
    public InstanceField(final AstNode source, final String name) {
        this.source = source;
        this.name = name;
    }

    @Override
    public String print() {
        return String.format("%s.%s", this.source.print(), this.name);
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives()
            .add("o")
            .attr("base", String.format(".%s", this.name))
            .attr("scope", "field")
            .append(this.source.toXmir())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        //@checkstyle MethodBodyCommentsCheck (10 lines)
        // @todo #86:90min Implement "GETFIELD" opcode compilation from 'InstanceField'.
        //  The opcode should be compiled from the 'InstanceField' node correctly.
        //  Right now we put dummy owner and descriptor.
        //  Don't forget to remove this comment after the implementation is done and add new tests.
        final List<AstNode> res = new ArrayList<>(0);
        res.addAll(this.source.opcodes());
        res.add(new Opcode(Opcodes.GETFIELD, "???owner???", this.name, "???descriptor???"));
        return res;
    }
}
