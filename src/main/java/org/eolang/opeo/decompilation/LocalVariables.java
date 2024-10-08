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
package org.eolang.opeo.decompilation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.LocalVariable;
import org.eolang.opeo.ast.This;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Local variables.
 * @since 0.1
 */
@EqualsAndHashCode
@ToString
public final class LocalVariables {

    /**
     * Method access modifiers.
     */
    private final int modifiers;

    /**
     * Method argument types.
     */
    private final Type[] types;

    /**
     * Cache of variables.
     */
    private final Map<Integer, AstNode> cache;

    /**
     * Class type.
     * We need a class name to correctly generate the 'this' reference with the correct type.
     */
    private final Type clazz;

    /**
     * Constructor.
     */
    public LocalVariables() {
        this(Opcodes.ACC_PUBLIC, Type.getType(Object.class));
    }

    /**
     * Constructor.
     * @param modifiers Method access modifiers.
     * @param descriptor Method descriptor.
     * @param type Method type.
     */
    public LocalVariables(final int modifiers, final String descriptor, final String type) {
        this(modifiers, new VariablesArray(modifiers, descriptor).array(), Type.getType(type));
    }

    /**
     * Constructor.
     * @param modifiers Method access modifiers.
     * @param descriptor Method descriptor.
     */
    public LocalVariables(final int modifiers, final String descriptor) {
        this(modifiers, new VariablesArray(modifiers, descriptor).array());
    }

    /**
     * Constructor.
     * @param modifiers Method access modifiers.
     * @param types Method argument types.
     */
    private LocalVariables(final int modifiers, final Type... types) {
        this(modifiers, types, Type.getType(Object.class));
    }

    /**
     * Constructor.
     * @param modifiers Method access modifiers.
     * @param types Method argument types.
     * @param clazz Class type.
     */
    public LocalVariables(
        final int modifiers,
        final Type[] types,
        final Type clazz
    ) {
        this.modifiers = modifiers;
        this.types = Arrays.copyOf(types, types.length);
        this.cache = new HashMap<>(0);
        this.clazz = clazz;
    }

    /**
     * Get variable by index.
     * @param index Index.
     * @param type Type.
     * @return Variable.
     */
    public AstNode variable(final int index, final Type type) {
        return this.restore(index).orElseGet(() -> this.store(index, type));
    }

    /**
     * Restore variable from cache.
     * @param index Index.
     * @return Variable.
     */
    private Optional<AstNode> restore(final int index) {
        final Optional<AstNode> result;
        if (this.cache.containsKey(index)) {
            result = Optional.of(this.cache.get(index));
        } else {
            result = Optional.empty();
        }
        return result;
    }

    /**
     * Store variable in cache.
     * @param index Index.
     * @param fallback Fallback type.
     * @return Variable.
     */
    private AstNode store(final int index, final Type fallback) {
        final Type type = this.argumentType(index).orElse(fallback);
        final AstNode result;
        if (index == 0 && this.isInstanceMethod()) {
            result = new This(this.clazz);
        } else {
            result = new LocalVariable(index, type);
        }
        this.cache.put(index, result);
        return result;
    }

    /**
     * Is it an instance method?
     * @return True if it is an instance method.
     */
    private boolean isInstanceMethod() {
        return (this.modifiers & Opcodes.ACC_STATIC) == 0;
    }

    /**
     * Find an argument type by index.
     * @param index Index.
     * @return Type.
     */
    private Optional<Type> argumentType(final int index) {
        final Optional<Type> result;
        if (index < 0 || index >= this.types.length) {
            result = Optional.empty();
        } else {
            result = Optional.ofNullable(this.types[index]);
        }
        return result;
    }

    /**
     * Variables array with types.
     * @since 0.2
     */
    private static final class VariablesArray {

        /**
         * Method modifiers.
         */
        private final int modifiers;

        /**
         * Method descriptor.
         */
        private final String descriptor;

        /**
         * Constructor.
         * @param modifiers Method modifiers.
         * @param descriptor Method descriptor.
         */
        private VariablesArray(final int modifiers, final String descriptor) {
            this.modifiers = modifiers;
            this.descriptor = descriptor;
        }

        /**
         * Convert descriptor to an array of types.
         * In bytecode:
         * - aload_0 is used to load the reference to this onto the stack.
         * So index 0 is reserved for this.
         * - 'long' and 'double' values occupy two local variable slots each because
         * they are 64-bit values.
         *
         * @return Array of types.
         */
        Type[] array() {
            final Type[] types = Type.getArgumentTypes(this.descriptor);
            final Type[] result = new Type[this.size()];
            int offset = 0;
            if (this.isInstanceMethod()) {
                offset = offset + 1;
            }
            for (int index = 0; index < types.length; ++index) {
                final Type current = types[index];
                result[index + offset] = current;
                if (current.getSize() > 1) {
                    offset = offset + 1;
                    result[index + offset] = current;
                }
            }
            return result;
        }

        /**
         * Calculate the size of the array with types.
         * @return Size.
         */
        private int size() {
            final int result;
            final int res = Arrays.stream(Type.getArgumentTypes(this.descriptor))
                .mapToInt(Type::getSize)
                .sum();
            if (this.isInstanceMethod()) {
                result = res + 1;
            } else {
                result = res;
            }
            return result;
        }

        /**
         * Is it an instance method?
         * @return True if it is an instance method.
         */
        private boolean isInstanceMethod() {
            return (this.modifiers & Opcodes.ACC_STATIC) == 0;
        }
    }
}
