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
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test case for {@link Invocation}.
 * @since 0.1
 */
class InvocationTest {

    @Test
    void transformsToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            String.format(
                "We expect the following XMIRl to be generated:%n%s%n",
                String.join(
                    "\n",
                    "<o base='.bar'>",
                    "  <o base='.new'>",
                    "    <o base='foo'/>",
                    "  </o>",
                    "  <o base='string' data='bytes'>62 61 7A</o>",
                    "</o>"
                )
            ),
            new Xembler(
                new Invocation(
                    new Constructor("foo"),
                    "bar",
                    new Literal("baz")
                ).toXmir(),
                new Transformers.Node()
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "/o[@base='.bar']/o[@base='.new']/o[@base='foo']",
                "/o[@base='.bar']/o[@base='string' and @data='bytes' and text()='62 61 7A']"
            )
        );
    }

}