package org.eolang.opeo;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.eolang.opeo.ast.OpcodeName;
import org.eolang.opeo.decompilation.Decompiler;
import org.eolang.opeo.decompilation.handlers.RouterHandler;
import org.eolang.opeo.jeo.JeoDecompiler;
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
     * Where to save the copy of each decompiled file.
     */
    private final Storage copy;


    private final String[] supported;

    public SelectiveDecompiler(final Storage storage) {
        this(storage, new RouterHandler(true).supportedOpcodes());
    }

    public SelectiveDecompiler(final Storage storage, String... supported) {
        this(storage, storage, supported);
    }

    public SelectiveDecompiler(
        final Storage storage, final Storage copy, final String... supported
    ) {
        this.storage = storage;
        this.copy = copy;
        this.supported = supported;
    }

    @Override
    public void decompile() {
        this.storage.all().forEach(
            entry -> {
                final XmirEntry res;
                final List<String> xpath = entry.xpath(this.xpath());
                final boolean empty = xpath.isEmpty();
                if (empty) {
                    res = entry.transform(xml -> new JeoDecompiler(xml).decompile());
                } else {
                    res = entry;
                }
                this.copy.save(res);
                this.storage.save(res);
            }
        );
    }

    private String xpath() {
        return String.format(
            "//o[@base='opcode'][not(contains(' %s ', concat(' ', @name, ' '))) ]/@name",
            Arrays.stream(this.supported).collect(Collectors.joining(" "))
        );
    }
}
