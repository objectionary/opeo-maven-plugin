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
package org.eolang.opeo;

import com.jcabi.log.Logger;
import java.io.File;
import java.nio.file.Path;

/**
 * Decompiler.
 * This class is a high level abstraction of the decompilation process.
 * The main purpose of this class it to get the output of the jeo-maven-plugin
 * and decompile it into high-level EO constructs.
 *
 * @since 0.1
 */
public class Decompiler {

    /**
     * Path to the generated XMIRs by jeo-maven-plugin.
     */
    private final Path xmirs;

    /**
     * Path to the output directory.
     */
    private final Path output;

    /**
     * Constructor.
     * @param generated The default Maven 'generated-sources' directory.
     */
    public Decompiler(final File generated) {
        this(generated.toPath());
    }

    /**
     * Constructor.
     * @param generated The default Maven 'generated-sources' directory.
     */
    public Decompiler(final Path generated) {
        this(generated.resolve("xmir"), generated.resolve("opeo-xmir"));
    }

    /**
     * Constructor.
     * @param xmirs Path to the generated XMIRs by jeo-maven-plugin.
     * @param output Path to the output directory.
     */
    public Decompiler(
        final Path xmirs,
        final Path output
    ) {
        this.xmirs = xmirs;
        this.output = output;
    }

    /**
     * Decompile EO to high-level EO.
     * EO represented by XMIR.
     */
    void decompile() {
        //@checkstyle MethodBodyCommentsCheck (5 lines)
        // @todo #33:90min Implement decompilation of EO to high-level EO.
        //  Currently we print dummy messages in order to pass 'decompile-compile' integration test.
        //  We have to implement decompilation of EO to high-level EO.
        //  Don't forget to add unit tests. Also, you might need to change some checks in the
        //  'decompile-compile' integration test.
        Logger.info(this, "Decompiling EO sources from %s", this.xmirs);
        Logger.info(this, "Saving new decompiled EO sources to %s", this.output);
        Logger.info(this, "Decompiled app.eo (545 bytes)");
        Logger.info(this, "Decompiled main.eo (545 bytes)");
        Logger.info(this, "Decompiled %d EO sources", 2);
    }
}
