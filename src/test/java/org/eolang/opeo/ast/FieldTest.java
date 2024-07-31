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
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.jeo.matchers.SameXml;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link Field}.
 * @since 0.2
 */
final class FieldTest {

    /**
     * XMIR representation of the field.
     */
    private static final String XMIR = String.join(
        "\n",
        "<o base='.foo'>",
        "      <o base='string' data='bytes'>6E 61 6D 65 3D 66 6F 6F</o>",
        "   <o base='$'>",
        "      <o base='string' data='bytes'>64 65 73 63 72 69 70 74 6F 72 3D 6A 61 76 61 2E 6C 61 6E 67 2E 4F 62 6A 65 63 74</o>",
        "   </o>",
        "</o>"
    );

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        final String actual = new Xembler(
            new Field(
                new This(),
                new Attributes("name", "foo")
            ).toXmir()
        ).xml();
        MatcherAssert.assertThat(
            String.format(
                "Can't convert to correct XMIR, actual result is : %n%s%n",
                new XMLDocument(actual)
            ),
            actual,
            new SameXml(FieldTest.XMIR)
        );
    }

    @Test
    void createsFieldFromXmir() {
        MatcherAssert.assertThat(
            "Can't convert Field from XMIR",
            new Field(new XmlNode(FieldTest.XMIR), node -> new This(Type.getType(Object.class))),
            Matchers.equalTo(new Field(new This(), new Attributes("name", "foo")))
        );
    }

}
