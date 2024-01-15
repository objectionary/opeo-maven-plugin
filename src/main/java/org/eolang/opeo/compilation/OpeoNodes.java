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
import java.util.Optional;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.xmir.XmlInstruction;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.ast.Add;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.Constructor;
import org.eolang.opeo.ast.Invocation;
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
     * @checkstyle CyclomaticComplexityCheck (200 lines)
     * @checkstyle ExecutableStatementCountCheck (200 lines)
     * @checkstyle JavaNCSSCheck (200 lines)
     * @checkstyle NestedIfDepthCheck (200 lines)
     * @todo #77:90min Finish compilation pipeline.
     *  Right now we successfully disassemble bytecode (jeo), transform it to a high-level
     *  representation (opeo) and then somehow compile back to low-level representation.
     *  Probably with some mistakes.
     *  We need to finish this implementation and check if the transformation is correct.
     *  To do so, we need to enable "benchmark" integration test.
     *  To do so, just add "jeo.assemble" goal.
     * @todo #77:90min Refactor OpeoNodes.node() method.
     *  The parsing method OpeoNodes.node() looks overcomplicated and violates many
     *  code quality standards. We should refactor the method and remove all
     *  the checkstyle and PMD "suppressions" from the method.
     */
    @SuppressWarnings({"PMD.NcssCount", "PMD.ExcessiveMethodLength"})
    private static AstNode node(final XmlNode node) {
        final AstNode result;
        final Optional<String> base = node.attribute("base");
        if (base.isEmpty()) {
            throw new IllegalArgumentException(
                String.format(
                    "Can't recognize node: %n%s%n'base' attribute should be present",
                    node
                )
            );
        }
        final String attribute = base.get();
        if (".plus".equals(attribute)) {
            final List<XmlNode> inner = node.children().collect(Collectors.toList());
            final AstNode left = OpeoNodes.node(inner.get(0));
            final AstNode right = OpeoNodes.node(inner.get(1));
            result = new Add(left, right);
        } else if ("opcode".equals(attribute)) {
            final XmlInstruction instruction = new XmlInstruction(node.node());
            result = new Opcode(instruction.opcode(), instruction.operands());
        } else if ("label".equals(attribute)) {
            final List<XmlNode> inner = node.children().collect(Collectors.toList());
            result = new Label(OpeoNodes.node(inner.get(0)));
        } else if ("int".equals(attribute)) {
            result = new Literal(new HexString(node.text()).decodeAsInt());
        } else if ("string".equals(attribute)) {
            result = new Literal(new HexString(node.text()).decode());
        } else if (".super".equals(attribute)) {
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
        } else if ("$".equals(attribute)) {
            result = new This();
        } else if (attribute.contains("local")) {
            result = new Variable(node);
        } else if (".write".equals(attribute)) {
            final List<XmlNode> inner = node.children().collect(Collectors.toList());
            final AstNode variable = OpeoNodes.node(inner.get(0));
            final AstNode value = OpeoNodes.node(inner.get(1));
            if (variable instanceof Variable) {
                result = new StoreLocal(variable, value);
            } else {
                result = new WriteField(variable, value);
            }
        } else if (".new".equals(attribute)) {
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
        } else if (!attribute.isEmpty() && attribute.charAt(0) == '.') {
            final List<XmlNode> inner = node.children().collect(Collectors.toList());
            final AstNode target = OpeoNodes.node(inner.get(0));
            final List<AstNode> arguments;
            if (inner.size() > 1) {
                arguments = inner.subList(1, inner.size())
                    .stream()
                    .map(OpeoNodes::node)
                    .collect(Collectors.toList());
            } else {
                arguments = Collections.emptyList();
            }
            result = new Invocation(target, attribute.substring(1), arguments);
        } else {
            throw new IllegalArgumentException(
                String.format("Can't recognize node: %n%s%n", node)
            );
        }
        return result;
    }
}
