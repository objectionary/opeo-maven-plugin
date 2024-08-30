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

import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.matchers.SameXml;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link Labeled}.
 * @since 0.2
 */
final class LabeledTest {

    /**
     * Example of XMIR representation of labeled constant.
     */
    private static final String XMIR =
        "<o base='labeled'><o base='int' data='bytes'>00 00 00 00 00 00 00 01</o><o base='label' data='bytes'>01</o></o>";

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Wrong XMIR is generated for labeled constant",
            new Xembler(
                new Labeled(new Const(1), new Label("1")).toXmir()
            ).xml(),
            new SameXml(new XMLDocument(LabeledTest.XMIR))
        );
    }

    @Test
    void convertsFromXmir() {
        MatcherAssert.assertThat(
            "Wrong labeled constant were generated from XMIR",
            new Labeled(new XmlNode(LabeledTest.XMIR), node -> new Const(1)),
            Matchers.equalTo(
                new Labeled(new Const(1), new Label("1"))
            )
        );
    }

    @Test
    void convertsToOpcodes() {
        final Label label = new Label("1");
        MatcherAssert.assertThat(
            "Wrong opcodes are generated for labeled constant",
            new Labeled(new Const(1), label).opcodes(),
            Matchers.contains(
                new Opcode(Opcodes.ICONST_1),
                label
            )
        );
    }

}
