package org.eolang.opeo.storage;

import com.jcabi.log.Logger;
import com.jcabi.xml.XMLDocument;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DecompilationStorage implements Storage {

    /**
     * Path to the generated XMIRs by jeo-maven-plugin.
     */
    private final Path xmirs;

    /**
     * Path to the output directory.
     */
    private final Path output;

    public DecompilationStorage(
        final Path xmirs,
        final Path output
    ) {
        this.xmirs = xmirs;
        this.output = output;
    }

    @Override
    public Collection<XmirEntry> all() {
        Logger.info(this, "Decompiling EO sources from %[file]s", this.xmirs);
        Logger.info(this, "Saving new decompiled EO sources to %[file]s", this.output);
        try (Stream<Path> files = Files.walk(this.xmirs).filter(Files::isRegularFile)) {
            return files.filter(DecompilationStorage::isXmir)
                .map(this::read)
                .collect(Collectors.toList());
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

    @Override
    public void save(final XmirEntry xml) {
        final Path out = this.output.resolve(Path.of(xml.pckg()));
        try {
            Files.createDirectories(out.getParent());
            Files.write(
                out,
                xml.xml().toString().getBytes(StandardCharsets.UTF_8)
            );
            Logger.info(
                this,
                "Decompiled %[file]s (%[size]s)",
                this.output,
                Files.size(this.output)
            );
        } catch (final IllegalArgumentException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't decompile file '%s' in the '%s' folder and save it into '%s'",
                    xml.pckg(),
                    this.xmirs,
                    this.output
                ),
                exception
            );
        } catch (final FileNotFoundException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't find the file '%s' for decompilation in the '%s' folder",
                    xml.pckg(),
                    this.xmirs
                ),
                exception
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't decompile file '%s' in the '%s' folder",
                    xml.pckg(),
                    this.xmirs
                ),
                exception
            );
        }
    }


    private XmirEntry read(final Path path) {
        try {
            return new XmirEntry(new XMLDocument(path), this.xmirs.relativize(path).toString());
        } catch (final FileNotFoundException exception) {
            throw new IllegalStateException(
                String.format("Can't decompile '%x'", path),
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
