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
import org.eolang.jeo.representation.xmir.XmlNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link StaticInvocation}.
 * @since 0.2
 */
final class StaticInvocationTest {

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        final StaticInvocation invocation = new StaticInvocation("java/lang/A", "get", "()I");
        MatcherAssert.assertThat(
            String.format(
                "Can't convert static invocation %s to xmir representation",
                invocation
            ),
            new Xembler(invocation.toXmir()).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[@base='.int$get']",
                "./o[@base='.int$get']/o[@base='java.lang.j$A']"
            )
        );
    }

    @Test
    void parsesStaticInvocationFromXmir() {
        MatcherAssert.assertThat(
            "Can't parse static invocation from xmir representation",
            new StaticInvocation(
                new XmlNode(
                    String.join(
                        "\n",
                        "<o base='.get'>",
                        "<o base='java.lang.A'/>",
                        "<o base='string' data='bytes'>6E 61 6D 65 3D 67 65 74 7C 64 65 73 63 72 69 70 74 6F 72 3D 28 29 49 7C 74 79 70 65 3D 73 74 61 74 69 63</o>",
                        "</o>"
                    )
                )
            ),
            Matchers.equalTo(
                new StaticInvocation(
                    "java/lang/A",
                    "get",
                    "()I"
                )
            )
        );
    }
}
