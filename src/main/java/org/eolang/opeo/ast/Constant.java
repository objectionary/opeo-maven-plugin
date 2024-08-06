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
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.DataType;
import org.eolang.jeo.representation.directives.DirectivesData;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Constant.
 * This class represents LDC instruction in the JVM bytecode.
 * @since 0.2
 * @todo #229:90min Do we need {@link Constant} class?
 *  It seems that this class is rather similar to {@link Literal} class.
 *  We need to investigate if we can remove this class and use {@link Literal} instead.
 *  If we can't remove this class, we need to add more tests to cover all the cases
 *  where {@link Constant} is used.
 */
@ToString
@EqualsAndHashCode
public final class Constant implements AstNode, Typed {

    /**
     * The constant value.
     */
    private final Object value;

    /**
     * The constant value type.
     */
    private final Type vtype;

    /**
     * Constructor.
     * @param node The XMIR node with value to parse.
     */
    public Constant(final XmlNode node) {
        this(Constant.xvalue(node), Constant.xtype(node));
    }

    /**
     * Constructor.
     * @param value The constant value.
     */
    public Constant(final Object value) {
        this(value, Type.getType(value.getClass()));
    }

    /**
     * Constructor.
     * @param value The constant value.
     * @param type The constant value type.
     */
    public Constant(final Object value, final Type type) {
        this.value = value;
        this.vtype = type;
    }

    @Override
    public List<AstNode> opcodes() {
        return Collections.singletonList(new Opcode(Opcodes.LDC, this.value));
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives().add("o")
            .attr("base", "load-constant")
            .append(new DirectivesData(this.value))
            .up();
    }

    @Override
    public Type type() {
        return this.vtype;
    }

    /**
     * Parse the Constant type from XMIR representation.
     * @param node The node to parse.
     * @return The parsed type.
     */
    private static Type xtype(final XmlNode node) {
        final XmlNode child = node.firstChild();
        final DataType type = DataType.find(
            child.attribute("base").orElseThrow(
                () -> new IllegalStateException("Constant node has no 'base' attribute")
            )
        );
        final Type result;
        switch (type) {
            case INT:
                result = Type.INT_TYPE;
                break;
            case LONG:
                result = Type.LONG_TYPE;
                break;
            case FLOAT:
                result = Type.FLOAT_TYPE;
                break;
            case DOUBLE:
                result = Type.DOUBLE_TYPE;
                break;
            case CHAR:
                result = Type.CHAR_TYPE;
                break;
            case BYTE:
                result = Type.BYTE_TYPE;
                break;
            case SHORT:
                result = Type.SHORT_TYPE;
                break;
            case STRING:
                result = Type.getType(String.class);
                break;
            case TYPE_REFERENCE:
                result = Type.getType(Type.class);
                break;
            default:
                result = Type.getType(type.type());
                break;
        }
        return result;
    }

    /**
     * Parse the Constant value from XMIR representation.
     * @param node The node to parse.
     * @return The parsed value.
     */
    private static Object xvalue(final XmlNode node) {
        final XmlNode child = node.firstChild();
        final DataType type = DataType.find(
            child.attribute("base").orElseThrow(
                () -> new IllegalStateException("Constant node has no 'base' attribute")
            )
        );
        return type.decode(child.text());
    }
}
