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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesData;
import org.eolang.jeo.representation.xmir.HexString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.compilation.Parser;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Array constructor.
 * @since 0.1
 */
@ToString
@EqualsAndHashCode
public final class ArrayConstructor implements AstNode, Typed {

    /**
     * Array size.
     */
    private final AstNode size;

    /**
     * Array type.
     */
    private final String type;

    /**
     * Constructor.
     * @param node Xmir representation of an array constructor.
     * @param parser Parser that will be used to parse the child nodes of the array constructor.
     */
    public ArrayConstructor(final XmlNode node, final Parser parser) {
        this(
            parser.parse(node.children().collect(Collectors.toList()).get(1)),
            new HexString(node.firstChild().text()).decode()
        );
    }

    /**
     * Constructor.
     * @param size Array size
     * @param type Array type
     */
    public ArrayConstructor(final AstNode size, final String type) {
        this.size = size;
        this.type = type;
    }

    @Override
    public Iterable<Directive> toXmir() {
        final Directives directives = new Directives();
        directives.add("o")
            .attr("base", ".array-node")
            .append(new DirectivesData(this.type))
            .append(this.size.toXmir());
        return directives.up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(0);
        res.addAll(this.size.opcodes());
        res.add(new Opcode(Opcodes.ANEWARRAY, this.type));
        return res;
    }

    @Override
    public Type type() {
        return Type.getType(String.format("[L%s;", this.type));
    }
}
