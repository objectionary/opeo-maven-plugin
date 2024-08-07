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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import org.cactoos.map.MapEntry;
import org.eolang.jeo.representation.directives.DirectivesData;
import org.eolang.jeo.representation.xmir.HexString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.xembly.Directives;

/**
 * Type attributes of AST nodes.
 * This class is useful when we need to preserve the information about types of AST nodes.
 * @since 0.1
 */
@SuppressWarnings("PMD.TooManyMethods")
@EqualsAndHashCode
public final class Attributes implements Xmir {

    /**
     * New line pattern for replacing redundant symbols from hex strings.
     */
    private static final Pattern NEW_LINE = Pattern.compile("\n", Pattern.LITERAL);

    /**
     * All attributes.
     */
    private final Map<String, String> all;

    /**
     * Constructor.
     * @param raw Raw attributes
     */
    public Attributes(final String raw) {
        this(Attributes.parse(raw));
    }

    /**
     * Constructor.
     * @param entries Attribute entry pairs.
     */
    public Attributes(final String... entries) {
        this(Attributes.fromEntries(entries));
    }

    /**
     * Constructor.
     * @param node Xmir representation of attributes.
     */
    public Attributes(final XmlNode node) {
        this(Attributes.fromXmir(node));
    }

    /**
     * Constructor.
     * @param all All attributes.
     */
    public Attributes(final Map<String, String> all) {
        this.all = new TreeMap<>(all);
    }

    @Override
    public Directives toXmir() {
        return new Directives(new DirectivesData(this.toString()));
    }

    @Override
    public String toString() {
        return this.all.entrySet().stream()
            .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining("|"));
    }

    /**
     * Get descriptor attribute.
     * @return Descriptor value.
     */
    public String descriptor() {
        return this.find("descriptor");
    }

    /**
     * Set descriptor attribute.
     * @param descriptor Descriptor
     * @return This object
     */
    public Attributes descriptor(final String descriptor) {
        this.all.put("descriptor", descriptor);
        return this;
    }

    /**
     * Get type attribute.
     * @return Type value.
     */
    public String type() {
        return this.find("type");
    }

    /**
     * Set type attribute.
     * @param type Type
     * @return This object
     */
    public Attributes type(final String type) {
        this.all.put("type", type);
        return this;
    }

    /**
     * Get owner attribute.
     * @return Owner value.
     */
    public String owner() {
        return this.find("owner");
    }

    /**
     * Set owner attribute.
     * @param owner Owner
     * @return This object
     */
    public Attributes owner(final String owner) {
        this.all.put("owner", owner);
        return this;
    }

    /**
     * Get name attribute.
     * @return Name value.
     */
    public String name() {
        return this.find("name");
    }

    /**
     * Set name attribute.
     * @param name Name
     * @return This object
     */
    public Attributes name(final String name) {
        this.all.put("name", name);
        return this;
    }

    /**
     * Get interfaced attribute.
     * @return True if method is interfaced or not.
     * @todo #201:90min Fix 'staticize' optimization.
     *  We need to use of the default value below because outdated 'staticize' optimization
     *  doesn't add 'interfaced' attribute to some methods. Particulary,
     *  scope="name=get|descriptor=()I|owner=org/eolang/benchmark/StaticizedA|type=method"
     *  in class A should contain 'interfaced=false'.
     *  To implement this, we need to send a PR to ineo-maven-plugin repository.
     *  See https://github.com/objectionary/ineo-maven-plugin
     */
    public boolean interfaced() {
        return "true".equals(this.all.getOrDefault("interfaced", "false"));
    }

    /**
     * Set interfaced attribute.
     * @param interfaced Interfaced method or not
     * @return This object
     */
    public Attributes interfaced(final boolean interfaced) {
        this.all.put("interfaced", Boolean.toString(interfaced));
        return this;
    }

    /**
     * Find attribute.
     * @param key Attribute key
     * @return Attribute value
     */
    private String find(final String key) {
        if (this.all.containsKey(key)) {
            return this.all.get(key);
        } else {
            throw new IllegalArgumentException(
                String.format("'%s' is not defined: %s", key, this)
            );
        }
    }

    /**
     * Convert entries to map.
     * @param entries Entries
     * @return Map
     */
    private static Map<String, String> fromEntries(final String... entries) {
        final Map<String, String> result;
        final int length = entries.length;
        if (length == 0) {
            result = new LinkedHashMap<>(0);
        } else {
            if (length % 2 != 0) {
                throw new IllegalArgumentException("Entries must be even");
            }
            final Map<String, String> res = new LinkedHashMap<>(0);
            for (int idx = 0; idx < length; idx += 2) {
                res.put(entries[idx], entries[idx + 1]);
            }
            result = res;
        }
        return result;
    }

    /**
     * Parse attributes from XMIR.
     * In the new method attributes are placed as a first data element.
     * @param node Xmir node attribute.
     * @return Map of attributes.
     */
    private static String fromXmir(final XmlNode node) {
        final String original = node.text();
        try {
            return Attributes.NEW_LINE.matcher(
                new HexString(original.trim()).decode()
            ).replaceAll("");
        } catch (final NumberFormatException exception) {
            throw new IllegalArgumentException(
                String.format(
                    "Can't parse attributes '%s' from '%s' node, bytes %s",
                    original,
                    node,
                    Arrays.toString(original.getBytes(StandardCharsets.UTF_8))
                ),
                exception
            );
        }
    }

    /**
     * Parse raw attributes.
     * @param raw Raw attributes
     * @return Map
     */
    private static Map<String, String> parse(final String raw) {
        try {
            return Arrays.stream(raw.split("\\|")).map(entry -> entry.split("="))
                .filter(arr -> arr.length > 1)
                .peek(Attributes::entrySize)
                .map(entry -> new MapEntry<>(entry[0], entry[1]))
                .collect(Collectors.toMap(MapEntry::getKey, MapEntry::getValue));
        } catch (final IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                String.format("Can't parse attributes '%s'", raw),
                exception
            );
        }
    }

    /**
     * Check entry size.
     * @param entry Entry to check
     */
    private static void entrySize(final String... entry) {
        if (entry.length != 2) {
            throw new IllegalArgumentException(
                String.format(
                    "Entry must have two parts, but it has %d: %s",
                    entry.length,
                    Arrays.toString(entry)
                )
            );
        }
    }
}
