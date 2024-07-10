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
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.SameXml;
import org.eolang.opeo.compilation.Parser;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DynamicInvocation}.
 * @since 0.5
 */
final class DynamicInvocationTest {

    private final static String XMIR = String.join(
        "",
        "<o base='.run'>",
        "   <o base='string' data='bytes' line='306231171'>64 65 73 63 72 69 70 74 6F 72 3D 28 29 4C 6A 61 76 61 2F 6C 61 6E 67 2F 52 75 6E 6E 61 62 6C 65 3B 7C 74 79 70 65 3D 64 79 6E 61 6D 69 63</o>",
        "   <o base='java.lang.invoke.LambdaMetafactory.meafactory'>",
        "      <o base='int' data='bytes' line='74325437'>00 00 00 00 00 00 00 06</o>",
        "      <o base='string' data='bytes' line='981153864'>28 4C 6A 61 76 61 2F 6C 61 6E 67 2F 69 6E 76 6F 6B 65 2F 4D 65 74 68 6F 64 48 61 6E 64 6C 65 73 24 4C 6F 6F 6B 75 70 3B 4C 6A 61 76 61 2F 6C 61 6E 67 2F 53 74 72 69 6E 67 3B 4C 6A 61 76 61 2F 6C 61 6E 67 2F 69 6E 76 6F 6B 65 2F 4D 65 74 68 6F 64 54 79 70 65 3B 4C 6A 61 76 61 2F 6C 61 6E 67 2F 69 6E 76 6F 6B 65 2F 4D 65 74 68 6F 64 54 79 70 65 3B 4C 6A 61 76 61 2F 6C 61 6E 67 2F 69 6E 76 6F 6B 65 2F 4D 65 74 68 6F 64 48 61 6E 64 6C 65 3B 4C 6A 61 76 61 2F 6C 61 6E 67 2F 69 6E 76 6F 6B 65 2F 4D 65 74 68 6F 64 54 79 70 65 3B 29 4C 6A 61 76 61 2F 6C 61 6E 67 2F 69 6E 76 6F 6B 65 2F 43 61 6C 6C 53 69 74 65 3B</o>",
        "      <o base='bool' data='bytes' line='623874038'>00</o>",
        "   </o>",
        "   <o base='type' data='bytes' line='1809304796'>28 29 56</o>",
        "   <o base='org.eolang.streams.Lambda.lambda$run$0'>",
        "      <o base='int' data='bytes' line='631518845'>00 00 00 00 00 00 00 06</o>",
        "      <o base='string' data='bytes' line='1214403213'>28 29 56</o>",
        "      <o base='bool' data='bytes' line='1030902856'>00</o>",
        "   </o>",
        "   <o base='type' data='bytes' line='1901002995'>28 29 56</o>",
        "</o>"
    );

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't convert to dynamic invocation to correct XMIR",
            new Xembler(
                new DynamicInvocation(
                    "run",
                    new Handle(
                        6,
                        "meafactory",
                        "java/lang/invoke/LambdaMetafactory",
                        "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                        false
                    ),
                    "()Ljava/lang/Runnable;",
                    Arrays.asList(
                        Type.getType("()V"),
                        new Handle(
                            6,
                            "lambda$run$0",
                            "org/eolang/streams/Lambda",
                            "()V",
                            false
                        ),
                        Type.getType("()V")
                    )
                ).toXmir()
            ).xml(),
            new SameXml(DynamicInvocationTest.XMIR)
        );
    }

    @Test
    void createsDynamicInvocationFromXmir() {
        MatcherAssert.assertThat(
            "Can't convert dynamic invocation to opcodes",
            new DynamicInvocation(
                new XmlNode(DynamicInvocationTest.XMIR)
            ).opcodes(),
            Matchers.hasItem(
                new Opcode(
                    Opcodes.INVOKEDYNAMIC,
                    "run",
                    "()Ljava/lang/Runnable;",
                    new org.objectweb.asm.Handle(
                        6,
                        "java/lang/invoke/LambdaMetafactory",
                        "meafactory",
                        "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                        false
                    ),
                    "()Ljava/lang/Runnable;",
                    Type.getType("()V"),
                    new org.objectweb.asm.Handle(
                        6,
                        "org/eolang/streams/Lambda",
                        "lambda$run$0",
                        "()V",
                        false
                    ),
                    Type.getType("()V")
                )
            )
        );
    }
}