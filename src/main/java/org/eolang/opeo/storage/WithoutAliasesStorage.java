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
package org.eolang.opeo.storage;

import java.util.stream.Stream;
import org.eolang.opeo.decompilation.WithoutAliases;

/**
 * Storage without aliases.
 * @since 0.4
 * @todo #355:30min Remove the crutch related to aliases.
 *  We use {@link WithoutAliases} class here and in the
 *  {@link org.eolang.opeo.SelectiveDecompiler} to avoid the problem related to redundant
 *  aliases in the XMIR. We should add aliases only when they are needed.
 *  This is the similar problem to the one described in the
 *  <a href="https://github.com/objectionary/jeo-maven-plugin/issues/647">issue</a>
 */
public final class WithoutAliasesStorage implements Storage {

    /**
     * Original storage.
     * In other words, it is a delegate.
     */
    private final Storage origin;

    /**
     * Constructor.
     * @param origin Original storage.
     */
    public WithoutAliasesStorage(final Storage origin) {
        this.origin = origin;
    }

    @Override
    public Stream<XmirEntry> all() {
        return this.origin.all();
    }

    @Override
    public void save(final XmirEntry xmir) {
        this.origin.save(xmir.transform(xml -> new WithoutAliases(xml).toXml()));
    }
}
