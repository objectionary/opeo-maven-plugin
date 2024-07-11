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
import java.util.stream.Collectors;
import org.eolang.jeo.representation.directives.DirectivesData;
import org.eolang.jeo.representation.xmir.HexString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.compilation.Parser;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Check if the value is of the given type.
 * @since 0.5
 */
public final class CheckCast implements AstNode, Typed {

    /**
     * Type to cast to.
     */
    private final Type type;

    /**
     * Value to cast.
     */
    private final AstNode value;

    public CheckCast(final XmlNode node, final Parser parser) {
        this(CheckCast.xtype(node), CheckCast.xvalue(node, parser));
    }

    public CheckCast(final Type type, final AstNode value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives().add("o").attr("base", "checkcast")
            .append(new DirectivesData(this.type))
            .append(this.value.toXmir())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(1);
        res.addAll(this.value.opcodes());
        res.add(new Opcode(Opcodes.CHECKCAST, this.type));
        return res;
    }

    @Override
    public Type type() {
        return this.type;
    }

    private static AstNode xvalue(final XmlNode node, final Parser parser) {
        return parser.parse(node.children().collect(Collectors.toList()).get(1));
    }

    private static Type xtype(final XmlNode node) {
        return Type.getType(
            new HexString(
                node.children().findFirst().orElseThrow(
                    () -> new IllegalArgumentException(
                        "CheckCast should have a first child for the type."
                    )
                ).text()
            ).decode()
        );
    }
}
