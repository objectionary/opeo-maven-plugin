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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * Storage that keeps everything in memory.
 * This storage is rather useful for unit tests.
 * @since 0.2
 */
public final class InMemoryStorage implements Storage {

    /**
     * Container where everything is stored.
     */
    private final List<XmirEntry> container;

    /**
     * Constructor.
     */
    public InMemoryStorage() {
        this(new CopyOnWriteArrayList<>());
    }

    /**
     * Constructor.
     * @param container Container where everything is stored.
     */
    public InMemoryStorage(final List<XmirEntry> container) {
        this.container = container;
    }

    @Override
    public Stream<XmirEntry> all() {
        final Stream<XmirEntry> stream = this.container.stream();
        this.container.clear();
        return stream;
    }

    @Override
    public void save(final XmirEntry xmir) {
        this.container.add(xmir);
    }

    /**
     * Get the last saved entry.
     * @return The last saved entry.
     */
    public XmirEntry last() {
        if (!this.container.isEmpty()) {
            return this.container.get(this.container.size() - 1);
        } else {
            throw new IllegalStateException("Storage is empty");
        }
    }
}
