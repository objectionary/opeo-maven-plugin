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
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Synced;
import org.cactoos.scalar.Unchecked;

/**
 * Xmir with package.
 * @since 0.2
 */
@ToString
@EqualsAndHashCode
public final class XmirEntry {

    /**
     * XML representation of XMIR.
     */
    @ToString.Exclude
    private final Unchecked<XML> xml;

    /**
     * Package name.
     */
    private final String pckg;

    /**
     * Constructor.
     * @param path Path to XMIR.
     * @param pckg Package name.
     */
    XmirEntry(final Path path, final String pckg) {
        this(XmirEntry.fromFile(path), pckg);
    }

    /**
     * Constructor.
     * @param xmir XMIR as XML.
     * @param pckg Package name.
     */
    XmirEntry(final XML xmir, final String pckg) {
        this(XmirEntry.fromXml(xmir), pckg);
    }

    /**
     * Constructor.
     * @param xml Lazy XML.
     * @param pckg Package name.
     */
    public XmirEntry(final Unchecked<XML> xml, final String pckg) {
        this.xml = xml;
        this.pckg = pckg;
    }

    /**
     * Transform XMIR.
     * @param transformer Function to transform XMIR.
     * @return Transformed XMIR.
     */
    public XmirEntry transform(final Function<? super XML, ? extends XML> transformer) {
        return new XmirEntry(transformer.apply(this.xml.value()), this.pckg);
    }

    /**
     * Apply XPath query.
     * @param query XPath query.
     * @return List of strings returned by query.
     */
    public List<String> xpath(final String query) {
        return this.xml.value().xpath(query);
    }

    /**
     * To XML.
     * @return XML representation of XMIR.
     */
    XML toXml() {
        return this.xml.value();
    }

    /**
     * Package name.
     * @return Package name (relative path).
     */
    String relative() {
        return this.pckg;
    }

    /**
     * Prestructor from file.
     * @param path Path to the file.
     * @return Lazy XMIR entry.
     */
    private static Unchecked<XML> fromFile(final Path path) {
        return new Unchecked<>(
            new Synced<>(
                new Sticky<>(
                    () -> {
                        try {
                            return new XMLDocument(path);
                        } catch (final FileNotFoundException exception) {
                            throw new IllegalStateException(
                                String.format("Can't find '%x'", path),
                                exception
                            );
                        }
                    }
                )
            )
        );
    }

    /**
     * Prestructor from XML.
     * @param xml XML.
     * @return Lazy XMIR entry.
     */
    private static Unchecked<XML> fromXml(final XML xml) {
        return new Unchecked<>(new Synced<>(new Sticky<>(() -> xml)));
    }

}
