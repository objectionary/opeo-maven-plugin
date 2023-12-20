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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.tools.ToolProvider;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jucs.ClasspathSource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Integration tests that verify that java code transforms into EO correctly.
 * The test logic is as follows:
 * 1. Compile java code into bytecode
 * 2. Transform bytecode into XMIR
 * 3. Compile expected EO into XMIR
 * 4. Compare XMIRs
 * @since 0.1
 */
final class TrasformationPacksTest {

    @ParameterizedTest
    @ClasspathSource(value = "packs", glob = "**.yaml")
    @EnabledIf(value = "hasJavaCompiler", disabledReason = "Java compiler is not available")
    void checksPack(final String pack, @TempDir Path where) throws IOException {
        //@checkstyle MethodBodyCommentsCheck (10 lines)
        // @todo #6:90min Implement primitive transformation test.
        //  Currently we just get the pack name from the test and check that it is not null.
        //  We have to implement proper transformation test. It will consist of the following steps:
        //  1. Compile java code into bytecode
        //  2. Transform bytecode into XMIR
        //  3. Compile expected EO into XMIR
        //  4. Compare XMIRs
        final List<JavaEoPack.Program> java = new JavaEoPack(pack).java();
        final List<Bytecode> compile = TrasformationPacksTest.compile(where, java);
        MatcherAssert.assertThat(
            "Pack is not null",
            pack,
            Matchers.notNullValue()
        );
    }

    /**
     * Compile random java class into bytecode.
     * @param where Where to compile.
     * @param classpath Source Code.
     * @return Bytecode.
     */
    private static List<Bytecode> compile(
        final Path where,
        List<JavaEoPack.Program> classpath
    ) throws IOException {
        final List<String> saved = new ArrayList<>(0);
        for (final JavaEoPack.Program program : classpath) {
            final Path path = where.resolve(program.name());
            saved.add(path.toString());
            Files.write(
                path,
                program.src().getBytes(StandardCharsets.UTF_8)
            );
        }
        ToolProvider.getSystemJavaCompiler().run(
            System.in,
            System.out,
            System.err,
            Stream.concat(
                Stream.of("-g:none", "-source", "11", "-target", "11"),
                saved.stream()
            ).toArray(String[]::new)
        );
        return Arrays.stream(where.toFile().listFiles())
            .map(File::toPath)
            .filter(p -> p.getFileName().toString().endsWith(".class"))
            .map(TrasformationPacksTest::readBytectode)
            .collect(Collectors.toList());
    }

    /**
     * Tries to read bytecode of a classfile.
     * @param classfile Classfile with '.class' extension.
     * @return Bytecode.
     */
    private static Bytecode readBytectode(final Path classfile) {
        try {
            return new Bytecode(Files.readAllBytes(classfile));
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't read classfile: %s", classfile),
                exception
            );
        }
    }


    /**
     * Check if java compiler is available.
     * Don't care about PMD warning: this method is used in @EnabledIf annotation.
     * @return True if java compiler is available.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static boolean hasJavaCompiler() {
        return ToolProvider.getSystemJavaCompiler() != null;
    }
}
