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

/**
 * Method name with a type.
 * This class adds a type to a method name.
 * Also, it can be used to remove a type from a method name.
 * In other words, it allows the following translations:
 * `foo` <-> `java_lang_utils_Stream$foo`
 * `of` <-> `java_util_Stream$of`
 * `map` <-> `java_util_Stream$map`
 * You can find more examples in unit tests.
 * @since 0.4
 */
public final class TypedName {

    /**
     * Original name with or without a type.
     */
    private final String original;
    private char DELIMITER = '$';

    /**
     * Constructor.
     * @param original Original name with or without a type.
     */
    public TypedName(final String original) {
        this.original = original;
    }

    /**
     * Remove a type from the name.
     * @return Name without a type.
     */
    public String withoutType() {
        return this.original.substring(this.original.indexOf(this.DELIMITER) + 1);
    }

    /**
     * Add a type to the name.
     * @param attributes Attributes that have the type information.
     * @return Name with a type.
     */
    public String withType(final Attributes attributes) {
        final String descriptor = attributes.descriptor();
        if (descriptor.isEmpty()) {
            throw new IllegalStateException(
                String.format("Descriptor in attributes '%s' is empty", attributes)
            );
        }
        final Type type = Type.getReturnType(descriptor);
        final String className = type.getClassName();
        return String.join(
            String.format("%s", this.DELIMITER),
            className.replace('.', '_'),
            this.original
        );
    }
}
