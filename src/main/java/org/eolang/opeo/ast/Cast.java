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
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesData;
import org.eolang.jeo.representation.xmir.HexString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Cast node.
 *
 * @since 0.2
 */
@ToString
@EqualsAndHashCode
public final class Cast implements AstNode, Typed {

    /**
     * Target type.
     */
    private final Type target;

    /**
     * Node to cast.
     */
    private final AstNode origin;

    /**
     * Constructor.
     *
     * @param node XML node
     * @param target Function to determine the origin node.
     */
    public Cast(final XmlNode node, final Function<XmlNode, AstNode> target) {
        this(Cast.xtarget(node), Cast.xorigin(node, target));
    }

    /**
     * Constructor.
     *
     * @param target Target type
     * @param origin Node to cast
     */
    public Cast(final Type target, final AstNode origin) {
        this.origin = origin;
        this.target = target;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives()
            .add("o").attr("base", "cast")
            .append(this.origin.toXmir())
            .append(new DirectivesData(this.target.getDescriptor()))
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> opcodes = new ArrayList<>(3);
        opcodes.addAll(this.origin.opcodes());
        opcodes.add(this.opcode());
        return opcodes;
    }

    @Override
    public Type type() {
        return this.target;
    }

    /**
     * Constructor.
     *
     * @return Cast.
     * @checkstyle CyclomaticComplexityCheck (50 lines)
     * @checkstyle JavaNCSSCheck (50 lines)
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    private AstNode opcode() {
        final Type fromtype = new ExpressionType(this.origin).type();
        final Type totype = this.target;
        final AstNode result;
        if (fromtype.equals(totype)) {
            result = new Opcode(Opcodes.NOP);
        } else if (fromtype.equals(Type.INT_TYPE) && totype.equals(Type.LONG_TYPE)) {
            result = new Opcode(Opcodes.I2L);
        } else if (fromtype.equals(Type.INT_TYPE) && totype.equals(Type.FLOAT_TYPE)) {
            result = new Opcode(Opcodes.I2F);
        } else if (fromtype.equals(Type.INT_TYPE) && totype.equals(Type.DOUBLE_TYPE)) {
            result = new Opcode(Opcodes.I2D);
        } else if (fromtype.equals(Type.LONG_TYPE) && totype.equals(Type.INT_TYPE)) {
            result = new Opcode(Opcodes.L2I);
        } else if (fromtype.equals(Type.LONG_TYPE) && totype.equals(Type.FLOAT_TYPE)) {
            result = new Opcode(Opcodes.L2F);
        } else if (fromtype.equals(Type.LONG_TYPE) && totype.equals(Type.DOUBLE_TYPE)) {
            result = new Opcode(Opcodes.L2D);
        } else if (fromtype.equals(Type.FLOAT_TYPE) && totype.equals(Type.DOUBLE_TYPE)) {
            result = new Opcode(Opcodes.F2D);
        } else if (fromtype.equals(Type.FLOAT_TYPE) && totype.equals(Type.INT_TYPE)) {
            result = new Opcode(Opcodes.F2I);
        } else if (fromtype.equals(Type.FLOAT_TYPE) && totype.equals(Type.LONG_TYPE)) {
            result = new Opcode(Opcodes.F2L);
        } else if (fromtype.equals(Type.DOUBLE_TYPE) && totype.equals(Type.INT_TYPE)) {
            result = new Opcode(Opcodes.D2I);
        } else if (fromtype.equals(Type.DOUBLE_TYPE) && totype.equals(Type.LONG_TYPE)) {
            result = new Opcode(Opcodes.D2L);
        } else if (fromtype.equals(Type.DOUBLE_TYPE) && totype.equals(Type.FLOAT_TYPE)) {
            result = new Opcode(Opcodes.D2F);
        } else if (fromtype.equals(Type.INT_TYPE) && totype.equals(Type.BYTE_TYPE)) {
            result = new Opcode(Opcodes.I2B);
        } else if (fromtype.equals(Type.INT_TYPE) && totype.equals(Type.CHAR_TYPE)) {
            result = new Opcode(Opcodes.I2C);
        } else if (fromtype.equals(Type.INT_TYPE) && totype.equals(Type.SHORT_TYPE)) {
            result = new Opcode(Opcodes.I2S);
        } else {
            throw new IllegalStateException(
                String.format(
                    "Can't cast from %s to %s",
                    fromtype.getDescriptor(),
                    totype.getDescriptor()
                )
            );
        }
        return result;
    }

    /**
     * Prestructor for Cast#target.
     *
     * @param node XML node
     * @return Target type.
     */
    private static Type xtarget(final XmlNode node) {
        return Type.getType(
            new HexString(
                node.children()
                    .collect(Collectors.toCollection(LinkedList::new))
                    .getLast()
                    .text()
            ).decode()
        );
    }

    /**
     * Prestructor for Cast#origin.
     *
     * @param node XML node
     * @param target Function to determine the origin node.
     * @return Origin node.
     */
    private static AstNode xorigin(
        final XmlNode node,
        final Function<? super XmlNode, ? extends AstNode> target
    ) {
        return target.apply(node.children().findFirst().orElseThrow());
    }
}
