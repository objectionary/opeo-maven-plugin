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

import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesData;
import org.eolang.jeo.representation.xmir.HexString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Method or field reference.
 * @since 0.5
 * @todo #329:90min Refactor and rename {@link Handle} class.
 *  I implemented this class as fast as possible to implement {@link DynamicInvocation} class.
 *  It is not a good name for this class. It should be renamed to something more meaningful.
 *  Moreover, we need to add more tests for it.
 */
@ToString
@EqualsAndHashCode
public final class Handle implements Xmir {

    /**
     * Reference type.
     * See {@link org.objectweb.asm.Handle#tag}
     */
    private final int tag;

    /**
     * Name of the method or field.
     * See {@link org.objectweb.asm.Handle#name}
     */
    private final String name;

    /**
     * Owner of the method or field.
     * See {@link org.objectweb.asm.Handle#owner}
     */
    private final String owner;

    /**
     * Descriptor of the method or field.
     * See {@link org.objectweb.asm.Handle#descriptor}
     */
    private final String desc;

    /**
     * Is it an interface method?
     * See {@link org.objectweb.asm.Handle#isInterface}
     */
    private final boolean itf;

    /**
     * Constructor.
     * @param root XMIR node to parse.
     */
    public Handle(final XmlNode root) {
        this(root, root.children().collect(Collectors.toList()));
    }

    /**
     * Constructor.
     * @param root XMIR node to parse.
     * @param children XMIR root node children to parse.
     */
    public Handle(final XmlNode root, final List<XmlNode> children) {
        this(
            Handle.xtag(children),
            Handle.xname(root),
            Handle.xowner(root),
            Handle.xdesc(children),
            Handle.xitf(children)
        );
    }

    /**
     * Constructor.
     * @param handle ASM handle.
     */
    public Handle(final org.objectweb.asm.Handle handle) {
        this(
            handle.getTag(),
            handle.getName(),
            handle.getOwner(),
            handle.getDesc(),
            handle.isInterface()
        );
    }

    /**
     * Constructor.
     * @param tag Reference type.
     * @param name Name of the method or field.
     * @param owner Owner of the method or field.
     * @param desc Descriptor of the method or field.
     * @param itf Is it an interface method?
     */
    public Handle(
        final int tag,
        final String name,
        final String owner,
        final String desc,
        final boolean itf
    ) {
        this.tag = tag;
        this.name = name;
        this.owner = owner;
        this.desc = desc;
        this.itf = itf;
    }

    /**
     * Parse name from XMIR node.
     * @param root XMIR node to parse.
     * @return Name.
     */
    private static String xname(final XmlNode root) {
        return root.attribute("base")
            .map(s -> s.substring(s.lastIndexOf('.') + 1))
            .orElseThrow(() -> new IllegalArgumentException("Name is required"));
    }

    /**
     * Parse owner from XMIR node.
     * @param root XMIR node to parse.
     * @return Owner.
     */
    private static String xowner(final XmlNode root) {
        return root.attribute("base")
            .map(s -> s.substring(0, s.lastIndexOf('.')))
            .orElseThrow(() -> new IllegalArgumentException("Owner is required")).replace('.', '/');
    }

    /**
     * Parse tag from XMIR node.
     * @param children XMIR node children.
     * @return Tag.
     */
    private static int xtag(final List<XmlNode> children) {
        return new HexString(children.get(0).text()).decodeAsInt();
    }

    /**
     * Parse descriptor from XMIR node.
     * @param children XMIR node children.
     * @return Descriptor.
     */
    private static String xdesc(final List<XmlNode> children) {
        return new HexString(children.get(1).text()).decode();
    }

    /**
     * Parse isInterface from XMIR node.
     * @param children XMIR node children.
     * @return true if it is an interface method.
     */
    private static boolean xitf(final List<XmlNode> children) {
        return new HexString(children.get(2).text()).decodeAsBoolean();
    }

    /**
     * Convert to ASM handle.
     * @return ASM handle.
     */
    public org.objectweb.asm.Handle toAsm() {
        return new org.objectweb.asm.Handle(this.tag, this.owner, this.name, this.desc, this.itf);
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives().add("o")
            .attr("base", String.format("%s.%s", this.owner.replace('/', '.'), this.name))
            .append(new DirectivesData("", this.tag))
            .append(new DirectivesData("", this.desc))
            .append(new DirectivesData("", this.itf))
            .up();
    }
}
