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
import java.util.Arrays;
import java.util.List;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Super output node.
 * @since 0.1
 * @todo #201:90min Add Attributes to the Super class.
 *  The Super class should have attributes that can be used to define the
 *  'descriptor' and 'intefaced' operands of the INVOKESPECIAL opcode.
 *  Moreover, we use hardcoded value for 'interfaced' which is dangerous.
 */
public final class Super implements AstNode {

    /**
     * Super instance.
     */
    private final AstNode instance;

    /**
     * Super arguments.
     */
    private final List<AstNode> arguments;

    /**
     * Attributes.
     */
    private final Attributes attributes;

    /**
     * Constructor.
     * @param instance Super instance
     * @param arguments Super arguments
     */
    public Super(final AstNode instance, final AstNode... arguments) {
        this(instance, Arrays.asList(arguments));
    }

    /**
     * Constructor.
     * @param instance Super instance
     * @param descriptor Descriptor
     * @param arguments Super arguments
     */
    public Super(final AstNode instance, final String descriptor, final AstNode... arguments) {
        this(instance, Arrays.asList(arguments), descriptor);
    }

    /**
     * Constructor.
     * @param instance Super instance
     * @param arguments Super arguments
     */
    public Super(final AstNode instance, final List<AstNode> arguments) {
        this(instance, arguments, "()V");
    }

    /**
     * Constructor.
     * @param instance Target instance
     * @param arguments Super arguments
     * @param descriptor Descriptor
     */
    public Super(
        final AstNode instance,
        final List<AstNode> arguments,
        final String descriptor
    ) {
        this(instance, arguments, descriptor, "java/lang/Object", "<init>");
    }

    /**
     * Constructor.
     * @param instance Target instance
     * @param arguments Super arguments
     * @param descriptor Descriptor
     * @param type Type
     * @param name Method Name
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public Super(
        final AstNode instance,
        final List<AstNode> arguments,
        final String descriptor,
        final String type,
        final String name
    ) {
        this(instance, arguments, new Attributes().descriptor(descriptor).name(name).owner(type));
    }

    /**
     * Constructor.
     * @param instance Target instance
     * @param arguments Super arguments
     * @param attributes Attributes
     */
    public Super(
        final AstNode instance,
        final List<AstNode> arguments,
        final Attributes attributes
    ) {
        this.instance = instance;
        this.arguments = arguments;
        this.attributes = attributes;
    }

    @Override
    public Iterable<Directive> toXmir() {
        final Directives directives = new Directives();
        directives.add("o")
            .attr("base", ".super")
            .attr("scope", this.attributes)
            .append(this.instance.toXmir());
        this.arguments.stream().map(AstNode::toXmir).forEach(directives::append);
        return directives.up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(2);
        res.addAll(this.instance.opcodes());
        this.arguments.stream().map(AstNode::opcodes).forEach(res::addAll);
        res.add(
            new Opcode(
                Opcodes.INVOKESPECIAL,
                this.attributes.owner(),
                this.attributes.name(),
                this.attributes.descriptor(),
                false
            )
        );
        return res;
    }
}
