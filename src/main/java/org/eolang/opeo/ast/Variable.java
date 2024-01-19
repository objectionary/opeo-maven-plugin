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
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * A variable.
 * @since 0.1
 */
@ToString
@EqualsAndHashCode
public final class Variable implements AstNode {

    /**
     * The prefix of the variable.
     */
    private static final String PREFIX = "llocal";

    /**
     * The type of the variable.
     */
    private final Type type;

    /**
     * The operation of the variable.
     */
    private final Operation operation;

    /**
     * The identifier of the variable.
     */
    private final int identifier;

    /**
     * Constructor.
     * @param node The XML node that represents variable.
     */
    public Variable(final XmlNode node) {
        this(Variable.vtype(node), Variable.voperation(node), Variable.videntifier(node));
    }

    public Variable(final Type type, final int identifier) {
        this(type, Operation.LOAD, identifier);
    }

    /**
     * Constructor.
     * @param type The type of the variable.
     * @param identifier The identifier of the variable.
     */
    public Variable(
        final Type type,
        final Operation operation,
        final int identifier
    ) {
        this.type = type;
        this.operation = operation;
        this.identifier = identifier;
    }

    @ToString.Include
    @Override
    public String print() {
        return String.format("%s%d%s", Variable.PREFIX, this.identifier, this.type.getClassName());
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives()
            .add("o")
            .attr("base", String.format("%s%d", Variable.PREFIX, this.identifier))
            .attr("scope", this.type.getDescriptor())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> result;
        if (this.operation.equals(Operation.LOAD)) {
            result = this.load();
        } else {
            result = this.store();
        }
        return result;
    }

    private List<AstNode> load() {
        final List<AstNode> result;
        if (this.type.equals(Type.INT_TYPE)) {
            result = List.of(new Opcode(Opcodes.ILOAD, this.identifier));
        } else if (this.type.equals(Type.DOUBLE_TYPE)) {
            result = List.of(new Opcode(Opcodes.DLOAD, this.identifier));
        } else if (this.type.equals(Type.LONG_TYPE)) {
            result = List.of(new Opcode(Opcodes.LLOAD, this.identifier));
        } else if (this.type.equals(Type.FLOAT_TYPE)) {
            result = List.of(new Opcode(Opcodes.FLOAD, this.identifier));
        } else if (this.type.equals(Type.BOOLEAN_TYPE)) {
            result = List.of(new Opcode(Opcodes.ILOAD, this.identifier));
        } else if (this.type.equals(Type.CHAR_TYPE)) {
            result = List.of(new Opcode(Opcodes.ILOAD, this.identifier));
        } else if (this.type.equals(Type.BYTE_TYPE)) {
            result = List.of(new Opcode(Opcodes.ILOAD, this.identifier));
        } else if (this.type.equals(Type.SHORT_TYPE)) {
            result = List.of(new Opcode(Opcodes.ILOAD, this.identifier));
        } else {
            result = List.of(new Opcode(Opcodes.ALOAD, this.identifier));
        }
        return result;
    }

    private List<AstNode> store() {
        final List<AstNode> result;
        if (this.type.equals(Type.INT_TYPE)) {
            result = List.of(new Opcode(Opcodes.ISTORE, this.identifier));
        } else if (this.type.equals(Type.DOUBLE_TYPE)) {
            result = List.of(new Opcode(Opcodes.DSTORE, this.identifier));
        } else if (this.type.equals(Type.LONG_TYPE)) {
            result = List.of(new Opcode(Opcodes.LSTORE, this.identifier));
        } else if (this.type.equals(Type.FLOAT_TYPE)) {
            result = List.of(new Opcode(Opcodes.FSTORE, this.identifier));
        } else if (this.type.equals(Type.BOOLEAN_TYPE)) {
            result = List.of(new Opcode(Opcodes.ISTORE, this.identifier));
        } else if (this.type.equals(Type.CHAR_TYPE)) {
            result = List.of(new Opcode(Opcodes.ISTORE, this.identifier));
        } else if (this.type.equals(Type.BYTE_TYPE)) {
            result = List.of(new Opcode(Opcodes.ISTORE, this.identifier));
        } else if (this.type.equals(Type.SHORT_TYPE)) {
            result = List.of(new Opcode(Opcodes.ISTORE, this.identifier));
        } else if (this.type.equals(Type.VOID_TYPE)) {
            result = List.of(new Opcode(Opcodes.ISTORE, this.identifier));
        } else {
            result = List.of(new Opcode(Opcodes.ASTORE, this.identifier));
        }
        return result;
    }

    /**
     * Get the identifier of the variable.
     * @param node The XML node that represents variable.
     * @return The identifier.
     */
    private static int videntifier(final XmlNode node) {
        return Integer.parseInt(
            node.attribute("base").orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "Can't recognize variable node: %n%s%nWe expected to find 'base' attribute",
                        node
                    )
                )
            ).substring(Variable.PREFIX.length())
        );
    }

    /**
     * Get the type of the variable.
     * @param node The XML node that represents variable.
     * @return The type.
     */
    private static Type vtype(final XmlNode node) {
        return Type.getType(node.attribute("scope")
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "Can't recognize variable node: %n%s%nWe expected to find 'scope' attribute",
                        node
                    )
                )
            )
        );
    }

    private static Operation voperation(final XmlNode node) {
        throw new UnsupportedOperationException("Not implemented yet");
    }


    public enum Operation {
        LOAD,
        STORE;
    }
}
