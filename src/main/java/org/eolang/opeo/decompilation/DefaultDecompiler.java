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

import com.jcabi.log.Logger;
import java.nio.file.Path;
import org.eolang.opeo.jeo.JeoDecompiler;
import org.eolang.opeo.storage.DecompilationStorage;
import org.eolang.opeo.storage.Storage;
import org.eolang.opeo.storage.XmirEntry;

/**
 * Default Decompiler.
 * This class is a high-level abstraction of the decompilation process.
 * The main purpose of this class is to get the output of the jeo-maven-plugin
 * and decompile it into high-level EO constructs.
 *
 * @since 0.1
 */
public final class DefaultDecompiler implements Decompiler {

    /**
     * The storage where the XMIRs are stored.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param xmirs Path to the generated XMIRs by jeo-maven-plugin.
     * @param output Path to the output directory.
     */
    public DefaultDecompiler(
        final Path xmirs,
        final Path output
    ) {
        this(new DecompilationStorage(xmirs, output));
    }

    /**
     * Constructor.
     * @param generated The default Maven 'generated-sources' directory.
     */
    DefaultDecompiler(final Path generated) {
        this(generated.resolve("xmir"), generated.resolve("opeo-xmir"));
    }

    /**
     * Constructor.
     * @param storage The storage where the XMIRs are stored.
     */
    private DefaultDecompiler(final Storage storage) {
        this.storage = storage;
    }

    @Override
    public void decompile() {
        Logger.info(
            this,
            "Decompiled %d EO sources",
            this.storage.all()
                .parallel()
                .mapToInt(this::decompile).sum()
        );
    }

    /**
     * Decompile the entry.
     * @param entry The entry to decompile.
     * @return Number of decompiled EO sources.
     */
    private int decompile(final XmirEntry entry) {
        this.storage.save(entry.transform(xml -> new JeoDecompiler(xml).decompile()));
        return 1;
    }

}
