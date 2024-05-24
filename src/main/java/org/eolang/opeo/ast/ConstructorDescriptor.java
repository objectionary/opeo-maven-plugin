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

import java.util.Arrays;
import java.util.List;
import org.objectweb.asm.Type;

/**
 * Constructor descriptor.
 * @since 0.2
 */
public final class ConstructorDescriptor {

    /**
     * Original descriptor.
     */
    private final String descriptor;

    /**
     * Constructor arguments.
     */
    private final List<AstNode> args;

    public ConstructorDescriptor(final List<AstNode> args) {
        this("", args);
    }

    /**
     * Constructor.
     * @param arguments Constructor arguments.
     */
    public ConstructorDescriptor(final String descriptor, final List<AstNode> arguments) {
        this.descriptor = descriptor;
        this.args = arguments;
    }

    @Override
    public String toString() {
        return this.combine();
    }

    /**
     * TODO: explain
     * @return
     */
    private String combine() {
//        return Type.getMethodDescriptor(
//            Type.VOID_TYPE,
//            this.args.stream()
//                .peek(this::verify)
//                .map(Typed.class::cast)
//                .map(Typed::type)
//                .toArray(Type[]::new)
//        );


        if (this.descriptor.contains("org/eolang")
            || this.descriptor.contains("org.eolang")
            || this.descriptor.isEmpty()) {
            return Type.getMethodDescriptor(
                Type.VOID_TYPE,
                this.argumentTypes()
            );
        } else {
            return this.descriptor;
        }
    }


    private Type[] argumentTypes() {
        return this.args.stream()
            .peek(this::verify)
            .map(Typed.class::cast)
            .map(Typed::type)
            .toArray(Type[]::new);
    }

    /**
     * Verify that the node is typed.
     * @param node Node to verify.
     */
    private void verify(final AstNode node) {
        if (!(node instanceof Typed)) {
            throw new IllegalArgumentException(
                String.format(
                    "Node %s is not typed, all constructor arguments must be typed: %s. Most probably, you forgot to add a type to the AstNode which you are tying to pass to a constructor.",
                    node,
                    Arrays.toString(this.args.toArray())
                )
            );
        }
    }
}
