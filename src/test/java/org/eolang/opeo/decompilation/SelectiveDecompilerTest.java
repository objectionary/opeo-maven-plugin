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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.eolang.opeo.SelectiveDecompiler;
import org.eolang.opeo.storage.FileStorage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test cases for {@link org.eolang.opeo.SelectiveDecompiler}.
 * @since 0.1
 */
final class SelectiveDecompilerTest {

    @Test
    void decompiles(@TempDir final Path temp) throws Exception {
        final byte[] known = new BytesOf(new ResourceOf("xmir/Bar.xmir")).asBytes();
        final Path input = temp.resolve("input");
        final Path output = temp.resolve("output");
        Files.createDirectories(input);
        Files.createDirectories(output);
        Files.write(input.resolve("Bar.xmir"), known);
        new SelectiveDecompiler(new FileStorage(input, output)).decompile();
        MatcherAssert.assertThat(
            "We expect that decompiler will decompile the input file and store the result into the output folder.",
            output.resolve("Bar.xmir").toFile(),
            FileMatchers.anExistingFile()
        );
        MatcherAssert.assertThat(
            "We expect that the decompiled file won't be the same as the input file. Since the decompiler should change the file.",
            new BytesOf(output.resolve("Bar.xmir")),
            Matchers.not(Matchers.equalTo(new BytesOf(known)))
        );
    }

    @Test
    void decompilesNothing(@TempDir final Path temp) throws Exception {
        final byte[] known = new BytesOf(new ResourceOf("xmir/Bar.xmir")).asBytes();
        final Path input = temp.resolve("input");
        final Path output = temp.resolve("output");
        Files.createDirectories(input);
        Files.createDirectories(output);
        Files.write(input.resolve("Bar.xmir"), known);
        new SelectiveDecompiler(new FileStorage(input, output)).decompile();
        MatcherAssert.assertThat(
            "We expect that decompiler will copy the input file and store the result into the output folder.",
            output.resolve("Bar.xmir").toFile(),
            FileMatchers.anExistingFile()
        );
        MatcherAssert.assertThat(
            "We expect that the decompiled file will be the same as the input file. Since the decompiler doesn't know some instructions.",
            new BytesOf(output.resolve("Bar.xmir")),
            Matchers.equalTo(new BytesOf(known))
        );
    }

    @Test
    void decompilesOnlySomeFiles(@TempDir final Path temp) throws Exception {
        final byte[] known = new BytesOf(new ResourceOf("xmir/Bar.xmir")).asBytes();
        final byte[] unknown = new BytesOf(new ResourceOf("xmir/Bar.xmir")).asBytes();
        final Path input = temp.resolve("input");
        final Path output = temp.resolve("output");
        Files.createDirectories(input);
        Files.createDirectories(output);
        Files.write(input.resolve("Known.xmir"), known);
        Files.write(input.resolve("Unknown.xmir"), known);
        new SelectiveDecompiler(new FileStorage(input, output)).decompile();
        MatcherAssert.assertThat(
            "We expect that the decompiled file will be changed since the decompiler knows all instructions.",
            new BytesOf(output.resolve("Known.xmir")),
            Matchers.not(Matchers.equalTo(new BytesOf(known)))
        );
        MatcherAssert.assertThat(
            "We expect that the decompiled file won't be changed since the decompiler doesn't know some instructions.",
            new BytesOf(output.resolve("Unknown.xmir")),
            Matchers.equalTo(new BytesOf(unknown))
        );
    }

}
