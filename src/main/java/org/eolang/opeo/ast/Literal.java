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
import org.eolang.jeo.representation.directives.DirectivesData;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;

/**
 * Literal output.
 * @since 0.1
 */
public final class Literal implements AstNode, Typed {

    /**
     * Literal value.
     */
    private final Object value;

    /**
     * Literal type.
     */
    private final Type type;

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
        this.value = value;
        this.type = type;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new DirectivesData(this.value);
    }

    @Override
    public List<AstNode> opcodes() {
        final Opcode res;
        if (this.type.equals(Type.CHAR_TYPE)) {
            res = Literal.opcode((char) this.value);
        } else if (this.type.equals(Type.BOOLEAN_TYPE)) {
            res = Literal.opcode((boolean) this.value);
        } else if (this.type.equals(Type.BYTE_TYPE)) {
            res = new Opcode(Opcodes.BIPUSH, this.value);
        } else if (this.type.equals(Type.SHORT_TYPE)) {
            res = new Opcode(Opcodes.SIPUSH, this.value);
        } else if (this.type.equals(Type.INT_TYPE)) {
            res = Literal.opcode((int) this.value);
        } else if (this.type.equals(Type.LONG_TYPE)) {
            res = Literal.opcode((long) this.value);
        } else if (this.type.equals(Type.FLOAT_TYPE)) {
            res = new Opcode(Opcodes.LDC, this.value);
        } else if (this.type.equals(Type.DOUBLE_TYPE)) {
            res = new Opcode(Opcodes.LDC, this.value);
        } else if (this.type.equals(Type.getType(String.class))) {
            res = new Opcode(Opcodes.LDC, this.value);
        } else {
            throw new IllegalArgumentException(
                String.format(
                    "Unsupported literal type %s, value is %s",
                    this.type.getClassName(),
                    this.value
                )
            );
        }
        return Collections.singletonList(res);
    }

    @Override
    public Type type() {
        return this.type;
    }

    /**
     * Convert integer into an opcode.
     * @param value Integer value.
     * @return Opcode.
     */
    private static Opcode opcode(final int value) {
        final Opcode res;
        switch (value) {
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
}
