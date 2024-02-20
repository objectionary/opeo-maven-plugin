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
 * @checkstyle CyclomaticComplexityCheck (500 lines)
 * @todo #129:90min Implement all types of literals.
 *  Currently, only int, long and string literals are supported.
 *  Add support for float, double, boolean, char, byte, short and other types.
 *  Don't forget to add tests for all supported types.
 */
public final class Literal implements AstNode, Typed {

    /**
     * Literal value.
     */
    private final Object object;

    /**
     * Constructor.
     * @param value Literal value
     */
    public Literal(final Object value) {
        this.object = value;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new DirectivesData(this.object);
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> result;
        if (this.object instanceof Integer) {
            result = Collections.singletonList(Literal.opcode((Integer) this.object));
        } else if (this.object instanceof Long) {
            result = Collections.singletonList(Literal.opcode((Long) this.object));
        } else if (this.object instanceof String) {
            result = Collections.singletonList(Literal.opcode((String) this.object));
        } else {
            throw new IllegalArgumentException(
                String.format(
                    "Unsupported literal type %s, value is %s",
                    this.object.getClass().getName(),
                    this.object
                )
            );
        }
        return result;
    }

    @Override
    public Type type() {
        final Type result;
        final Class<?> clazz = this.object.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            result = Type.INT_TYPE;
        } else if (clazz == long.class || clazz == Long.class) {
            result = Type.LONG_TYPE;
        } else if (clazz == float.class || clazz == Float.class) {
            result = Type.FLOAT_TYPE;
        } else if (clazz == double.class || clazz == Double.class) {
            result = Type.DOUBLE_TYPE;
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            result = Type.BOOLEAN_TYPE;
        } else if (clazz == char.class || clazz == Character.class) {
            result = Type.CHAR_TYPE;
        } else if (clazz == byte.class || clazz == Byte.class) {
            result = Type.BYTE_TYPE;
        } else if (clazz == short.class || clazz == Short.class) {
            result = Type.SHORT_TYPE;
        } else if (clazz == String.class) {
            result = Type.getType(String.class);
        } else {
            result = Type.getType(clazz);
        }
        return result;
    }

    /**
     * Convert string into an opcode.
     * @param value String value.
     * @return Opcode.
     */
    private static Opcode opcode(final String value) {
        return new Opcode(Opcodes.LDC, value);
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
