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
package org.eolang.opeo.compilation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.Opcode;
import org.objectweb.asm.Opcodes;
import org.xembly.Xembler;

/**
 * High-level representation of Opeo nodes.
 * @since 0.1
 */
public final class OpeoNodes {

    /**
     * Opeo nodes.
     */
    private final List<XmlNode> nodes;

    public OpeoNodes(AstNode... nodes) {
        this(
            Arrays.stream(nodes)
                .map(AstNode::toXmir)
                .map(Xembler::new)
                .map(Xembler::xmlQuietly)
                .map(XmlNode::new)
                .collect(Collectors.toList())
        );
    }


    /**
     * Constructor.
     * @param nodes Opeo nodes.
     */
    public OpeoNodes(XmlNode... nodes) {
        this(List.of(nodes));
    }

    /**
     * Constructor.
     * @param nodes Opeo nodes.
     */
    public OpeoNodes(final List<XmlNode> nodes) {
        this.nodes = nodes;
    }

    /**
     * Convert to XML nodes.
     * @return XML nodes.
     */
    List<XmlNode> toJeoNodes() {
        return this.nodes.stream()
            .map(OpeoNodes::opcodes)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    /**
     * Convert XmlNode into a list of opcodes.
     * @param node XmlNode
     * @return List of opcodes
     */
    private static List<XmlNode> opcodes(final XmlNode node) {
        if (node.hasAttribute("base", ".plus")) {
            final List<XmlNode> inner = node.children().collect(Collectors.toList());
            final int first = new HexString(inner.get(0).text()).decodeAsInt();
            final int second = new HexString(inner.get(1).text()).decodeAsInt();
            final Opcode left;
            switch (first) {
                case 0:
                    left = new Opcode(Opcodes.ICONST_0);
                    break;
                case 1:
                    left = new Opcode(Opcodes.ICONST_1);
                    break;
                case 2:
                    left = new Opcode(Opcodes.ICONST_2);
                    break;
                case 3:
                    left = new Opcode(Opcodes.ICONST_3);
                    break;
                case 4:
                    left = new Opcode(Opcodes.ICONST_4);
                    break;
                case 5:
                    left = new Opcode(Opcodes.ICONST_5);
                    break;
                default:
                    left = new Opcode(Opcodes.BIPUSH, first);
                    break;
            }
            final Opcode right;
            switch (second) {
                case 0:
                    right = new Opcode(Opcodes.ICONST_0);
                    break;
                case 1:
                    right = new Opcode(Opcodes.ICONST_1);
                    break;
                case 2:
                    right = new Opcode(Opcodes.ICONST_2);
                    break;
                case 3:
                    right = new Opcode(Opcodes.ICONST_3);
                    break;
                case 4:
                    right = new Opcode(Opcodes.ICONST_4);
                    break;
                case 5:
                    right = new Opcode(Opcodes.ICONST_5);
                    break;
                default:
                    right = new Opcode(Opcodes.BIPUSH, second);
                    break;
            }
            return Stream.of(left, right, new Opcode(Opcodes.IADD))
                .map(Opcode::toXmir)
                .map(Xembler::new)
                .map(Xembler::xmlQuietly)
                .map(XmlNode::new)
                .collect(Collectors.toList());
        } else {
            return Collections.singletonList(node);
        }
    }
}
