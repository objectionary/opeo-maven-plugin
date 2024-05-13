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

import com.jcabi.log.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Decompilation storage.
 * The same as {@link FileStorage}, but with some logging.
 * @since 0.2
 */
public final class DecompilationStorage implements Storage {

    /**
     * Path to the generated XMIRs by jeo-maven-plugin.
     */
    private final Path xmirs;

    /**
     * Path to the output directory.
     */
    private final Path output;

    /**
     * The original storage.
     */
    private final Storage original;

    /**
     * Constructor.
     * @param xmirs Path to the generated XMIRs by jeo-maven-plugin.
     * @param output Path to the output directory.
     */
    public DecompilationStorage(
        final Path xmirs,
        final Path output
    ) {
        this.xmirs = xmirs;
        this.output = output;
        this.original = new FileStorage(xmirs, output);
    }

    @Override
    public Stream<XmirEntry> all() {
        Logger.info(this, "Decompiling EO sources from %[file]s", this.xmirs);
        Logger.info(this, "Saving new decompiled EO sources to %[file]s", this.output);
        return this.original.all();
    }

    @Override
    public void save(final XmirEntry xmir) {
        try {
            final Path out = this.output.resolve(Path.of(xmir.relative()));
            this.original.save(xmir);
            Logger.info(
                this,
                "Decompiled %[file]s (%[size]s)",
                this.output.resolve(out),
                Files.size(out)
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't decompile file '%s' in the '%s' folder",
                    xmir.relative(),
                    this.xmirs
                ),
                exception
            );
        }
    }
}
