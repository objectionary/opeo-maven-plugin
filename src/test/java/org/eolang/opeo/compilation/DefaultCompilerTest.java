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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.decompilation.NaiveDecompiler;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link DefaultCompiler}.
 * @since 0.1
 */
final class DefaultCompilerTest {

    @Test
    void compilesWithFailureSinceInputFolderIsNotFound(@TempDir final Path temp) {
        MatcherAssert.assertThat(
            "We expect to receive detailed error message",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new DefaultCompiler(temp).compile(),
                "The input folder should not be found"
            ).getMessage(),
            Matchers.containsString(
                String.format(
                    "The input XMIR folder '%s' doesn't exist",
                    temp.resolve("opeo-xmir")
                )
            )
        );
    }

    @Test
    void compilesSingleHighLevelXmir(@TempDir final Path temp) throws Exception {
        Opcode.disableCounting();
        final Path input = temp.resolve("opeo-xmir").resolve("Bar.xmir");
        Files.createDirectories(input.getParent());
        final byte[] before = new BytesOf(new ResourceOf("xmir/Bar.xmir")).asBytes();
        Files.write(input, before);
        new DefaultCompiler(temp).compile();
        final File output = temp.resolve("xmir").resolve("Bar.xmir").toFile();
        MatcherAssert.assertThat(
            "The compiled file is missing",
            output,
            FileMatchers.anExistingFile()
        );
        MatcherAssert.assertThat(
            "The compiled file is not equal to the original, but should. This is because Bar.xmir is already a low-level xmir",
            new BytesOf(output).asBytes(),
            Matchers.equalTo(before)
        );
    }

    @Test
    void compilesClassNameType(@TempDir final Path temp) throws Exception {
        final String name = "JsonMixinModule$JsonMixinComponentScanner.xmir";
        final Path input = temp.resolve("opeo-xmir").resolve(name);
        Files.createDirectories(input.getParent());
        Files.write(input, new BytesOf(new ResourceOf(String.format("xmir/%s", name))).asBytes());
        new DefaultCompiler(temp).compile();
        final Path expected = temp.resolve("xmir").resolve(name);
        MatcherAssert.assertThat(
            String.format(
                "The compiled file is missing, expected path: %s",
                expected
            ),
            expected.toFile(),
            FileMatchers.anExistingFile()
        );
    }
}
