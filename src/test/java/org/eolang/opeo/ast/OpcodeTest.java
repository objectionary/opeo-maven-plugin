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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test case for {@link Opcode}.
 * @since 0.1
 * @todo #95:90min Enable the 'prints 'test below and fix it.
 *  Currently the test {@link OpcodeTest#prints} is disabled because it fails.
 *  You can find the reason of the failure in the issue #2801.
 *  Link: <a href="https://github.com/objectionary/eo/issues/2801">#2801</a>.
 *  Also in the project we have several more tests with the same problem.
 *  You can find them by the '2801' in the project.
 */
class OpcodeTest {

    @Test
    void transformsToXml() {
        MatcherAssert.assertThat(
            String.format(
                "We expect the following XML to be generated: %s",
                "<o base='opcode' name='LDC-1'><o base='int' data='bytes'>00 00 00 00 00 00 00 12</o><o base='string' data='bytes'>68 65 6C 6C 6F</o></o>"
            ),
            new Xembler(
                new Opcode(Opcodes.LDC, "hello").toXmir(),
                new Transformers.Node()
            ).xmlQuietly(),
            Matchers.allOf(
                XhtmlMatchers.hasXPath("./o[@base='opcode']/o[@base='int']"),
                XhtmlMatchers.hasXPath("./o[@base='opcode']/o[@base='string']")
            )
        );
    }

    @Test
    @Disabled("https://github.com/objectionary/eo/issues/2801")
    void prints() {
        MatcherAssert.assertThat(
            "We expect the following string to be printed: 'opcode > LDC-1\n  18\n  \"bye\"'",
            new Opcode(Opcodes.LDC, "bye").print(),
            Matchers.allOf(
                Matchers.containsString("opcode > LDC"),
                Matchers.containsString("18"),
                Matchers.containsString("bye")
            )
        );
    }
}
