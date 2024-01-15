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
import org.eolang.jeo.representation.xmir.XmlInstruction;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.ast.Add;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.Constructor;
import org.eolang.opeo.ast.Label;
import org.eolang.opeo.ast.Literal;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.ast.StoreLocal;
import org.eolang.opeo.ast.Super;
import org.eolang.opeo.ast.This;
import org.eolang.opeo.ast.Variable;
import org.eolang.opeo.ast.WriteField;
import org.objectweb.asm.Type;
import org.xembly.Xembler;

/**
 * High-level representation of Opeo nodes.
 * @since 0.1
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class OpeoNodes {

    /**
     * Opeo nodes.
     */
    private final List<XmlNode> nodes;

    /**
     * Constructor.
     * @param nodes Opeo nodes.
     */
    public OpeoNodes(final AstNode... nodes) {
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
        return OpeoNodes.node(node).opcodes()
            .stream()
            .map(AstNode::toXmir)
            .map(Xembler::new)
            .map(Xembler::xmlQuietly)
            .map(XmlNode::new)
            .collect(Collectors.toList());
    }

    /**
     * Convert XmlNode to AstNode.
     * @param node XmlNode
     * @return Ast node
     * @todo #65:90min Add more nodes to the parser.
     *  Currently we only support addition and integer literals.
     *  We need to add support for multiplication and many other nodes.
     *  You can check all the required nodes in the {@link org.eolang.opeo.ast} package.
     *  To check all correct transformation you can modify 'benchmark' integration test.
     */
    private static AstNode node(final XmlNode node) {
        final AstNode result;
        if (node.hasAttribute("base", ".plus")) {
            final List<XmlNode> inner = node.children().collect(Collectors.toList());
            final AstNode left = OpeoNodes.node(inner.get(0));
            final AstNode right = OpeoNodes.node(inner.get(1));
            result = new Add(left, right);
        } else if (node.hasAttribute("base", "opcode")) {
            final XmlInstruction instruction = new XmlInstruction(node.node());
            result = new Opcode(instruction.opcode(), instruction.operands());
        } else if (node.hasAttribute("base", "label")) {
            final List<XmlNode> inner = node.children().collect(Collectors.toList());
            result = new Label(OpeoNodes.node(inner.get(0)));
        } else if (node.hasAttribute("base", "int")) {
            result = new Literal(new HexString(node.text()).decodeAsInt());
        } else if (node.hasAttribute("base", "string")) {
            result = new Literal(new HexString(node.text()).decode());
        } else if (node.hasAttribute("base", ".super")) {
            final List<XmlNode> inner = node.children().collect(Collectors.toList());
            final AstNode instance = OpeoNodes.node(inner.get(0));
            final List<AstNode> arguments;
            if (inner.size() > 1) {
                arguments = inner.subList(1, inner.size())
                    .stream()
                    .map(OpeoNodes::node)
                    .collect(Collectors.toList());
            } else {
                arguments = Collections.emptyList();
            }
            result = new Super(instance, arguments);
        } else if (node.hasAttribute("base", "$")) {
            result = new This();
        } else if (node.hasAttribute("base", "local0")) {
            //@checkstyle MethodBodyCommentsCheck (10 lines)
            // @todo #65:90min Handle local variables.
            //  Currently we just treat all the variables as local variable with index 0
            //  and type int. We need to handle local variables correctly.
            result = new Variable(Type.INT_TYPE, 0);
        } else if (node.hasAttribute("base", "local1")) {
            result = new Variable(Type.INT_TYPE, 1);
        } else if (node.hasAttribute("base", ".write")) {
            final List<XmlNode> inner = node.children().collect(Collectors.toList());
            final AstNode variable = OpeoNodes.node(inner.get(0));
            final AstNode value = OpeoNodes.node(inner.get(1));
            if (variable instanceof Variable) {
                result = new StoreLocal(variable, value);
            } else {
                result = new WriteField(variable, value);
            }
        } else if (node.hasAttribute("base", ".new")) {
            final List<XmlNode> inner = node.children().collect(Collectors.toList());
            final String type = inner.get(0).text();
            final List<AstNode> arguments;
            if (inner.size() > 1) {
                arguments = inner.subList(1, inner.size())
                    .stream()
                    .map(OpeoNodes::node)
                    .collect(Collectors.toList());
            } else {
                arguments = Collections.emptyList();
            }
            result = new Constructor(type, arguments);
        } else {
            throw new IllegalArgumentException(
                String.format("Can't recognize node: %n%s%n", node)
            );
        }
        return result;
    }
}
