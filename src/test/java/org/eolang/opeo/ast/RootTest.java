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
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test case for {@link Root}.
 * @since 0.1
 */
class RootTest {

    @Test
    void combinesAllChildElementsIntoSingleXmir() throws ImpossibleModificationException {
        final Root root = new Root();
        root.append(new Literal("Wake up, Neo..."));
        root.append(new Opcode(Opcodes.RETURN));
        root.append(new Literal(1));
        MatcherAssert.assertThat(
            String.format(
                "We expected to get the following XMIR:%n%s%n",
                String.join(
                    "\n",
                    "<o base='tuple'>",
                    "  <o base='string' data='bytes'>57 61 6B 65 20 75 70 2C 20 4E 65 6F 2E 2E 2E</o>",
                    "  <o base='opcode' name='RETURN-1'>",
                    "    <o base='int' data='bytes'>00 00 00 00 00 00 00 B1</o>",
                    "  </o>",
                    "  <o base='int' data='bytes'>00 00 00 00 00 00 00 01</o>",
                    "</o>"
                )
            ),
            new Xembler(root.toXmir(), new Transformers.Node()).xml(),
            Matchers.allOf(
                XhtmlMatchers.hasXPath("/o[@base='tuple']"),
                XhtmlMatchers.hasXPath("/o[@base='tuple']/o[@base='string' and @data='bytes']"),
                XhtmlMatchers.hasXPath(
                    "/o[@base='tuple']/o[@base='opcode' and contains(@name,'RETURN')]"
                ),
                XhtmlMatchers.hasXPath(
                    "/o[@base='tuple']/o[@base='int' and @data='bytes' and text()='00 00 00 00 00 00 00 01']"
                )
            )
        );
    }
}
