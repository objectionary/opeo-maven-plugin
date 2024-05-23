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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link NaiveDecompiler}.
 * @since 0.1
 */
final class NaiveDecompilerTest {

    @Test
    void decompilesSeveralFiles(@TempDir final Path temp) throws Exception {
        final String name = "Bar.xmir";
        final Path subpath = Paths.get("org").resolve("eolang").resolve("jeo");
        final Path input = temp.resolve("xmir").resolve(subpath).resolve(name);
        Files.createDirectories(input.getParent());
        Files.write(input, new BytesOf(new ResourceOf("xmir/Bar.xmir")).asBytes());
        new NaiveDecompiler(temp).decompile();
        final Path expected = temp.resolve("opeo-xmir").resolve(subpath).resolve(name);
        MatcherAssert.assertThat(
            String.format(
                "The decompiled file is missing, expected path: %s",
                expected
            ),
            expected.toFile(),
            FileMatchers.anExistingFile()
        );
    }

    @Test
    void decompilesArrayStore(@TempDir final Path temp) throws Exception {
        final String name = "BeanMethod$NonOverridableMethodError.xmir";
        final Path input = temp.resolve("xmir").resolve(name);
        Files.createDirectories(input.getParent());
        Files.write(input, new BytesOf(new ResourceOf(String.format("xmir/jeo/%s", name))).asBytes());
        new NaiveDecompiler(temp).decompile();
        final Path expected = temp.resolve("opeo-xmir").resolve(name);
        MatcherAssert.assertThat(
            String.format(
                "The decompiled file is missing, expected path: %s",
                expected
            ),
            expected.toFile(),
            FileMatchers.anExistingFile()
        );
    }

}
