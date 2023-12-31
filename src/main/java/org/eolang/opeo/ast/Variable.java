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

import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * A variable.
 * @since 0.1
 * @todo #44:90min Use type of the variable.
 *  Currently, the type of the variable is not used.
 *  It should be used to generate the correct XMIR.
 *  For example, if the type is "int", then the XMIR
 *  should contain this information to be able to
 *  reconstruct the type of the variable during
 *  the compilation to bytecode.
 */
public final class Variable implements AstNode {
    /**
     * The type of the variable.
     */
    private final Type type;

    /**
     * The identifier of the variable.
     */
    private final int identifier;

    /**
     * Constructor.
     * @param type The type of the variable.
     * @param identifier The identifier of the variable.
     */
    public Variable(
        final Type type,
        final int identifier
    ) {
        this.type = type;
        this.identifier = identifier;
    }

    @Override
    public String print() {
        return String.format("local%d%s", this.identifier, this.type.getClassName());
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives()
            .add("o")
            .attr("base", String.format("local%d", this.identifier))
            .up();
    }
}
