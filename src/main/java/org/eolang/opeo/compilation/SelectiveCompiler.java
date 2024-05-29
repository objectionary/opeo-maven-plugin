package org.eolang.opeo.compilation;

import com.jcabi.log.Logger;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.eolang.opeo.decompilation.handlers.RouterHandler;
import org.eolang.opeo.storage.CompilationStorage;
import org.eolang.opeo.storage.Storage;
import org.eolang.opeo.storage.XmirEntry;

public final class SelectiveCompiler implements Compiler {

    private final Storage storage;
    private final String[] supported;

    public SelectiveCompiler(final Path xmirs, final Path output) {
        this(new CompilationStorage(xmirs, output));
    }

    public SelectiveCompiler(final Storage storage) {
        this.storage = storage;
        this.supported = new RouterHandler(false).supportedOpcodes();
    }

    @Override
    public void compile() {
        Logger.info(
            this,
            "Compiled %d sources",
            this.storage.all()
                .parallel()
                .mapToInt(this::compile)
                .sum()
        );
    }

    private int compile(final XmirEntry entry) {
        final XmirEntry res;
        if (entry.xpath(this.xpath()).isEmpty()) {
            res = entry.transform(xml -> new JeoCompiler(xml).compile());
        } else {
            Logger.info(
                this,
                "Skipping %s, because it wasn't previously compiled",
                entry
            );
            res = entry;
        }
        this.storage.save(res);
        return 1;
    }

    /**
     * Xpath to find all opcodes that are not supported.
     * @return Xpath.
     */
    private String xpath() {
        return String.format(
            "//o[@base='opcode'][not(contains('%s', substring-before(concat(@name, '-'), '-'))) ]/@name",
            Arrays.stream(this.supported).collect(Collectors.joining(" "))
        );
    }
}
