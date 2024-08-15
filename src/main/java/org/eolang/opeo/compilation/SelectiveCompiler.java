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
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.eolang.opeo.decompilation.agents.AllAgents;
import org.eolang.opeo.storage.CompilationStorage;
import org.eolang.opeo.storage.Storage;
import org.eolang.opeo.storage.XmirEntry;

/**
 * Selective compiler.
 * Compiles only those sources that were previously decompiled.
 * All the rest are skipped and copied as is without any changes.
 * @since 0.2
 */
public final class SelectiveCompiler implements Compiler {

    /**
     * Storage.
     */
    private final Storage storage;

    /**
     * Supported opcodes.
     */
    private final String[] supported;

    /**
     * Constructor.
     * @param xmirs XMIRs to compile directory.
     * @param output Output directory
     */
    public SelectiveCompiler(final Path xmirs, final Path output) {
        this(new CompilationStorage(xmirs, output));
    }

    /**
     * Constructor.
     * @param storage Storage.
     */
    public SelectiveCompiler(final Storage storage) {
        this.storage = storage;
        this.supported = new AllAgents().supportedOpcodes();
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

    /**
     * Compile the entry.
     * @param entry Entry to compile.
     * @return One if compiled, zero otherwise.
     */
    private int compile(final XmirEntry entry) {
        final XmirEntry res;
        if (entry.xpath(this.unsupportedOpcodes()).isEmpty()
            || entry.xpath(SelectiveCompiler.trycatches())
            .isEmpty()) {
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
    private String unsupportedOpcodes() {
        return String.format(
            "//o[@base='opcode'][not(contains('%s', substring-before(concat(@name, '-'), '-'))) ]/@name",
            Arrays.stream(this.supported).collect(Collectors.joining(" "))
        );
    }

    /**
     * Xpath to find all try-catch blocks.
     * @return Xpath.
     */
    private static String trycatches() {
        return "//o[@base='tuple' and @name='trycatchblocks']/@name";
    }
}
