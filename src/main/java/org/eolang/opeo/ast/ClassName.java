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
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesData;
import org.eolang.jeo.representation.xmir.HexString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;

/**
 * Class constant.
 * @since 0.2
 * @todo #229:30min Do we need ClassName node?
 *  It seems that we don't need this node, because it is used only in the
 *  single place in {@link org.eolang.opeo.compilation.XmirParser}. Most likely
 *  we can remove this node and use something already existing in the project.
 *  If we decide to keep this node, we need to add a test for it.
 */
@ToString
public final class ClassName implements AstNode, Typed {

    /**
     * Class name.
     */
    private final String name;

    /**
     * Constructor.
     * @param node XML node
     */
    public ClassName(final XmlNode node) {
        this(ClassName.xname(node));
    }

    /**
     * Constructor.
     * @param name Class name
     */
    public ClassName(final String name) {
        this.name = name;
    }

    @Override
    public List<AstNode> opcodes() {
        return Collections.singletonList(
            new Opcode(
                Opcodes.LDC,
                Type.getType(this.name)
            )
        );
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new DirectivesData(this.name);
    }

    @Override
    public Type type() {
        return Type.getType(Class.class);
    }

    /**
     * Prestructor to parse the Class name from the XML node.
     * @param node XML node
     * @return Class name
     */
    private static String xname(final XmlNode node) {
        return new HexString(node.text()).decode();
    }
}
