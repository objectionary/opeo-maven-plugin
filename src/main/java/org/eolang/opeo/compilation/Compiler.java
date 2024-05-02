package org.eolang.opeo.compilation;

public interface Compiler {

    /**
     * Compile high-level EO constructs into XMIRs for the jeo-maven-plugin.
     */

    void compile();
}
