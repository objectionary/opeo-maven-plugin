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
package org.eolang.opeo.storage;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.stream.Stream;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Lower Opcodes Storage.
 * This storage transforms all opcode names to lowercase.
 * @since 0.4
 * @todo #411:90min Remove {@link LowerOpcodesStorage} class.
 *  This class is an ad-hoc solution to hide the real problem with incorrect
 *  opcode names set by jeo-maven-plugin. When the original problem will be fix
 *  this class should be removed.
 *  The original problem is described in the
 *  <a href="https://github.com/objectionary/jeo-maven-plugin/issues/678">issue</a>
 */
public final class LowerOpcodesStorage implements Storage {

    /**
     * The storage where the XMIRs are stored.
     * Delegate.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param storage The original storage.
     */
    public LowerOpcodesStorage(final Storage storage) {
        this.storage = storage;
    }

    @Override
    public Stream<XmirEntry> all() {
        return this.storage.all();
    }

    @Override
    public void save(final XmirEntry xmir) {
        this.storage.save(xmir.transform(this::lowercase));
    }

    /**
     * Lowercase all opcode names.
     * @param xml The XMIR with opcode names.
     * @return The XMIR with lowercase opcode names.
     */
    private XML lowercase(final XML xml) {
        return new XMLDocument(
            new Xembler(
                new Directives()
                    .xpath("//o[@base='opcode' and @name]")
                    .xattr(
                        "name",
                        "translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"
                    ))
                .applyQuietly(xml.node())
        );
    }
}
