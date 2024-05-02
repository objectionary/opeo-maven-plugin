package org.eolang.opeo.compilation;

import com.jcabi.log.Logger;
import java.nio.file.Path;
import org.eolang.opeo.storage.CompilationStorage;
import org.eolang.opeo.storage.Storage;
import org.eolang.opeo.storage.XmirEntry;

public final class StorageCompiler implements Compiler {

    private final Storage storage;

    /**
     * Constructor.
     * @param xmirs Path to the generated XMIRs by opeo-maven-plugin.
     * @param output Path to the output directory.
     */
    public StorageCompiler(final Path xmirs, final Path output) {
        this(new CompilationStorage(xmirs, output));
    }

    public StorageCompiler(final Storage storage) {
        this.storage = storage;
    }

    /**
     * Compile high-level EO constructs into XMIRs for the jeo-maven-plugin.
     */
    public void compile() {
        Logger.info(this, "Compiling EO sources from StorageCompiler");
        Logger.info(
            this,
            "Compiled %d sources",
            (long) this.storage.all()
                .stream()
                .mapToInt(this::compile)
                .sum()
        );
    }

    /**
     * Compile the file.
     * @param xmir Path to the file.
     */
    private int compile(final XmirEntry xmir) {
        Logger.info(this, "Compiling %s", xmir.pckg());
        this.storage.save(xmir.transform(xml -> new JeoCompiler(xml).compile()));
        return 1;
    }
}
