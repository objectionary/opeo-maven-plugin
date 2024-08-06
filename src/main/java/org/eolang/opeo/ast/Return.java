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
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.compilation.Parser;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Return statement.
 *
 * @since 0.5
 */
public final class Return implements AstNode {

    /**
     * Value to return.
     */
    private final AstNode value;

    /**
     * Default constructor.
     */
    public Return() {
        this(new Empty());
    }

    /**
     * Constructor.
     * @param node XML node to parse.
     * @param parser Parser, that is used to parse child nodes.
     */
    public Return(final XmlNode node, final Parser parser) {
        this(Return.xtype(node, parser));
    }

    /**
     * Constructor.
     * @param typed Value to return.
     */
    public Return(final AstNode typed) {
        this.value = typed;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives()
            .add("o")
            .attr("base", "return")
            .append(this.value.toXmir())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(1);
        res.addAll(this.value.opcodes());
        res.add(this.opcode());
        return res;
    }

    /**
     * Get opcode.
     * @return Opcode.
     * @checkstyle CyclomaticComplexityCheck (20 lines)
     */
    private Opcode opcode() {
        final Type type = this.type();
        final Opcode result;
        if (type.equals(Type.VOID_TYPE)) {
            result = new Opcode(Opcodes.RETURN);
        } else if (type.equals(Type.INT_TYPE)) {
            result = new Opcode(Opcodes.IRETURN);
        } else if (type.equals(Type.LONG_TYPE)) {
            result = new Opcode(Opcodes.LRETURN);
        } else if (type.equals(Type.FLOAT_TYPE)) {
            result = new Opcode(Opcodes.FRETURN);
        } else if (type.equals(Type.DOUBLE_TYPE)) {
            result = new Opcode(Opcodes.DRETURN);
        } else if (type.equals(Type.BOOLEAN_TYPE)) {
            result = new Opcode(Opcodes.IRETURN);
        } else {
            result = new Opcode(Opcodes.ARETURN);
        }
        return result;
    }

    /**
     * Get a type of the value.
     * @return Type of the value.
     */
    private Type type() {
        return Typed.class.cast(this.value).type();
    }

    /**
     * Parse type.
     * @param node XML node.
     * @param parser Parser to parse children.
     * @return Parsed typed value.
     */
    private static AstNode xtype(final XmlNode node, final Parser parser) {
        final AstNode result;
        final List<XmlNode> children = node.children().collect(Collectors.toList());
        if (children.isEmpty()) {
            result = new Empty();
        } else {
            result = parser.parse(children.get(0));
        }
        return result;
    }
}
