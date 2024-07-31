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
package org.eolang.opeo.decompilation;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * XMIR representation without aliases.
 * @since 0.4
 */
public final class WithoutAliases {

    /**
     * Xmir with aliases.
     */
    private final XML original;

    /**
     * Constructor.
     * @param original Xmir with aliases.
     */
    public WithoutAliases(final XML original) {
        this.original = original;
    }

    /**
     * Xmir without aliases.
     * @return Xmir without aliases.
     */
    public XML toXml() {
        return new XMLDocument(
            new Xembler(
                new Directives()
                    .xpath("./program/metas/meta[head[text()='alias']]")
                    .remove()
            ).applyQuietly(this.original.node())
        );
    }
}
