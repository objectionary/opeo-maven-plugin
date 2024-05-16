package org.eolang.opeo;


import org.eolang.opeo.decompilation.Decompiler;
import org.eolang.opeo.storage.Storage;

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

    public SelectiveDecompiler(final Storage storage) {
        this.storage = storage;
    }

    @Override
    public void decompile() {

    }
}
