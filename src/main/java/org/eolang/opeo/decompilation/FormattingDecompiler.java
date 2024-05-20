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

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.nio.file.Path;
import org.eolang.opeo.storage.FileStorage;
import org.eolang.opeo.storage.Storage;
import org.eolang.opeo.storage.XmirEntry;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Decompiler that fixes XMIR generated by two plugins.
 * @since 0.2
 */
public final class FormattingDecompiler implements Decompiler {

    /**
     * Original decompiler.
     */
    private final Decompiler original;

    /**
     * Where to search and to save the modified XMIRs.
     */
    private final Storage modified;

    public FormattingDecompiler(final Decompiler original, final Path modified) {
        this(original, new FileStorage(modified, modified));
    }

    /**
     * Constructor.
     * @param original Original decompiler.
     * @param modified Where to search and to save the modified XMIRs.
     */
    private FormattingDecompiler(final Decompiler original, final Storage modified) {
        this.original = original;
        this.modified = modified;
    }

    @Override
    public void decompile() {
        this.original.decompile();
        this.modified.all().map(this::format).forEach(this.modified::save);
    }

    /**
     * Fix all the problems with the XMIR entry.
     * @param entry XMIR entry to fix.
     * @return Fixed XMIR entry.
     */
    private XmirEntry format(final XmirEntry entry) {
        Logger.info(this, "Adding lines to %s", entry.relative());
        return entry.transform(FormattingDecompiler::format);
    }

    /**
     * The crutch to fix several problems with incorrect XMIR generated by two plugins.
     * jeo-maven-plugin and opeo-maven-plugin generate XMIR incorrectly.
     * Particularly: they don't set the line number for some elements and
     * don't set the abstract attribute for maxs.
     *
     * @param input The problematic XMIR to fix.
     * @return The fixed XMIR.
     * @todo #226:30min Remove the crutch related to line numbers.
     *  The crutch is used to fix the problem with incorrect XMIR generated by two plugins.
     *  It adds the line number to the elements that don't have it.
     *  The crutch should be removed after the original problem that requires
     *  the crutch is fixed.
     *  You can read more about the problem here: https://github.com/objectionary/eo/issues/3189
     * @todo #226:30min Remove the crutch related to abstract attribute.
     *  The crutch is used to fix the problem with incorrect XMIR generated by two plugins.
     *  It adds the abstract attribute to the maxs elements that don't have it.
     *  The crutch should be removed after the original problem that requires
     *  the crutch is fixed.
     *  You can read more about the problem here:
     *  https://github.com/objectionary/jeo-maven-plugin/issues/602
     */
    private static XML format(final XML input) {
        final Directives lines = new Directives()
            .xpath("//o[@name='descriptor' or @name='visible']")
            .attr("line", "999");
        final Directives maxs = new Directives()
            .xpath("//o[@name='maxs']")
            .attr("abstract", "");
        return new XMLDocument(
            new Xembler(maxs).applyQuietly(new Xembler(lines).applyQuietly(input.node()))
        );
    }
}
