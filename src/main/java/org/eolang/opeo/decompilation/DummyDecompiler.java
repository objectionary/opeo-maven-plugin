package org.eolang.opeo.decompilation;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public final class DummyDecompiler {

    private final Path xmirs;

    private final Path output;

    public DummyDecompiler(final Path xmirs, final Path output) {
        this.xmirs = xmirs;
        this.output = output;
    }

    public void decompile() {
        try (Stream<Path> files = Files.walk(this.xmirs).filter(Files::isRegularFile)) {
            Logger.info(this, "Decompiling EO sources from %[file]s", this.xmirs);
            Logger.info(this, "Saving new decompiled EO sources to %[file]s", this.output);
            Logger.info(
                this,
                "Decompiled %d EO sources",
                files.filter(DummyDecompiler::isXmir)
                    .peek(this::decompile)
                    .count()
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't decompile files from '%s'", this.xmirs),
                exception
            );
        } catch (final IllegalArgumentException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't decompile files from '%s' directory and save them into '%s', current directory is '%s'",
                    this.xmirs,
                    this.output,
                    Paths.get("").toAbsolutePath()
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

    /**
     * Decompile XMIR to high-level EO.
     * @param path Path to the XMIR file.
     */
    private void decompile(final Path path) {
        try {
//            final XML decompiled = new XMLDocument(path);
            final Path out = this.output.resolve(this.xmirs.relativize(path));
            Files.createDirectories(out.getParent());
            Files.copy(path, out);
//            Files.write(
//                out,
//                decompiled.toString().getBytes(StandardCharsets.UTF_8)
//            );
            Logger.info(this, "Decompiled %[file]s (%[size]s)", out, Files.size(out));
        } catch (final IllegalArgumentException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't decompile file '%s' in the '%s' folder and save it into '%s'",
                    path,
                    this.xmirs,
                    this.output
                ),
                exception
            );
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

}
