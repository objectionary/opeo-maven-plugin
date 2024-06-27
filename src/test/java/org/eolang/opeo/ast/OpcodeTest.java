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
package org.eolang.opeo.ast;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test case for {@link Opcode}.
 * @since 0.1
 */
final class OpcodeTest {

    /**
     * XMIR representation of the opcode.
     */
    private static final String XMIR = String.join(
        "\n",
        "<o base='opcode' line='999' name='LDC'>",
        "<o base='int' data='bytes'>00 00 00 00 00 00 00 12</o>",
        "<o base='string' data='bytes'>68 65 6C 6C 6F</o>",
        "</o>"
    );

    @Test
    void createsFromXmir() {
        MatcherAssert.assertThat(
            "The LDC opcode should be created from XMIR",
            new Opcode(new XmlNode(OpcodeTest.XMIR)),
            Matchers.equalTo(
                new Opcode(Opcodes.LDC, "hello")
            )
        );
    }

    @Test
    void transformsToXml() {
        Opcode.disableCounting();
        MatcherAssert.assertThat(
            String.format("We expect the following XML to be generated: %s", OpcodeTest.XMIR),
            new XMLDocument(
                new Xembler(new Opcode(Opcodes.LDC, "hello").toXmir()).xmlQuietly()
            ),
            Matchers.equalTo(new XMLDocument(OpcodeTest.XMIR))
        );
    }
}
