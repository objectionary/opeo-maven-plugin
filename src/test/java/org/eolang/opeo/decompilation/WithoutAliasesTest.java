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
package org.eolang.opeo.decompilation;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Test cases for {@link WithoutAliases}.
 *
 * @since 0.4
 */
final class WithoutAliasesTest {

    @Test
    void removesAllAliases() {
        MatcherAssert.assertThat(
            "We expect that all aliases are removed since the program doesn't have labels or opcodes",
            new WithoutAliases(WithoutAliasesTest.program()).toXml(),
            Matchers.not(XhtmlMatchers.hasXPath("/program/metas/meta[head='alias']"))
        );
    }

    @Test
    void removesOnlyLabelAlias() {
        MatcherAssert.assertThat(
            "We expect that only the label alias is removed since the program has an object",
            new WithoutAliases(
                WithoutAliasesTest.program(WithoutAliasesTest.object("opcode"))
            ).toXml(),
            Matchers.allOf(
                Matchers.not(
                    XhtmlMatchers.hasXPath(
                        "/program/metas/meta[head='alias' and tail='org.eolang.jeo.label']"
                    )
                ),
                XhtmlMatchers.hasXPaths(
                    "/program/metas/meta[head='alias' and tail='org.eolang.jeo.opcode']"
                )
            )
        );
    }

    @Test
    void removesOnlyOpcodeAlias() {
        MatcherAssert.assertThat(
            "We expect that only the opcode alias is removed since the program has a label",
            new WithoutAliases(
                WithoutAliasesTest.program(WithoutAliasesTest.object("label"))
            ).toXml(),
            Matchers.allOf(
                Matchers.not(
                    XhtmlMatchers.hasXPath(
                        "/program/metas/meta[head='alias' and tail='org.eolang.jeo.opcode']"
                    )
                ),
                XhtmlMatchers.hasXPaths(
                    "/program/metas/meta[head='alias' and tail='org.eolang.jeo.label']"
                )
            )
        );
    }

    @Test
    void keepsAllAliases() {
        MatcherAssert.assertThat(
            "We expect that all aliases are kept since the program has both labels and opcodes",
            new WithoutAliases(
                WithoutAliasesTest.program(
                    WithoutAliasesTest.object("label"),
                    WithoutAliasesTest.object("opcode")
                )
            ).toXml(),
            XhtmlMatchers.hasXPaths(
                "/program/metas/meta[head='alias' and tail='org.eolang.jeo.label']",
                "/program/metas/meta[head='alias' and tail='org.eolang.jeo.opcode']"
            )
        );
    }

    /**
     * A simple XMIR program that contains aliases and some objects.
     * @param objects Objects to add to the program.
     * @return XMIR program.
     */
    private static XML program(final Directives... objects) {
        return new XMLDocument(
            new Xembler(
                new Directives()
                    .add("program")
                    .add("metas")
                    .append(WithoutAliasesTest.alias("org.eolang.jeo.label"))
                    .append(WithoutAliasesTest.alias("org.eolang.jeo.opcode"))
                    .up()
                    .add("objects")
                    .append(Arrays.stream(objects).reduce(new Directives(), Directives::append))
                    .up()
                    .up()
            ).xmlQuietly()
        );
    }

    /**
     * Alias directive.
     * @param name Alias name.
     * @return Alias directive.
     */
    private static Directives alias(final String name) {
        return new Directives()
            .add("meta")
            .add("head").set("alias").up()
            .add("tail").set(name).up()
            .add("part").set(name).up()
            .up();
    }

    /**
     * Object directive.
     * @param base Base of the object.
     * @return Object directive.
     */
    private static Directives object(final String base) {
        return new Directives().add("o").attr("base", base).up();
    }

}
