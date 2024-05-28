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

import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesData;
import org.eolang.jeo.representation.xmir.HexString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * This class represents NEW instruction in the JVM bytecode.
 * @since 0.2
 */
@ToString
@EqualsAndHashCode
public final class NewAddress implements AstNode, Typed {

    /**
     * Type of the new object.
     */
    private final String ctype;

    /**
     * Constructor.
     * @param node The XMIR node with a type to parse.
     */
    public NewAddress(final XmlNode node) {
        this(NewAddress.parse(node));
    }

    /**
     * Constructor.
     * @param type The type.
     */
    public NewAddress(final String type) {
        this.ctype = type;
    }

    @Override
    public List<AstNode> opcodes() {
        return Collections.singletonList(new Opcode(Opcodes.NEW, this.ctype));
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives()
            .add("o")
            .attr("base", ".new-type")
            .append(new DirectivesData(this.ctype))
            .up();
    }

    @Override
    public Type type() {
        return Type.getObjectType(this.ctype);
    }

    /**
     * Type as string.
     * @return Type as string.
     */
    String typeAsString() {
        return this.ctype;
    }

    /**
     * Prestructor that retrieves type from XMIR node.
     * @param node XMIR node to parse.
     * @return Type as string.
     */
    private static String parse(final XmlNode node) {
        return new HexString(node.firstChild().text()).decode();
    }
}
