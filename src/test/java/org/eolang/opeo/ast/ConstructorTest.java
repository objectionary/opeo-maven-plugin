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
import org.eolang.opeo.SameXml;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test case for {@link Constructor}.
 * @since 0.1
 */
final class ConstructorTest {

    /**
     * Constructor XMIR representation.
     */
    private static final String CONSTRUCTOR = String.join(
        "\n",
        "<o base='.new'>",
        "  <o base='string' data='bytes'>64 65 73 63 72 69 70 74 6F 72 3D 28 4C 6A 61 76 61 2F 6C 61 6E 67 2F 53 74 72 69 6E 67 3B 4C 6A 61 76 61 2F 6C 61 6E 67 2F 53 74 72 69 6E 67 3B 49 29 56 7C 69 6E 74 65 72 66 61 63 65 64 3D 66 61 6C 73 65 0A</o>",
        "  <o base='.new-type'><o base='string' data='bytes'>41</o></o>",
        "  <o base='string' data='bytes'>66 69 72 73 74</o>",
        "  <o base='string' data='bytes'>73 65 63 6F 6E 64</o>",
        "  <o base='int' data='bytes'>00 00 00 00 00 00 00 03</o>",
        "</o>"
    );

    @Test
    void transformsIntoXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            String.format(
                "We expect the following XML to be generated:%n%s%n",
                ConstructorTest.CONSTRUCTOR
            ),
            new Xembler(
                new Constructor(
                    "A",
                    new Literal("first"),
                    new Literal("second"),
                    new Literal(3)
                ).toXmir(),
                new Transformers.Node()
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "/o[@base='.new']",
                "/o[@base='.new']/o[@base='.new-type']",
                "/o[@base='.new']/o[@base='.new-type']/o[@base='string' and @data='bytes' and text()='41']",
                "/o[@base='.new']/o[@base='int' and @data='bytes']"
            )
        );
    }

    @Test
    void transformsConstructorToXmirWithAttributes() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that constructor will be transformed to XMIR with scope attribute",
            new Xembler(
                new Constructor(
                    "A",
                    new Attributes().descriptor("(Ljava/lang/String;)V"),
                    new Literal("first")
                ).toXmir()
            ).xml(),
            new SameXml(
                String.join(
                    "\n",
                    "<o base='.new'>",
                    "  <o base='string' data='bytes'>64 65 73 63 72 69 70 74 6F 72 3D 28 4C 6A 61 76 61 2F 6C 61 6E 67 2F 53 74 72 69 6E 67 3B 29 56</o>",
                    "  <o base='.new-type'><o base='string' data='bytes'>41</o></o>",
                    "  <o base='string' data='bytes'>66 69 72 73 74</o>",
                    "</o>"
                )
            )
        );
    }

    @Test
    void createsConstructorFromXmir() {
        MatcherAssert.assertThat(
            "Can't parse constructor from xmir representation",
            new Constructor(
                new XmlNode(ConstructorTest.CONSTRUCTOR),
                node -> {
                    final AstNode result;
                    if (node.equals(
                        new XmlNode("<o base='string' data='bytes'>66 69 72 73 74</o>")
                    )) {
                        result = new Literal("first");
                    } else if (node.equals(
                        new XmlNode("<o base='string' data='bytes'>73 65 63 6F 6E 64</o>")
                    )) {
                        result = new Literal("second");
                    } else if (node.equals(
                        new XmlNode("<o base='int' data='bytes'>00 00 00 00 00 00 00 03</o>")
                    )) {
                        result = new Literal(3);
                    } else if (node.equals(
                        new XmlNode(
                            "<o base='.new-type'><o base='string' data='bytes'>41</o></o>"
                        )
                    )) {
                        result = new NewAddress("A");
                    } else {
                        throw new IllegalArgumentException(
                            String.format("Can't parse constructor from node %s", node)
                        );
                    }
                    return result;
                }
            ),
            Matchers.equalTo(
                new Constructor(
                    "A",
                    new Attributes()
                        .descriptor("(Ljava/lang/String;Ljava/lang/String;I)V")
                        .interfaced(false),
                    new Literal("first"),
                    new Literal("second"),
                    new Literal(3)
                )
            )
        );
    }
}
