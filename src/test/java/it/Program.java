package it;

import java.util.Map;

/**
 * Program.
 * @since 0.1
 */
@SuppressWarnings("JTCOP.RuleCorrectTestName")
final class Program {

    /**
     * Filename.
     */
    private final String filename;

    /**
     * Source.
     */
    private final String source;

    /**
     * Constructor.
     * @param entry Entry.
     */
    Program(final Map.Entry<String, String> entry) {
        this(entry.getKey(), entry.getValue());
    }

    /**
     * Constructor.
     * @param filename Filename.
     * @param source Source.
     */
    Program(final String filename, final String source) {
        this.filename = filename;
        this.source = source;
    }

    /**
     * Program filename.
     * @return Filename.
     */
    String name() {
        return this.filename;
    }

    /**
     * Program source.
     * @return Source code.
     */
    String src() {
        return this.source;
    }
}
