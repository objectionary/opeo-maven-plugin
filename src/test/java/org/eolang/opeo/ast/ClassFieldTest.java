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

import org.eolang.jeo.matchers.SameXml;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link ClassField}.
 * @since 0.2
 */
final class ClassFieldTest {

    /**
     * XMIR representation of the field.
     */
    private static final String XMIR = String.join(
        "\n",
        "<o base='static-field'>",
        "   <o base='string' data='bytes'>64 65 73 63 72 69 70 74 6F 72 3D 49 7C 6E 61 6D 65 3D 78 7C 6F 77 6E 65 72 3D 6A 61 76 61 2F 6C 61 6E 67 2F 41</o>",
        "</o>"
    );

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't convert class field to xmir representation",
            new Xembler(new ClassField("java/lang/A", "x", "I").toXmir()).xml(),
            new SameXml(ClassFieldTest.XMIR)
        );
    }

    @Test
    void convertsFromXmir() {
        MatcherAssert.assertThat(
            "Can't convert class field from xmir representation",
            new ClassField(new XmlNode(ClassFieldTest.XMIR)),
            Matchers.equalTo(
                new ClassField("java/lang/A", "x", "I")
            )
        );
    }
}
