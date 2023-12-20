/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package it;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.yaml.snakeyaml.Yaml;

/**
 * Test pack that can parse java input and expected eo output.
 * @since 0.1
 */
@SuppressWarnings("JTCOP.RuleCorrectTestName")
final class JavaEoPack {

    /**
     * Raw YAML.
     */
    private final String pack;

    /**
     * Constructor.
     * @param yaml Raw YAML.
     */
    JavaEoPack(final String yaml) {
        this.pack = yaml;
    }

    /**
     * Java programs.
     * @return List of programs.
     */
    List<Program> java() {
        return this.parse().get("java")
            .entrySet().stream()
            .map(Program::new)
            .collect(Collectors.toList());
    }

    /**
     * EO programs.
     * @return List of programs.
     */
    List<Program> eolang() {
        return this.parse().get("eo")
            .entrySet()
            .stream().map(Program::new)
            .collect(Collectors.toList());
    }

    /**
     * Parse YAML.
     * @return Map of programs.
     */
    private Map<String, Map<String, String>> parse() {
        return new Yaml().load(this.pack);
    }

    /**
     * Program.
     * @since 0.1
     */
    static class Program {

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
}
