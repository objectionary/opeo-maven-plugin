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

import org.eolang.jeo.representation.xmir.XmlNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link NewAddress}.
 * @since 0.2
 */
final class NewAddressTest {

    /**
     * XML representation of new address.
     */
    private static final String XML = String.join(
        "\n",
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
        "<o base=\".new-type\">",
        "   <o base=\"string\" data=\"bytes\">53 6F 6D 65 54 79 70 65</o>",
        "</o>",
        ""
    );

    /**
     * Type of new address.
     */
    private static final String TYPE = "SomeType";

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect, that new address will be successfully converted to XMIR",
            new Xembler(new NewAddress(NewAddressTest.TYPE).toXmir()).xml(),
            Matchers.equalTo(NewAddressTest.XML)
        );
    }

    @Test
    void createsFromXmir() {
        MatcherAssert.assertThat(
            "We expect, that new address will be successfully created from XMIR",
            new NewAddress(new XmlNode(NewAddressTest.XML)),
            Matchers.equalTo(new NewAddress(NewAddressTest.TYPE))
        );
    }

    @Test
    void convertsToOpcodes() {
        MatcherAssert.assertThat(
            "We expect, that new address will be successfully converted to opcodes",
            new NewAddress(NewAddressTest.TYPE).opcodes(),
            Matchers.contains(
                new Opcode(Opcodes.NEW, NewAddressTest.TYPE)
            )
        );
    }
}
