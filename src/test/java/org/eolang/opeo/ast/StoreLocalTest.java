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
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test case for {@link StoreLocal}.
 * @since 0.1
 */
class StoreLocalTest {

    @Test
    void printsCorrectly() {
        MatcherAssert.assertThat(
            "We expect the printed assignment to be correct",
            new StoreLocal(new Variable(Type.INT_TYPE, 1), new Literal(1)).print(),
            Matchers.equalTo("llocal1int = 1")
        );
    }

    @Test
    void convertsToXmirCorrectly() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new StoreLocal(
                new Variable(Type.DOUBLE_TYPE, 2), new Literal(0.1d)
            ).toXmir(),
            new Transformers.Node()
        ).xml();
        MatcherAssert.assertThat(
            String.format(
                "We expect the XMIR to be correct, but it's not:%n%s%n",
                xml
            ),
            xml,
            XhtmlMatchers.hasXPaths(
                "./o[@base='.write']",
                "./o[@base='.write']/o[@base='llocal2']",
                "./o[@base='.write']/o[@base='float']"
            )
        );
    }
}
