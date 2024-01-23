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
import org.eolang.opeo.ast.Attributes;
import org.eolang.opeo.ast.Constructor;
import org.eolang.opeo.ast.InstanceField;
import org.eolang.opeo.ast.Invocation;
import org.eolang.opeo.ast.Label;
import org.eolang.opeo.ast.Literal;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.ast.StoreLocal;
import org.eolang.opeo.ast.Super;
import org.eolang.opeo.ast.This;
import org.eolang.opeo.ast.Variable;
import org.eolang.opeo.ast.WriteField;
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
     * @todo #77:90min Refactor OpeoNodes.node() method.
     *  The parsing method OpeoNodes.node() looks overcomplicated and violates many
     *  code quality standards. We should refactor the method and remove all
     *  the checkstyle and PMD "suppressions" from the method.
     */
    @SuppressWarnings({"PMD.NcssCount", "PMD.ExcessiveMethodLength"})
    private static AstNode node(final XmlNode node) {
        final AstNode result;
        final String base = node.attribute("base").orElseThrow(
            () -> new IllegalArgumentException(
                String.format(
                    "Can't recognize node: %n%s%n'base' attribute should be present",
                    node
                )
            )
        );
        if (".plus".equals(base)) {
            final List<XmlNode> inner = node.children().collect(Collectors.toList());
            final AstNode left = OpeoNodes.node(inner.get(0));
            final AstNode right = OpeoNodes.node(inner.get(1));
            result = new Add(left, right);
        } else if ("opcode".equals(base)) {
            final XmlInstruction instruction = new XmlInstruction(node.node());
            result = new Opcode(instruction.opcode(), instruction.operands());
        } else if ("label".equals(base)) {
            final List<XmlNode> inner = node.children().collect(Collectors.toList());
            result = new Label(OpeoNodes.node(inner.get(0)));
        } else if ("int".equals(base)) {
            result = new Literal(new HexString(node.text()).decodeAsInt());
        } else if ("string".equals(base)) {
            result = new Literal(new HexString(node.text()).decode());
        } else if (".super".equals(base)) {
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
            result = new Super(
                instance,
                arguments,
                node.attribute("scope")
                    .orElseThrow(
                        () -> new IllegalArgumentException(
                            "Can't find descriptor for super invocation"
                        )
                    )
            );
        } else if ("$".equals(base)) {
            result = new This();
        } else if (base.contains("local")) {
            result = new Variable(node);
        } else if (".write".equals(base)) {
            //@checkstyle MethodBodyCommentsCheck (20 lines)
            // @todo #80:90min Correct parsing of WriteField node
            //  Currently we have an ad-hoc solution for parsing WriteField node.
            //  It looks ugly, requires refactoring and maybe adding new ast node types.
            //  For now the parsing is done in a way to make the tests pass.
            if (node.attribute("scope").isPresent()) {
                final List<XmlNode> inner = node.children().collect(Collectors.toList());
                final AstNode target = OpeoNodes.node(
                    inner.get(0).children().collect(Collectors.toList()).get(0)
                );
                final AstNode value = OpeoNodes.node(inner.get(1));
                result = new WriteField(
                    target,
                    value,
                    new Attributes(node.attribute("scope").orElseThrow())
                );
            } else {
                final List<XmlNode> inner = node.children().collect(Collectors.toList());
                final AstNode variable = OpeoNodes.node(inner.get(0));
                final AstNode value = OpeoNodes.node(inner.get(1));
                result = new StoreLocal(variable, value);
            }
        } else if (".new".equals(base)) {
            final List<XmlNode> inner = node.children().collect(Collectors.toList());
            final String type = inner.get(0).attribute("base").orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "Can't find type of '%s'",
                        base
                    )
                )
            );
            final List<AstNode> arguments;
            if (inner.size() > 1) {
                arguments = inner.subList(1, inner.size())
                    .stream()
                    .map(OpeoNodes::node)
                    .collect(Collectors.toList());
            } else {
                arguments = Collections.emptyList();
            }
            result = new Constructor(
                type,
                new Attributes(
                    node.attribute("scope").orElseThrow(
                        () -> new IllegalStateException(
                            String.format(
                                "Can't find 'scope' attribute of constructor in node: %n%s%n",
                                node
                            )
                        )
                    )
                ),
                arguments
            );
        } else if (!base.isEmpty() && base.charAt(0) == '.') {
            final Attributes attributes = new Attributes(
                node.attribute("scope")
                    .orElseThrow(
                        () -> new IllegalArgumentException(
                            String.format(
                                "Can't find attributes of '%s'",
                                base
                            )
                        )
                    )
            );
            if ("field".equals(attributes.type())) {
                final List<XmlNode> inner = node.children().collect(Collectors.toList());
                final AstNode target = OpeoNodes.node(inner.get(0));
                result = new InstanceField(target, attributes);
            } else {
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
                result = new Invocation(target, attributes, arguments);
            }
        } else {
            throw new IllegalArgumentException(
                String.format("Can't recognize node: %n%s%n", node)
            );
        }
        return result;
    }
}
