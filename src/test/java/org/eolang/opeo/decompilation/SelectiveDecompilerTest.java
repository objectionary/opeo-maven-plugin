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
package org.eolang.opeo.decompilation;

import java.util.stream.Collectors;
import org.cactoos.io.ResourceOf;
import org.eolang.opeo.SelectiveDecompiler;
import org.eolang.opeo.decompilation.agents.RouterAgent;
import org.eolang.opeo.storage.InMemoryStorage;
import org.eolang.opeo.storage.XmirEntry;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link org.eolang.opeo.SelectiveDecompiler}.
 * @since 0.1
 */
final class SelectiveDecompilerTest {

    /**
     * Xmir with known instructions.
     */
    private static final String KNOWN = "xmir/Known.xmir";

    /**
     * Xmir with unknown instructions.
     */
    private static final String UNKNOWN = "xmir/Bar.xmir";

    @Test
    void decompiles() {
        final XmirEntry known = new XmirEntry(
            new ResourceOf(SelectiveDecompilerTest.KNOWN),
            "pckg"
        );
        final InMemoryStorage storage = new InMemoryStorage();
        storage.save(known);
        final InMemoryStorage modified = new InMemoryStorage();
        new SelectiveDecompiler(storage, modified).decompile();
        MatcherAssert.assertThat(
            "We expect that the decompiled file won't be the same as the input file. Since the decompiler should change the file.",
            modified.last(),
            Matchers.not(Matchers.equalTo(known))
        );
    }

    @Test
    void decompilesNothing() {
        final XmirEntry known = new XmirEntry(new ResourceOf("xmir/Bar.xmir"), "pckg");
        final InMemoryStorage storage = new InMemoryStorage();
        storage.save(known);
        final InMemoryStorage modified = new InMemoryStorage();
        new SelectiveDecompiler(storage, modified).decompile();
        MatcherAssert.assertThat(
            "We expect that the decompiled file will be the same as the input file. Since the decompiler doesn't know some instructions.",
            storage.last(),
            Matchers.equalTo(known)
        );
        MatcherAssert.assertThat(
            "We expecte the 'modified' storage to be empty",
            modified.all().count(),
            Matchers.equalTo(0L)
        );
    }

    @Test
    void decompilesOnlySomeFiles() {
        final XmirEntry known = new XmirEntry(
            new ResourceOf(SelectiveDecompilerTest.KNOWN), "known"
        );
        final XmirEntry unknown = new XmirEntry(
            new ResourceOf(SelectiveDecompilerTest.UNKNOWN), "unknown"
        );
        final InMemoryStorage storage = new InMemoryStorage();
        storage.save(known);
        storage.save(unknown);
        final InMemoryStorage modified = new InMemoryStorage();
        new SelectiveDecompiler(storage, modified).decompile();
        MatcherAssert.assertThat(
            "We expect that the decompiled file will be changed since the decompiler knows all instructions.",
            modified.last(),
            Matchers.not(Matchers.equalTo(known))
        );
        MatcherAssert.assertThat(
            "We expect that the decompiled file won't be changed since the decompiler doesn't know some instructions.",
            storage.all()
                .filter(entry -> "unknown".equals(entry.relative()))
                .findFirst()
                .get(),
            Matchers.equalTo(unknown)
        );
    }

    @Test
    void copiesDecompiledFiles() {
        final XmirEntry known = new XmirEntry(
            new ResourceOf(SelectiveDecompilerTest.KNOWN), "known"
        );
        final InMemoryStorage storage = new InMemoryStorage();
        storage.save(known);
        final InMemoryStorage modified = new InMemoryStorage();
        new SelectiveDecompiler(storage, modified).decompile();
        MatcherAssert.assertThat(
            "We expect that the decompiled file will be stored in the output folder, but it should be modified",
            storage.last(),
            Matchers.not(Matchers.equalTo(known))
        );
        MatcherAssert.assertThat(
            "We expect that the decompiled file will be stored in the copy folder, but it should be modified",
            modified.last(),
            Matchers.not(Matchers.equalTo(known))
        );
        MatcherAssert.assertThat(
            "We expect that the decompiled file will be the same in the output and copy folders.",
            storage.last(),
            Matchers.equalTo(modified.last())
        );
    }

    @Test
    void avoidsDecompileLargeFileWithUnknownDependencies() {
        final InMemoryStorage storage = new InMemoryStorage();
        final InMemoryStorage modified = new InMemoryStorage();
        storage.save(
            new XmirEntry(
                new ResourceOf("xmir/disassembled/ArrayBuilders$ByteBuilder.xmir"),
                "com.fasterxml.jackson.databind.util"
            )
        );
        new SelectiveDecompiler(storage, modified).decompile();
        MatcherAssert.assertThat(
            "We expect that the selective decompiler will skip this file because it doesn't understand NEWARRAY instruction",
            modified.all().collect(Collectors.toList()),
            Matchers.empty()
        );
    }

    @Test
    void identifiesUnsupportedOpcodes() {
        MatcherAssert.assertThat(
            "We expect that the supported opcodes won't contain the 'GOTO' opcode since we don't support it yet.",
            new RouterAgent(false).supportedOpcodes(),
            Matchers.not(Matchers.arrayContaining("GOTO"))
        );
    }
}
