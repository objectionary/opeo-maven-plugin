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
package org.eolang.opeo;

import com.jcabi.log.Logger;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eolang.opeo.decompilation.Decompiler;
import org.eolang.opeo.decompilation.agents.AllAgents;
import org.eolang.opeo.jeo.JeoDecompiler;
import org.eolang.opeo.storage.FileStorage;
import org.eolang.opeo.storage.Storage;
import org.eolang.opeo.storage.WithoutAliasesStorage;
import org.eolang.opeo.storage.XmirEntry;

/**
 * Selective decompiler.
 * Decompiler that decompiles ONLY fully understandable methods.
 * These methods contain only instructions that are
 * supported by {@link AllAgents}.
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

    /**
     * Constructor.
     * @param input Input folder with XMIRs.
     * @param output Output folder where to save the decompiled files.
     * @param modified Folder where to save the modified XMIRs.
     */
    public SelectiveDecompiler(final Path input, final Path output, final Path modified) {
        this(input, output, modified, new AllAgents().supportedOpcodes());
    }

    /**
     * Constructor.
     * @param input Input folder with XMIRs.
     * @param output Output folder where to save the decompiled files.
     * @param modified Folder where to save the modified XMIRs.
     * @param supported Supported opcodes are used in selection.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public SelectiveDecompiler(
        final Path input,
        final Path output,
        final Path modified,
        final String... supported
    ) {
        this(
            new WithoutAliasesStorage(new FileStorage(input, output)),
            new WithoutAliasesStorage(new FileStorage(modified, modified)),
            supported
        );
    }

    /**
     * Constructor.
     * @param storage Storage from which retrieve the XMIRs and where to save the modified ones.
     * @param modified Storage where to save the modified of each decompiled file.
     */
    public SelectiveDecompiler(final Storage storage, final Storage modified) {
        this(storage, modified, new AllAgents().supportedOpcodes());
    }

    /**
     * Constructor.
     * @param storage Storage from which retrieve the XMIRs and where to save the modified ones.
     * @param modified Storage where to save the modified of each decompiled file.
     * @param supported Supported opcodes are used in selection.
     */
    public SelectiveDecompiler(
        final Storage storage, final Storage modified, final String... supported
    ) {
        this.storage = storage;
        this.modified = modified;
        this.supported = supported.clone();
    }

    @Override
    public void decompile() {
        this.storage.all().parallel().forEach(
            entry -> {
                final XmirEntry res;
                final List<String> trycatches = entry.xpath(SelectiveDecompiler.trycatches());
                final Set<String> opcodes = this.unsupported(entry);
                if (opcodes.isEmpty() && trycatches.isEmpty()) {
                    res = entry.transform(
                        xml -> new JeoDecompiler(xml, entry.relative()).decompile()
                    );
                    this.modified.save(res);
                } else {
                    Logger.info(
                        this,
                        "Skipping %s, because of unsupported opcodes: %s, or try-catch blocks: %s",
                        entry,
                        opcodes,
                        trycatches
                    );
                    res = entry;
                }
                this.storage.save(res);
            }
        );
    }

    /**
     * Find all opcodes that are not supported.
     * @param entry XMIR entry.
     * @return Set of unsupported opcodes.
     */
    private Set<String> unsupported(final XmirEntry entry) {
        final Set<String> all = entry.xpath("//o[@base='opcode']/@name")
            .stream()
            .map(s -> String.format("%s%s", s, "-"))
            .map(s -> s.substring(0, s.indexOf('-')))
            .collect(Collectors.toSet());
        all.removeAll(Arrays.asList(this.supported));
        return all;
    }

    /**
     * Xpath to find all try-catch blocks.
     * @return Xpath.
     * @todo #284:90min Decompile try-catch blocks.
     *  Currently we skip decompilation of methods that contain try-catch blocks.
     *  We need to implement decompilation of try-catch blocks.
     *  Don't forget to add tests for the new functionality.
     */
    private static String trycatches() {
        return "//o[@base='tuple' and contains(@name,'trycatchblocks')]/@name";
    }
}
