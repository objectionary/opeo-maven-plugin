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

import java.util.stream.Stream;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test case for {@link Attributes}.
 * @since 0.1
 */
final class AttributesTest {

    @ParameterizedTest(name = "Converts \"{1}\" attributes to a single string \"{0}\"")
    @CsvSource({
        "scope=foo, 'scope,foo,'",
        "descriptor=I|owner=Lorg/eolang/opeo/ast/Invocation;|type=field, 'descriptor,I,type,field,owner,Lorg/eolang/opeo/ast/Invocation;'",
        "descriptor=()V|owner=Lorg/eolang/opeo/ast/Invocation;|type=method, 'descriptor,()V,type,method,owner,Lorg/eolang/opeo/ast/Invocation;'"
    })
    void convertsToString(final String expected, final String attributes) {
        MatcherAssert.assertThat(
            "Can't convert to string",
            new Attributes(attributes.replace("'", "").split(",")).toString(),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void parsesRawString() {
        final Attributes actual = new Attributes(
            "descriptor=I|type=field|owner=Lorg/eolang/opeo/ast/Invocation;"
        );
        MatcherAssert.assertThat(
            "Can't parse descriptor from raw string",
            actual.descriptor(),
            Matchers.equalTo("I")
        );
        MatcherAssert.assertThat(
            "Can't parse type from raw string",
            actual.type(),
            Matchers.equalTo("field")
        );
        MatcherAssert.assertThat(
            "Can't parse owner from raw string",
            actual.owner(),
            Matchers.equalTo("Lorg/eolang/opeo/ast/Invocation;")
        );
    }

    @ParameterizedTest
    @MethodSource("xmirAttributes")
    void parsesXmir(final String xmir, final String expected) {
        final XmlNode node = new XmlNode(xmir);
        MatcherAssert.assertThat(
            String.format("Can't create correct attributes from node '%s'", node),
            new Attributes(node).toString(),
            Matchers.equalTo(expected)
        );
    }

    /**
     * Xmir representation of attribute objects.
     * @return Stream of arguments for {@link #parsesXmir(String, String)} test.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> xmirAttributes() {
        return Stream.of(
            Arguments.of(
                "<o base='string' data='bytes' line='999'>64 65 73 63 72 69 70 74 6F 72 3D 49 7C 74 79 70 65 3D 6C 6F 63 61 6C</o>",
                "descriptor=I|type=local"
            ),
            Arguments.of(
                "<o base='string' data='bytes' line='999'>64 65 73 63 72 69 70 74 6F 72 3D 49 7C 74 79 70 65 3D 6C 6F 63 61 6C   </o>",
                "descriptor=I|type=local"
            ),
            Arguments.of(
                "<o base='string' data='bytes' line='999'>64 65 73 63 72 69 70 74 6F 72 3D 49 7C 74 79 70 65 3D 6C 6F 63 61 6C\n\n\n</o>",
                "descriptor=I|type=local"
            ),
            Arguments.of("<o base='string' data='bytes' line='999'></o>", ""),
            Arguments.of("<o base='string' data='bytes' line='999'>\n\n\n</o>", ""),
            Arguments.of("<o base='string' data='bytes' line='999'> \n \n </o>", ""),
            Arguments.of("<o base='string' data='bytes' line='999'>\n</o>", "")
        );
    }
}
