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
package org.eolang.opeo.compilation;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Compiler of high-level eo constructs into XMIRs for the jeo-maven-plugin.
 * @since 0.1
 */
public class Compiler {

    /**
     * Path to the generated XMIRs by opeo-maven-plugin.
     * In other words, it is the folder of the high-level EO constructs that were decompiled
     * on the previous step.
     */
    private final Path xmirs;

    /**
     * Path to the output directory.
     * The output folder with XMIRs accepted by jeo-maven-plugin.
     */
    private final Path output;

    /**
     * Constructor.
     * @param generated The default Maven 'generated-sources' directory.
     */
    public Compiler(final File generated) {
        this(generated.toPath());
    }

    /**
     * Constructor.
     * @param generated The default Maven 'generated-sources' directory.
     */
    public Compiler(final Path generated) {
        this(generated.resolve("opeo-xmir"), generated.resolve("xmir"));
    }

    /**
     * Constructor.
     * @param xmirs Path to the generated XMIRs by opeo-maven-plugin.
     * @param output Path to the output directory.
     */
    public Compiler(final Path xmirs, final Path output) {
        this.xmirs = xmirs;
        this.output = output;
    }

    /**
     * Compile high-level EO constructs into XMIRs for the jeo-maven-plugin.
     */
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
        try (Stream<Path> decompiled = Files.walk(this.xmirs).filter(Compiler::isXmir)) {
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
            final XML compiled = new JeoCompiler(new XMLDocument(xmir)).compile();
            final Path out = this.output.resolve(this.xmirs.relativize(xmir));
            Files.createDirectories(out.getParent());
            Files.write(
                out,
                compiled.toString().getBytes(StandardCharsets.UTF_8)
            );
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
