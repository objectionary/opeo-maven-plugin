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

import java.util.regex.Pattern;

/**
 * Prefixed name.
 * @since 0.1
 */
public final class PrefixedName {

    private static final Pattern PREFIX = Pattern.compile("j$", Pattern.LITERAL);

    /**
     * Original name.
     */
    private final String original;

    /**
     * Constructor.
     * @param original Original name.
     */
    public PrefixedName(final String original) {
        this.original = original;
    }


    public String withPrefix() {
        final String delimiter = delimiter();
        final String[] split = this.original.split(String.format("[%s]", delimiter));
        if (split.length < 1) {
            throw new IllegalArgumentException(String.format("Invalid name '%s'", this.original));
        } else {
            split[split.length - 1] = String.format("j$%s", split[split.length - 1]);
        }
        return String.join(delimiter, split);
    }

    public String withoutPrefix() {
        final String delimiter = delimiter();
        final String[] split = this.original.split(String.format("[%s]", delimiter));
        if (split.length < 1) {
            throw new IllegalArgumentException(String.format("Invalid name '%s'", this.original));
        } else {
            split[split.length - 1] = PrefixedName.PREFIX
                .matcher(split[split.length - 1])
                .replaceAll("");
        }
        return String.join(delimiter, split);
    }

    private String delimiter() {
        final String delimiter;
        if (this.original.contains(".")) {
            delimiter = ".";
        } else {
            delimiter = "/";
        }
        return delimiter;
    }
}
