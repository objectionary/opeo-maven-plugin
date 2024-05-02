package org.eolang.opeo.decompilation;

public interface Decompiler {

    /**
     * Decompile EO to high-level EO.
     * EO represented by XMIR.
     */
    void decompile();
}
