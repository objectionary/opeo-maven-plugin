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

import com.jcabi.log.Logger;
import java.nio.file.Path;
import org.eolang.opeo.storage.CompilationStorage;
import org.eolang.opeo.storage.Storage;
import org.eolang.opeo.storage.XmirEntry;

/**
 * Compiler of high-level eo constructs into XMIRs for the jeo-maven-plugin.
 * @since 0.1
 */
public class DefaultCompiler implements Compiler {

    /**
     * Storage where the XMIRs are stored.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param xmirs Path to the generated XMIRs by opeo-maven-plugin.
     * @param output Path to the output directory.
     */
    public DefaultCompiler(final Path xmirs, final Path output) {
        this(new CompilationStorage(xmirs, output));
    }

    /**
     * Constructor.
     * @param generated The default Maven 'generated-sources' directory.
     */
    DefaultCompiler(final Path generated) {
        this(generated.resolve("opeo-xmir"), generated.resolve("xmir"));
    }

    /**
     * Constructor.
     * @param storage The storage where the XMIRs are stored.
     */
    private DefaultCompiler(final Storage storage) {
        this.storage = storage;
    }

    /**
     * Compile high-level EO constructs into XMIRs for the jeo-maven-plugin.
     */
    public void compile() {
        Logger.info(
            this,
            "Compiled %d sources",
            this.storage.all()
                .parallel()
                .mapToInt(this::compile)
                .sum()
        );
    }

    /**
     * Compile the file.
     * @param xmir Xmir.
     * @return Number of compiled files.
     */
    private int compile(final XmirEntry xmir) {
        this.storage.save(xmir.transform(xml -> new JeoCompiler(xml).compile()));
        return 1;
    }

}
