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
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.eolang.opeo.jeo.JeoDecompiler;

/**
 * Decompiler.
 * This class is a high level abstraction of the decompilation process.
 * The main purpose of this class it to get the output of the jeo-maven-plugin
 * and decompile it into high-level EO constructs.
 *
 * @since 0.1
 */
public final class Decompiler {

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
        Logger.info(this, "Decompiling EO sources from %[file]s", this.xmirs);
        Logger.info(this, "Saving new decompiled EO sources to %[file]s", this.output);
        try (Stream<Path> files = Files.walk(this.xmirs).filter(Files::isRegularFile)) {
            Logger.info(
                this,
                "Decompiled %d EO sources",
                files.filter(Decompiler::isXmir).peek(this::decompile).count()
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't decompile files from '%s'", this.xmirs),
                exception
            );
        }
    }

    /**
     * Decompile XMIR to high-level EO.
     * @param path Path to the XMIR file.
     */
    private void decompile(final Path path) {
        try {
            final XML decompiled = new JeoDecompiler(new XMLDocument(path)).decompile();
            final Path out = this.output.resolve(this.xmirs.relativize(path));
            Files.createDirectories(out.getParent());
            Files.write(
                out,
                decompiled.toString().getBytes(StandardCharsets.UTF_8)
            );
            Logger.info(this, "Decompiled %[file]s (%[size]s)", out, Files.size(out));
        } catch (final FileNotFoundException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't find the file '%s' for decompilation in the '%s' folder",
                    path,
                    this.xmirs
                ),
                exception
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't decompile file '%s' in the '%s' folder",
                    path,
                    this.xmirs
                ),
                exception
            );
        }
    }

    /**
     * Check if the file is XMIR.
     * @param path Path to the file.
     * @return True if the file is XMIR.
     */
    private static boolean isXmir(final Path path) {
        return path.toString().endsWith(".xmir");
    }
}
