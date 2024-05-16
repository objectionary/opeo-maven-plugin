package org.eolang.opeo;


import com.jcabi.log.Logger;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.eolang.opeo.decompilation.Decompiler;
import org.eolang.opeo.decompilation.handlers.RouterHandler;
import org.eolang.opeo.jeo.JeoDecompiler;
import org.eolang.opeo.storage.DummyStorage;
import org.eolang.opeo.storage.FileStorage;
import org.eolang.opeo.storage.Storage;
import org.eolang.opeo.storage.XmirEntry;

/**
 * Selective decompiler.
 * Decompiler that decompiles ONLY fully understandable methods.
 * These methods contain only instructions that are
 * supported by {@link org.eolang.opeo.decompilation.handlers.RouterHandler}.
 *
 * @since 0.1
 */
public final class SelectiveDecompiler implements Decompiler {

    /**
     * The storage where the XMIRs are stored.
     */
    private final Storage storage;

    /**
     * Where to save the modified of each decompiled file.
     */
    private final Storage modified;

    /**
     * Supported opcodes.
     */
    private final String[] supported;

    public SelectiveDecompiler(final Path input, final Path output, final Path modified) {
        this(input, output, modified, new RouterHandler(false).supportedOpcodes());
    }

    public SelectiveDecompiler(final Path input, final Path output) {
        this(input, output, new RouterHandler(false).supportedOpcodes());
    }

    public SelectiveDecompiler(
        final Path input, final Path output, final Path modified, String... supported
    ) {
        this(new FileStorage(input, output), new FileStorage(modified, modified), supported);
    }

    public SelectiveDecompiler(
        final Path input, final Path output, String... supported
    ) {
        this(new FileStorage(input, output), supported);
    }

    public SelectiveDecompiler(final Storage storage, String... supported) {
        this(storage, new DummyStorage(), supported);
    }

    public SelectiveDecompiler(
        final Storage storage, final Storage modified, final String... supported
    ) {
        this.storage = storage;
        this.modified = modified;
        this.supported = supported;
    }

    @Override
    public void decompile() {
        this.storage.all().forEach(
            entry -> {
                final XmirEntry res;
                final List<String> xpath = entry.xpath(this.xpath());
                if (xpath.isEmpty()) {
                    res = entry.transform(xml -> new JeoDecompiler(xml).decompile());
                    this.modified.save(res);
                } else {
                    Logger.info(
                        this,
                        "Skipping %s, because of unsupported opcodes: %s",
                        entry,
                        xpath
                    );
                    res = entry;
                }
                this.storage.save(res);
            }
        );
    }

    private String xpath() {
        return String.format(
            "//o[@base='opcode'][not(contains('%s', substring-before(concat(@name, '-'), '-'))) ]/@name",
            Arrays.stream(this.supported).collect(Collectors.joining(" "))
        );
    }
}
