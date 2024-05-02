package org.eolang.opeo.compilation;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public final class DummyCompiler implements Compiler{

    private final Path xmirs;

    private final Path output;

    public DummyCompiler(final Path xmirs, final Path output) {
        this.xmirs = xmirs;
        this.output = output;
    }


    @Override
    public void compile() {
        if (!Files.exists(this.xmirs)) {
            throw new IllegalArgumentException(
                String.format(
                    "The input xmirs folder '%s' doesn't exist",
                    this.xmirs
                )
            );
        }
        Logger.info(this, "Compiling EO sources from %[file]s", this.xmirs);
        Logger.info(this, "Saving new compiled EO sources to %[file]s", this.output);
        try (Stream<Path> decompiled = Files.walk(this.xmirs).filter(DummyCompiler::isXmir)) {
            Logger.info(this, "Compiled %d sources", decompiled.peek(this::compile).count());
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format(
                    "Some problem with reading XMIRs from the '%s' folder",
                    this.xmirs
                ),
                exception
            );
        }
    }

    /**
     * Compile the file.
     * @param xmir Path to the file.
     */
    private void compile(final Path xmir) {
        try {
//            final XML compiled = new XMLDocument(xmir);
            final Path out = this.output.resolve(this.xmirs.relativize(xmir));
            Files.createDirectories(out.getParent());
            Files.copy(xmir, out);
//            Files.write(
//                out,
//                compiled.toString().getBytes(StandardCharsets.UTF_8)
//            );
            Logger.info(this, "Compiled %[file]s (%[size]s)", out, Files.size(out));
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't compile '%x'", xmir),
                exception
            );
        }
    }

    /**
     * Check if the file is XMIR.
     * @param path Path to the file
     * @return True if the file is XMIR
     */
    private static boolean isXmir(final Path path) {
        return Files.isRegularFile(path) && path.toString().endsWith(".xmir");
    }
}
