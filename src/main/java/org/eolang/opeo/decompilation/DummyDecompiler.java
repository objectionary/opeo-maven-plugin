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

import java.nio.file.Path;
import org.eolang.opeo.storage.DecompilationStorage;
import org.eolang.opeo.storage.Storage;

/**
 * Dummy decompiler.
 * It just copies XMIR files to the output directory without any changes.
 * @since 0.2
 */
public final class DummyDecompiler implements Decompiler {

    /**
     * The storage where the XMIRs are stored.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param xmirs Path to the generated XMIRs by jeo-maven-plugin.
     * @param output Path to the output directory.
     */
    public DummyDecompiler(final Path xmirs, final Path output) {
        this(new DecompilationStorage(xmirs, output));
    }

    /**
     * Constructor.
     * @param storage The storage where the XMIRs are stored.
     */
    public DummyDecompiler(final Storage storage) {
        this.storage = storage;
    }

    @Override
    public void decompile() {
        this.storage.all().forEach(this.storage::save);
//        try (Stream<Path> files = Files.walk(this.xmirs).filter(Files::isRegularFile)) {
//            Logger.info(this, "Decompiling EO sources from %[file]s", this.xmirs);
//            Logger.info(this, "Saving new decompiled EO sources to %[file]s", this.output);
//            Logger.info(
//                this,
//                "Decompiled %d EO sources",
//                files.filter(DummyDecompiler::isXmir)
//                    .peek(this::decompile)
//                    .count()
//            );
//        } catch (final IOException exception) {
//            throw new IllegalStateException(
//                String.format("Can't decompile files from '%s'", this.xmirs),
//                exception
//            );
//        } catch (final IllegalArgumentException exception) {
//            throw new IllegalStateException(
//                String.format(
//                    "Can't decompile files from '%s' directory and save them into '%s', current directory is '%s'",
//                    this.xmirs,
//                    this.output,
//                    Paths.get("").toAbsolutePath()
//                ),
//                exception
//            );
//        }
    }


    /**
     * Check if the file is XMIR.
     * @param path Path to the file.
     * @return True if the file is XMIR.
     */
    private static boolean isXmir(final Path path) {
        return path.toString().endsWith(".xmir");
    }

//    /**
//     * Decompile XMIR to high-level EO.
//     * @param path Path to the XMIR file.
//     */
//    private void decompile(final Path path) {
//        try {
////            final XML decompiled = new XMLDocument(path);
//            final Path out = this.output.resolve(this.xmirs.relativize(path));
//            Files.createDirectories(out.getParent());
//            Files.copy(path, out);
////            Files.write(
////                out,
////                decompiled.toString().getBytes(StandardCharsets.UTF_8)
////            );
//            Logger.info(this, "Decompiled %[file]s (%[size]s)", out, Files.size(out));
//        } catch (final IllegalArgumentException exception) {
//            throw new IllegalStateException(
//                String.format(
//                    "Can't decompile file '%s' in the '%s' folder and save it into '%s'",
//                    path,
//                    this.xmirs,
//                    this.output
//                ),
//                exception
//            );
//        } catch (final FileNotFoundException exception) {
//            throw new IllegalStateException(
//                String.format(
//                    "Can't find the file '%s' for decompilation in the '%s' folder",
//                    path,
//                    this.xmirs
//                ),
//                exception
//            );
//        } catch (final IOException exception) {
//            throw new IllegalStateException(
//                String.format(
//                    "Can't decompile file '%s' in the '%s' folder",
//                    path,
//                    this.xmirs
//                ),
//                exception
//            );
//        }
//    }

}
