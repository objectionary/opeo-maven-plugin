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

import org.eolang.jeo.matchers.SameXml;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.compilation.Parser;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link Return}.
 * @since 0.5
 */
final class ReturnTest {

    /**
     * Parser for all unit tests in this class.
     */
    private static final Parser PARSER = node -> {
        final String base = node.attribute("base").orElseThrow(
            () -> new IllegalArgumentException("No base attribute")
        );
        final AstNode result;
        if (base.startsWith("int")) {
            result = new Constant(42);
        } else if (base.startsWith("string")) {
            result = new Constant("foo");
        } else {
            throw new IllegalArgumentException(String.format("Unknown base: %s", base));
        }
        return result;
    };

    @Test
    void convertsSimpleReturnToXml() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't convert simple return to XML",
            new Xembler(new Return().toXmir()).xml(),
            new SameXml("<o base='return'/>")
        );
    }

    @Test
    void convertsIntReturnToXml() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't convert typed return with int to XML",
            new Xembler(new Return(new Constant(42)).toXmir()).xml(),
            new SameXml(
                "<o base='return'><o base='int' data='bytes'>00 00 00 00 00 00 00 2A</o></o>"
            )
        );
    }

    @Test
    void convertsStringReturnToXml() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't convert typed return with string to XML",
            new Xembler(new Return(new Constant("foo")).toXmir()).xml(),
            new SameXml(
                "<o base='return'><o base='string' data='bytes'>66 6F 6F</o></o>"
            )
        );
    }

    @Test
    void createsSimpleReturnFromXmir() {
        MatcherAssert.assertThat(
            "Can't create simple return",
            new Return(new XmlNode("<o base='return'/>"), ReturnTest.PARSER).opcodes(),
            Matchers.hasItem(new Opcode(Opcodes.RETURN))
        );
    }

    @Test
    void createsIntReturnFromXmir() {
        MatcherAssert.assertThat(
            "Can't create typed return with int",
            new Return(
                new XmlNode(
                    "<o base='return'><o base='int' data='bytes'>00 00 00 00 00 00 00 2A</o></o>"
                ),
                ReturnTest.PARSER
            ).opcodes(),
            Matchers.hasItems(
                new Opcode(Opcodes.BIPUSH, 42),
                new Opcode(Opcodes.IRETURN)
            )
        );
    }

    @Test
    void createsStringReturnFromXmir() {
        MatcherAssert.assertThat(
            "Can't create typed return with string",
            new Return(
                new XmlNode(
                    "<o base='return'><o base='string' data='bytes'>66 6F 6F</o></o>"
                ),
                ReturnTest.PARSER
            ).opcodes(),
            Matchers.hasItems(
                new Opcode(Opcodes.LDC, "foo"),
                new Opcode(Opcodes.ARETURN)
            )
        );
    }
}
