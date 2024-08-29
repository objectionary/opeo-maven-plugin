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
 * Test case for {@link InterfaceInvocation}.
 * @since 0.2
 */
final class InterfaceInvocationTest {

    /**
     * XMIR representation of the interface invocation.
     */
    private static final String XMIR = String.join(
        "",
        "<o base='.int$foo'>",
        "   <o base='$'>",
        "      <o base='string' data='bytes'>64 65 73 63 72 69 70 74 6F 72 3D 6A 61 76 61 2E 6C 61 6E 67 2E 4F 62 6A 65 63 74</o>",
        "   </o>",
        "   <o base='string' data='bytes'>64 65 73 63 72 69 70 74 6F 72 3D 28 29 49 7C 6E 61 6D 65 3D 66 6F 6F 7C 74 79 70 65 3D 69 6E 74 65 72 66 61 63 65</o>",
        "</o>"
    );

    @Test
    void transformsToXmir() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new InterfaceInvocation(
                new This(),
                new Attributes("name=foo").descriptor("()I")
            ).toXmir()
        ).xml();
        MatcherAssert.assertThat(
            String.format("Can't transform to correct XMIR, what we got is '%s'", xml),
            InterfaceInvocationTest.XMIR,
            new SameXml(xml)
        );
    }

    @Test
    void createsInterfaceInvocationFromXmir() {
        MatcherAssert.assertThat(
            "Can't parse correct interface invocation from XMIR",
            new InterfaceInvocation(
                new XmlNode(InterfaceInvocationTest.XMIR),
                node -> new This()
            ),
            Matchers.equalTo(
                new InterfaceInvocation(
                    new This(),
                    new Attributes("name=foo").descriptor("()I")
                )
            )
        );
    }
}
