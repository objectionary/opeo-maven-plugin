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

import com.jcabi.matchers.XhtmlMatchers;
import org.eolang.opeo.compilation.HasInstructions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link WriteField}.
 * @since 0.1
 */
class WriteFieldTest {

    @Test
    void prints() {
        MatcherAssert.assertThat(
            "Can't print 'write field' statement, should be 'this.a = 2' assignment",
            new WriteField(
                new InstanceField(new This(), "a"),
                new Literal(2)
            ).print(),
            Matchers.equalTo("this.a = 2")
        );
    }

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        final String xmir = new Xembler(
            new WriteField(
                new InstanceField(new This(), "bar"),
                new Literal(3)
            ).toXmir()
        ).xml();
        MatcherAssert.assertThat(
            String.format(
                "Can't convert 'this.bar = 3' statement to the correct xmir, result is wrong: %n%s%n",
                xmir
            ),
            xmir,
            XhtmlMatchers.hasXPaths(
                "./o[@base='.write']",
                "./o[@base='.write']/o[@base='.bar']",
                "./o[@base='.write']/o[@base='int' and contains(text(),'3')]"
            )
        );
    }

    @Test
    void transformsToOpcodes() {
        final String name = "d";
        final String owner = "test/Test";
        final String descriptor = "I";
        MatcherAssert.assertThat(
            "Can't transform 'this.a = 1' statement to the correct opcodes, result is wrong",
            new OpcodeNodes(
                new WriteField(
                    new This(),
                    new Literal(1),
                    new Attributes()
                        .name(name)
                        .owner(owner)
                        .descriptor(descriptor)
                )
            ).opcodes(),
            new HasInstructions(
                new HasInstructions.Instruction(Opcodes.ALOAD, 0),
                new HasInstructions.Instruction(Opcodes.ICONST_1),
                new HasInstructions.Instruction(Opcodes.PUTFIELD, owner, name, descriptor)
            )
        );
    }
}
