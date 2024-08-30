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
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.Xembler;

/**
 * Test case for {@link Duplicate}.
 * @since 0.4
 */
final class DuplicateTest {

    /**
     * Reference name.
     */
    private static final String ALIAS = "alias";

    /**
     * Definition of the literal reference together with the object.
     */
    private static final String FULL = String.join(
        "\n",
        String.format("<o base='duplicated' name='%s'>", DuplicateTest.ALIAS),
        "   <o base='int' data='bytes'>00 00 00 00 00 00 00 01</o>",
        "</o>"
    );

    /**
     * Literal reference.
     */
    private static final String REF = String.format("<o base='%s'/>", DuplicateTest.ALIAS);

    /**
     * Literal itself.
     */
    private static final AstNode LITERAL = new Const(1);

    @Test
    void transformsToXmirOnce() {
        MatcherAssert.assertThat(
            "Must transform to correct XMIR once",
            new Xembler(
                new Duplicate(DuplicateTest.ALIAS, DuplicateTest.LITERAL).toXmir()
            ).xmlQuietly(),
            new SameXml(DuplicateTest.FULL)
        );
    }

    @Test
    void transformsToXmirTwice() {
        final AstNode node = new Duplicate(DuplicateTest.ALIAS, DuplicateTest.LITERAL);
        node.toXmir();
        MatcherAssert.assertThat(
            "Must transform to correct XMIR twice and the second time should be just a reference to the previously created object",
            new Xembler(node.toXmir()).xmlQuietly(),
            new SameXml(DuplicateTest.REF)
        );
    }
}
