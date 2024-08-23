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
package org.eolang.opeo.storage;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Lower Opcodes Storage Test.
 * Test cases for {@link LowerOpcodesStorage}.
 * @since 0.4
 */
final class LowerOpcodesStorageTest {

    @Test
    void transformsAllOpcodeNamesToLowerCase() {
        final LowerOpcodesStorage storage = new LowerOpcodesStorage(new InMemoryStorage());
        final XML opcodes = this.program("LDC", "LOAD", "ADD", "SUB");
        storage.save(new XmirEntry(opcodes, "test"));
        MatcherAssert.assertThat(
            String.format(
                "We expect that all opcode names are transformed to lowercase from the XMIR %s",
                opcodes
            ),
            storage.all()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No XMIR found"))
                .toXml(),
            XhtmlMatchers.hasXPaths(
                ".//o[@name='ldc']",
                ".//o[@name='load']",
                ".//o[@name='add']",
                ".//o[@name='sub']"
            )
        );
    }

    /**
     * Test xmir program with objects.
     * @param names Object names.
     * @return Program as XMIR.
     */
    private XML program(final String... names) {
        return new XMLDocument(
            new Xembler(
                new Directives()
                    .add("program").add("objects")
                    .append(
                        Arrays.stream(names)
                            .map(this::opcode)
                            .reduce(new Directives(), Directives::append)
                    ).up().up()
            ).xmlQuietly()
        );
    }

    /**
     * Opcode directive.
     * @param name Opcode name.
     * @return Directives.
     */
    private Directives opcode(final String name) {
        return new Directives()
            .add("o")
            .attr("base", "opcode")
            .attr("name", name)
            .up();
    }
}
