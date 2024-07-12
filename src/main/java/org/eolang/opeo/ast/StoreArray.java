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
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.compilation.Parser;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Store array element.
 * @since 0.1
 * @todo #329:90min Remove ad-hoc solution with storing array in a field.
 *  In the {@link StoreArray#opcodes()} I added ad-hoc solution to solve the problem with
 *  array elements storage if the array is a field.
 *  See the 'if' statement.
 *  This ad-hoc solution means we have architectural problem with array elements storage opcodes
 *  generation.
 */
public final class StoreArray implements AstNode, Typed {

    /**
     * Target array.
     */
    private final AstNode array;

    /**
     * Index where to store.
     */
    private final AstNode index;

    /**
     * Value to store.
     */
    private final AstNode value;

    /**
     * Constructor.
     * @param root Xmir representation of a store array element.
     * @param parser Parser that will be used to parse the nodes of the store array element.
     */
    public StoreArray(final XmlNode root, final Parser parser) {
        this(root.children().collect(Collectors.toList()), parser);
    }

    /**
     * Constructor.
     * @param children Children nodes that represent an array, an index and a value.
     * @param parser Parser that will be used to parse the children nodes.
     */
    public StoreArray(final List<XmlNode> children, final Parser parser) {
        this(
            parser.parse(children.get(0)),
            parser.parse(children.get(1)),
            parser.parse(children.get(2))
        );
    }

    /**
     * Constructor.
     * @param array Array
     * @param index Index
     * @param value Value
     */
    public StoreArray(
        final AstNode array,
        final AstNode index,
        final AstNode value
    ) {
        this.array = array;
        this.index = index;
        this.value = value;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives().add("o")
            .attr("base", ".write-array")
            .append(this.array.toXmir())
            .append(this.index.toXmir())
            .append(this.value.toXmir())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(0);
        if (this.array instanceof FieldRetrieval) {
            res.addAll(this.array.opcodes());
        } else {
            res.addAll(this.array.opcodes());
            res.add(new Opcode(Opcodes.DUP));
        }
        res.addAll(this.index.opcodes());
        res.addAll(this.value.opcodes());
        res.add(new Opcode(Opcodes.AASTORE));
        return res;
    }

    @Override
    public Type type() {
        return Type.VOID_TYPE;
    }
}
