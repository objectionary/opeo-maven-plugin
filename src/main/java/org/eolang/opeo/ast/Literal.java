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

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
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
 * Literal output.
 * @since 0.1
 * @checkstyle CyclomaticComplexityCheck (500 lines)
 */
@ToString
@EqualsAndHashCode
@SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods"})
public final class Literal implements AstNode, Typed {

    /**
     * Literal value.
     */
    private final Object lvalue;

    /**
     * Literal type.
     */
    private final Type ltype;

    /**
     * Constructor.
     * Null literal.
     */
    public Literal() {
        this(null, Type.VOID_TYPE);
    }

    /**
     * Constructor.
     * @param value Char literal value.
     */
    public Literal(final char value) {
        this(value, Type.CHAR_TYPE);
    }

    /**
     * Constructor.
     * @param value Boolean literal value.
     */
    public Literal(final boolean value) {
        this(value, Type.BOOLEAN_TYPE);
    }

    /**
     * Constructor.
     * @param value Byte literal value.
     */
    public Literal(final byte value) {
        this(value, Type.BYTE_TYPE);
    }

    /**
     * Constructor.
     * @param value Short literal value.
     */
    public Literal(final short value) {
        this(value, Type.SHORT_TYPE);
    }

    /**
     * Constructor.
     * @param value Integer literal value.
     */
    public Literal(final int value) {
        this(value, Type.INT_TYPE);
    }

    /**
     * Constructor.
     * @param value Long literal value.
     */
    public Literal(final long value) {
        this(value, Type.LONG_TYPE);
    }

    /**
     * Constructor.
     * @param value Float literal value.
     */
    public Literal(final float value) {
        this(value, Type.FLOAT_TYPE);
    }

    /**
     * Constructor.
     * @param value Double literal value.
     */
    public Literal(final double value) {
        this(value, Type.DOUBLE_TYPE);
    }

    /**
     * Constructor.
     * @param node XML node.
     */
    public Literal(final XmlNode node) {
        this(Literal.xvalue(node), Literal.xtype(node));
    }

    /**
     * Constructor.
     * @param value Literal value.
     */
    public Literal(final Object value) {
        this(value, Type.getType(value.getClass()));
    }

    /**
     * Constructor.
     * @param value Literal value.
     * @param type Literal type.
     */
    public Literal(final Object value, final Type type) {
        this.lvalue = value;
        this.ltype = type;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new DirectivesData(this.lvalue);
    }

    @Override
    public List<AstNode> opcodes() {
        final Opcode res;
        if (this.ltype.equals(Type.CHAR_TYPE)) {
            res = Literal.opcode((char) this.lvalue);
        } else if (this.ltype.equals(Type.BOOLEAN_TYPE)) {
            res = Literal.opcode((boolean) this.lvalue);
        } else if (this.ltype.equals(Type.BYTE_TYPE)) {
            res = new Opcode(Opcodes.BIPUSH, this.lvalue);
        } else if (this.ltype.equals(Type.SHORT_TYPE)) {
            res = new Opcode(Opcodes.SIPUSH, this.lvalue);
        } else if (this.ltype.equals(Type.INT_TYPE)) {
            res = Literal.opcode((int) this.lvalue);
        } else if (this.ltype.equals(Type.LONG_TYPE)) {
            res = Literal.opcode((long) this.lvalue);
        } else if (this.ltype.equals(Type.FLOAT_TYPE)) {
            res = Literal.opcode((float) this.lvalue);
        } else if (this.ltype.equals(Type.DOUBLE_TYPE)) {
            res = Literal.opcode((double) this.lvalue);
        } else if (this.ltype.equals(Type.getType(String.class))) {
            res = new Opcode(Opcodes.LDC, this.lvalue);
        } else if (this.ltype.equals(Type.VOID_TYPE)) {
            res = new Opcode(Opcodes.ACONST_NULL);
        } else {
            throw new IllegalArgumentException(
                String.format(
                    "Unsupported literal type %s, value is %s",
                    this.ltype.getClassName(),
                    this.lvalue
                )
            );
        }
        return Collections.singletonList(res);
    }

    @Override
    public Type type() {
        return this.ltype;
    }

    /**
     * Bytes to HEX.
     *
     * @param bytes Bytes.
     * @return Hexadecimal value as string.
     */
    private static String bytesToHex(final byte... bytes) {
        final StringJoiner out = new StringJoiner(" ");
        for (final byte bty : bytes) {
            out.add(String.format("%02X", bty));
        }
        return out.toString();
    }

    /**
     * Convert integer into an opcode.
     * @param value Integer value.
     * @return Opcode.
     */
    private static Opcode opcode(final int value) {
        final Opcode res;
        switch (value) {
            case -1:
                res = new Opcode(Opcodes.ICONST_M1);
                break;
            case 0:
                res = new Opcode(Opcodes.ICONST_0);
                break;
            case 1:
                res = new Opcode(Opcodes.ICONST_1);
                break;
            case 2:
                res = new Opcode(Opcodes.ICONST_2);
                break;
            case 3:
                res = new Opcode(Opcodes.ICONST_3);
                break;
            case 4:
                res = new Opcode(Opcodes.ICONST_4);
                break;
            case 5:
                res = new Opcode(Opcodes.ICONST_5);
                break;
            default:
                res = new Opcode(Opcodes.BIPUSH, value);
                break;
        }
        return res;
    }

    /**
     * Convert char into an opcode.
     * @param value Char value.
     * @return Opcode.
     */
    private static Opcode opcode(final char value) {
        return new Opcode(Opcodes.BIPUSH, value);
    }

    /**
     * Convert boolean into an opcode.
     * @param value Boolean value.
     * @return Opcode.
     */
    private static Opcode opcode(final boolean value) {
        final Opcode result;
        if (value) {
            result = new Opcode(Opcodes.ICONST_1);
        } else {
            result = new Opcode(Opcodes.ICONST_0);
        }
        return result;
    }

    /**
     * Convert long into an opcode.
     * @param value Long value.
     * @return Opcode.
     */
    private static Opcode opcode(final long value) {
        final Opcode res;
        if (value == 0L) {
            res = new Opcode(Opcodes.LCONST_0);
        } else if (value == 1L) {
            res = new Opcode(Opcodes.LCONST_1);
        } else {
            res = new Opcode(Opcodes.LDC, value);
        }
        return res;
    }

    /**
     * Convert double into an opcode.
     * @param value Double value.
     * @return Opcode.
     */
    private static Opcode opcode(final double value) {
        final Opcode res;
        if (value == 0.0d) {
            res = new Opcode(Opcodes.DCONST_0);
        } else if (value == 1.0d) {
            res = new Opcode(Opcodes.DCONST_1);
        } else {
            res = new Opcode(Opcodes.LDC, value);
        }
        return res;
    }

    /**
     * Convert float into an opcode.
     * @param value Float value.
     * @return Opcode.
     */
    private static Opcode opcode(final float value) {
        final Opcode res;
        if (value == 0.0f) {
            res = new Opcode(Opcodes.FCONST_0);
        } else if (value == 1.0f) {
            res = new Opcode(Opcodes.FCONST_1);
        } else if (value == 2.0f) {
            res = new Opcode(Opcodes.FCONST_2);
        } else {
            res = new Opcode(Opcodes.LDC, value);
        }
        return res;
    }

    /**
     * Convert XML node into a value.
     * @param node XML node
     * @return Value.
     */
    private static Object xvalue(final XmlNode node) {
        final Object result;
        final Type type = Literal.xtype(node);
        if (type.equals(Type.INT_TYPE)) {
            result = new HexString(node.text()).decodeAsInt();
        } else if (type.equals(Type.BOOLEAN_TYPE)) {
            result = new HexString(node.text()).decodeAsBoolean();
        } else if (type.equals(Type.LONG_TYPE)) {
            result = Long.parseLong(node.text().trim().replace(" ", ""), 16);
        } else {
            result = new HexString(node.text()).decode();
        }
        return result;
    }

    /**
     * Prestructor for Literal#ltype.
     * @param node XML node
     * @return Literal type.
     */
    private static Type xtype(final XmlNode node) {
        final Type result;
        final String attribute = node.attribute("base").orElseThrow();
        switch (attribute) {
            case "string":
                result = Type.getType(String.class);
                break;
            case "int":
                result = Type.INT_TYPE;
                break;
            case "char":
                result = Type.CHAR_TYPE;
                break;
            case "long":
                result = Type.LONG_TYPE;
                break;
            case "float":
                result = Type.FLOAT_TYPE;
                break;
            case "double":
                result = Type.DOUBLE_TYPE;
                break;
            case "boolean":
                result = Type.BOOLEAN_TYPE;
                break;
            case "byte":
                result = Type.BYTE_TYPE;
                break;
            case "short":
                result = Type.SHORT_TYPE;
                break;
            default:
                throw new IllegalStateException(
                    String.format(
                        "Unsupported literal type %s",
                        attribute
                    )
                );
        }
        return result;
    }
}
