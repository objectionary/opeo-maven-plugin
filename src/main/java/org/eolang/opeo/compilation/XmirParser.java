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
import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.xmir.HexString;
import org.eolang.jeo.representation.xmir.XmlInstruction;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.jeo.representation.xmir.XmlOperand;
import org.eolang.opeo.ast.Add;
import org.eolang.opeo.ast.Arguments;
import org.eolang.opeo.ast.ArrayConstructor;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.Attributes;
import org.eolang.opeo.ast.Cast;
import org.eolang.opeo.ast.ClassField;
import org.eolang.opeo.ast.ClassName;
import org.eolang.opeo.ast.Constant;
import org.eolang.opeo.ast.Constructor;
import org.eolang.opeo.ast.Duplicate;
import org.eolang.opeo.ast.Field;
import org.eolang.opeo.ast.FieldAssignment;
import org.eolang.opeo.ast.FieldRetrieval;
import org.eolang.opeo.ast.If;
import org.eolang.opeo.ast.InterfaceInvocation;
import org.eolang.opeo.ast.Invocation;
import org.eolang.opeo.ast.Label;
import org.eolang.opeo.ast.Labeled;
import org.eolang.opeo.ast.Literal;
import org.eolang.opeo.ast.LocalVariable;
import org.eolang.opeo.ast.Mul;
import org.eolang.opeo.ast.NewAddress;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.ast.Popped;
import org.eolang.opeo.ast.RawXml;
import org.eolang.opeo.ast.StaticInvocation;
import org.eolang.opeo.ast.StoreArray;
import org.eolang.opeo.ast.Substraction;
import org.eolang.opeo.ast.Super;
import org.eolang.opeo.ast.This;
import org.eolang.opeo.ast.VariableAssignment;
import org.xembly.Xembler;

/**
 * High-level representation of Opeo nodes.
 *
 * @since 0.1
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class XmirParser implements Parser {

    /**
     * Opeo nodes.
     */
    private final List<XmlNode> nodes;

    /**
     * Constructor.
     *
     * @param nodes Opeo nodes.
     */
    XmirParser(final AstNode... nodes) {
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
     *
     * @param nodes Opeo nodes.
     */
    XmirParser(final List<XmlNode> nodes) {
        this.nodes = nodes;
    }

    /**
     * Convert XmlNode to AstNode.
     *
     * @param node XmlNode
     * @return Ast node
     * @todo #77:90min Refactor this.node() method.
     *  The parsing method this.node() looks overcomplicated and violates many
     *  code quality standards. We should refactor the method and remove all
     *  the checkstyle and PMD "suppressions" from the method.
     * @todo #110:90min Remove ad-hoc solution for replacing descriptors and owners.
     *  Currently we have an ad-hoc solution for replacing descriptors and owners.
     *  It looks ugly and requires refactoring. To remove ad-hoc solution we need
     *  to remove all the if statements that use concerete class names as:
     *  - "org/eolang/benchmark/B"
     *  - "org/eolang/benchmark/BA"
     * @checkstyle CyclomaticComplexityCheck (200 lines)
     * @checkstyle ExecutableStatementCountCheck (200 lines)
     * @checkstyle JavaNCSSCheck (200 lines)
     * @checkstyle NestedIfDepthCheck (200 lines)
     * @checkstyle MethodLengthCheck (200 lines)     *
     * @checkstyle NoJavadocForOverriddenMethodsCheck (200 lines)
     */
    @SuppressWarnings({"PMD.NcssCount", "PMD.ExcessiveMethodLength", "PMD.CognitiveComplexity"})
    @Override
    public AstNode parse(final XmlNode node) {
        final AstNode result;
        final String base = node.attribute("base").orElseThrow(
            () -> new IllegalArgumentException(
                String.format(
                    "Can't recognize node: %n%s%n'base' attribute should be present",
                    node
                )
            )
        );
        if (".ignore-result".equals(base)) {
            result = new Popped(this.parse(node.firstChild()));
        } else if ("labeled".equals(base)) {
            result = new Labeled(node, this::parse);
        } else if ("times".equals(base)) {
            result = new Mul(node, this::parse);
        } else if (".if".equals(base)) {
            result = new If(node, this::parse);
        } else if ("load-constant".equals(base)) {
            result = new Constant(node);
        } else if (".new-type".equals(base)) {
            result = new NewAddress(node);
        } else if ("duplicated".equals(base)) {
            result = new Duplicate(this.parse(node.firstChild()));
        } else if (".plus".equals(base)) {
            result = new Add(node, this::parse);
        } else if (".minus".equals(base)) {
            result = new Substraction(node, this::parse);
        } else if ("cast".equals(base)) {
            result = new Cast(node, this::parse);
        } else if ("frame".equals(base)) {
            result = new RawXml(node);
        } else if ("opcode".equals(base)) {
            final XmlInstruction instruction = new XmlInstruction(node);
            final int opcode = instruction.opcode();
            result = new Opcode(
                opcode,
                instruction.operands()
                    .stream()
                    .map(XmlOperand::asObject)
                    .collect(Collectors.toList())
            );
        } else if ("label".equals(base)) {
            result = new Label(node);
        } else if ("int".equals(base)) {
            result = new Literal(node);
        } else if ("string".equals(base)) {
            result = new Literal(node);
        } else if ("long".equals(base)) {
            result = new Literal(node);
        } else if ("type".equals(base)) {
            result = new ClassName(node);
        } else if (".super".equals(base)) {
            result = new Super(node, this);
        } else if ("$".equals(base)) {
            result = new This(node);
        } else if ("static-field".equals(base)) {
            result = new ClassField(node);
        } else if (".write-array".equals(base)) {
            final List<XmlNode> inner = node.children().collect(Collectors.toList());
            final AstNode array = this.parse(inner.get(0));
            final AstNode index = this.parse(inner.get(1));
            final AstNode value = this.parse(inner.get(2));
            result = new StoreArray(array, index, value);
        } else if (".write-local-var".equals(base)) {
            final List<XmlNode> inner = node.children().collect(Collectors.toList());
            final AstNode target = this.parse(inner.get(0));
            final AstNode value = this.parse(inner.get(1));
            result = new VariableAssignment((LocalVariable) target, value);
        } else if (".get-field".equals(base)) {
            final List<XmlNode> inner = node.children().collect(Collectors.toList());
            final XmlNode field = inner.get(0);
            result = new FieldRetrieval(new Field(field, this));
        } else if (".write-field".equals(base)) {
            final List<XmlNode> inner = node.children().collect(Collectors.toList());
            final XmlNode field = inner.get(0);
            final AstNode value = this.parse(inner.get(1));
            result = new FieldAssignment(new Field(field, this), value);
        } else if (base.contains("local")) {
            result = new LocalVariable(node);
        } else if (".new".equals(base)) {
            result = new Constructor(node, this);
        } else if (".array-node".equals(base)) {
            final List<XmlNode> children = node.children().collect(Collectors.toList());
            final String type = new HexString(children.get(0).text()).decode();
            final AstNode size = this.parse(children.get(1));
            result = new ArrayConstructor(size, type);
        } else if (!base.isEmpty() && base.charAt(0) == '.') {
            final Attributes attributes = new Attributes(node);
            if ("static".equals(attributes.type())) {
                result = new StaticInvocation(node, new Arguments(node, this, 1).toList());
            } else if ("interface".equals(attributes.type())) {
                result = new InterfaceInvocation(node, this);
            } else {
                result = new Invocation(node, this);
            }
        } else {
            throw new IllegalArgumentException(
                String.format("Can't recognize node: %n%s%n", node)
            );
        }
        return result;
    }

    /**
     * Convert to XML nodes.
     *
     * @return XML nodes.
     */
    List<XmlNode> toJeoNodes() {
        return this.nodes.stream()
            .map(this::opcodes)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    /**
     * Convert XmlNode into a list of opcodes.
     *
     * @param node XmlNode
     * @return List of opcodes
     */
    private List<XmlNode> opcodes(final XmlNode node) {
        return this.parse(node).opcodes()
            .stream()
            .map(AstNode::toXmir)
            .map(Xembler::new)
            .map(Xembler::xmlQuietly)
            .map(XmlNode::new)
            .collect(Collectors.toList());
    }
}
