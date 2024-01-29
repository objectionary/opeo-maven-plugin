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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Abstract syntax tree node.
 * @since 0.1
 */
public interface AstNode {

    /**
     * Print ast node and all it's children.
     * @return String output.
     */
    String print();

    /**
     * Convert node to XMIR.
     * @return XMIR XML.
     */
    Iterable<Directive> toXmir();

    /**
     * Bytecode instructions.
     * @return List of opcodes.
     */
    List<AstNode> opcodes();

    /**
     * Sequence of nodes.
     * @since 0.1
     */
    final class Sequence implements AstNode {

        /**
         * Nodes.
         */
        private final List<AstNode> nodes;

        /**
         * Useful constructor.
         * @param nodes Nodes
         * @param another Additional nodes
         */
        public Sequence(final List<AstNode> nodes, final AstNode... another) {
            this(
                Stream.concat(nodes.stream(), Arrays.stream(another)).collect(Collectors.toList())
            );
        }

        /**
         * Constructor.
         * @param nodes Nodes.
         */
        public Sequence(final List<AstNode> nodes) {
            this.nodes = nodes;
        }

        @Override
        public String print() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Iterable<Directive> toXmir() {
            final Directives directives = new Directives();
            for (final AstNode node : this.nodes) {
                directives.append(node.toXmir());
            }
            return directives;
        }

        @Override
        public List<AstNode> opcodes() {
            final List<AstNode> res = new ArrayList<>(0);
            for (final AstNode node : this.nodes) {
                res.addAll(node.opcodes());
            }
            return res;
        }
    }
}
