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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * File storage.
 * @since 0.2
 */
public final class FileStorage implements Storage {

    /**
     * Path to the source folder.
     */
    private final Path xmirs;

    /**
     * Path to the output folder.
     */
    private final Path output;

    /**
     * Constructor.
     * @param xmirs Path to the source folder.
     * @param output Path to the output folder.
     */
    public FileStorage(final Path xmirs, final Path output) {
        this.xmirs = xmirs;
        this.output = output;
    }

    @Override
    public Stream<XmirEntry> all() {
        if (!Files.exists(this.xmirs)) {
            throw new IllegalArgumentException(
                String.format(
                    "The input XMIR folder '%s' doesn't exist",
                    this.xmirs
                )
            );
        }
        try {
            return Files.walk(this.xmirs)
                .filter(Files::isRegularFile)
                .filter(FileStorage::isXmir)
                .map(this::entry);
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't retrieve XMIR files from the '%s' folder", this.xmirs),
                exception
            );
        } catch (final IllegalArgumentException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't retrieve XMIR files from '%s' directory, current directory is '%s'",
                    this.xmirs,
                    Paths.get("").toAbsolutePath()
                ),
                exception
            );
        }
    }

    @Override
    public void save(final XmirEntry xmir) {
        final Path out = this.output.resolve(Path.of(xmir.relative()));
        try {
            Files.createDirectories(out.getParent());
            Files.write(
                out,
                xmir.toXml().toString().getBytes(StandardCharsets.UTF_8)
            );
        } catch (final IllegalArgumentException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't save file '%s' from the '%s' folder into '%s'",
                    xmir.relative(),
                    this.xmirs,
                    this.output
                ),
                exception
            );
        } catch (final FileNotFoundException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't find the file '%s' in the '%s' folder",
                    xmir.relative(),
                    this.xmirs
                ),
                exception
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't save file '%s' from the '%s' folder to '%s'",
                    xmir.relative(),
                    this.xmirs,
                    this.output
                ),
                exception
            );
        }
    }

    /**
     * Read XMIR from the file.
     * @param path Path to the file
     * @return XMIR entry.
     */
    private XmirEntry entry(final Path path) {
        return new XmirEntry(path, this.xmirs.relativize(path).toString());
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
