package org.eolang.opeo.ast;

import org.eolang.jeo.representation.xmir.XmlNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

class NewAddressTest {

    private static final String XML = String.join(
        "\n",
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
        "<o base=\".new-type\">",
        "   <o base=\"string\" data=\"bytes\">53 6F 6D 65 54 79 70 65</o>",
        "</o>",
        ""
    );
    private static final String TYPE = "SomeType";

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect, that new address will be successfully converted to XMIR",
            new Xembler(new NewAddress(NewAddressTest.TYPE).toXmir()).xml(),
            Matchers.equalTo(NewAddressTest.XML)
        );
    }

    @Test
    void createsFromXmir() {
        MatcherAssert.assertThat(
            "We expect, that new address will be successfully created from XMIR",
            new NewAddress(new XmlNode(NewAddressTest.XML)),
            Matchers.equalTo(new NewAddress(NewAddressTest.TYPE))
        );
    }

    @Test
    void convertsToOpcodes() {
        MatcherAssert.assertThat(
            "We expect, that new address will be successfully converted to opcodes",
            new NewAddress(NewAddressTest.TYPE).opcodes(),
            Matchers.contains(
                new Opcode(Opcodes.NEW, NewAddressTest.TYPE)
            )
        );
    }

}