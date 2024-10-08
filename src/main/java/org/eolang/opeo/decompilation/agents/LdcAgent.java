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
package org.eolang.opeo.decompilation.agents;

import org.eolang.opeo.ast.Const;
import org.eolang.opeo.decompilation.DecompilerState;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Ldc instruction handler.
 * @since 0.1
 */
public final class LdcAgent implements DecompilationAgent {

    @Override
    public boolean appropriate(final DecompilerState state) {
        return new OpcodesAgent(this).appropriate(state);
    }

    @Override
    public Supported supported() {
        return new Supported(Opcodes.LDC);
    }

    @Override
    public void handle(final DecompilerState state) {
        if (this.appropriate(state)) {
            final Object operand = state.operand(0);
            state.stack().push(new Const(operand, LdcAgent.type(operand)));
            state.popInstruction();
        } else {
            throw new IllegalAgentException(this, state);
        }
    }

    /**
     * Determine the value type.
     * Since Java doesn't allow using primitive types for Object, we need to
     * determine the type of the value.
     * @param value Object value.
     * @return Type.
     */
    private static Type type(final Object value) {
        final Class<?> clazz = value.getClass();
        final Type result;
        if (clazz == Integer.class) {
            result = Type.INT_TYPE;
        } else if (clazz == Long.class) {
            result = Type.LONG_TYPE;
        } else if (clazz == Float.class) {
            result = Type.FLOAT_TYPE;
        } else if (clazz == Double.class) {
            result = Type.DOUBLE_TYPE;
        } else if (clazz == Short.class) {
            result = Type.SHORT_TYPE;
        } else if (clazz == Byte.class) {
            result = Type.BYTE_TYPE;
        } else if (clazz == Character.class) {
            result = Type.CHAR_TYPE;
        } else if (clazz == Boolean.class) {
            result = Type.BOOLEAN_TYPE;
        } else {
            result = Type.getType(clazz);
        }
        return result;
    }
}
