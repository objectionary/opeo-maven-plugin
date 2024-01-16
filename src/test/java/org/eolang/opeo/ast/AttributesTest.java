package org.eolang.opeo.ast;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AttributesTest {

    @ParameterizedTest(name = "Converts \"{1}\" attributes to a single string \"{0}\"")
    @CsvSource({
        "scope=foo, 'scope,foo,'",
        "descriptor=I|type=field|owner=Lorg/eolang/opeo/ast/Invocation;, 'descriptor,I,type,field,owner,Lorg/eolang/opeo/ast/Invocation;'",
        "descriptor=()V|type=method|owner=Lorg/eolang/opeo/ast/Invocation;, 'descriptor,()V,type,method,owner,Lorg/eolang/opeo/ast/Invocation;'",
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
            "descriptor=I|type=field|owner=Lorg/eolang/opeo/ast/Invocation;");
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


}