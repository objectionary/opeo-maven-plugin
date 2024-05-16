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

import com.jcabi.xml.XMLDocument;
import java.util.Collections;
import java.util.List;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * This class represents a raw XML node.
 * It is used to represent nodes that we don't know how to parse yet.
 *
 * @since 0.1
 */
public final class RawXml implements AstNode {

    /**
     * XML node.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param node XML node.
     */
    public RawXml(final XmlNode node) {
        this.node = node;
    }

    @Override
    public List<AstNode> opcodes() {
        //@checkstyle MethodBodyCommentsCheck (10 lines)
        // @todo #226:90min Refactor AstNode#opcodes() Method Usage.
        //  This method is used improperly in the codebase since we have such strange
        //  implementations as in the RawXml class. We need to refactor the codebase
        //  and maybe just remove this method and use only `toXmir`.
        //  So, we need to investigate this.
        return Collections.singletonList(this);
    }

    @Override
    public Iterable<Directive> toXmir() {
        //@checkstyle MethodBodyCommentsCheck (10 line)
        // @todo #226:90min Inefficient RawXml#toXmir() Method Implementation.
        //  This method is inefficient because it creates a new XMLDocument
        //  instance and then converts it to a string. We need to refactor this
        //  method to be more efficient.
        return new Directives().append(
            new XMLDocument(
                new XMLDocument(this.node.node()).toString()).node()
        );
    }
}
