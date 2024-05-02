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
package org.eolang.opeo.compilation;

import java.nio.file.Path;
import org.eolang.opeo.storage.CompilationStorage;
import org.eolang.opeo.storage.Storage;

/**
 * Dummy compiler.
 * It just copies XMIR files to the output directory without any changes.
 * @since 0.2
 */
public final class DummyCompiler implements Compiler {

    /**
     * The storage where the XMIRs are stored.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param xmirs Path to the generated XMIRs by jeo-maven-plugin.
     * @param output Path to the output directory.
     */
    public DummyCompiler(final Path xmirs, final Path output) {
        this(new CompilationStorage(xmirs, output));
    }

    /**
     * Constructor.
     * @param storage The storage where the XMIRs are stored.
     */
    private DummyCompiler(final Storage storage) {
        this.storage = storage;
    }

    @Override
    public void compile() {
        this.storage.all().forEach(this.storage::save);
    }
}
