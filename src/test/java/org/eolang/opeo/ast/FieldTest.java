package org.eolang.opeo.ast;

import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.compilation.Parser;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

class FieldTest {

    private static final String XMIR = String.join(
        "\n",
        "<o base='.foo'>",
        "      <o base='string' data='bytes'>6E 61 6D 65 3D 66 6F 6F</o>",
        "   <o base='$'>",
        "      <o base='string' data='bytes'>64 65 73 63 72 69 70 74 6F 72 3D 6A 61 76 61 2E 6C 61 6E 67 2E 4F 62 6A 65 63 74</o>",
        "   </o>",
        "</o>"
    );

    private static final Parser PARSER = node -> new This(Type.getType(Object.class));


    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        final XMLDocument actual = new XMLDocument(
            new Xembler(
                new Field(
                    new This(),
                    new Attributes("name", "foo")
                ).toXmir()
            ).xml()
        );
        MatcherAssert.assertThat(
            String.format("Can't convert to correct XMIR, actual result is : %n%s%n", actual),
            actual,
            Matchers.equalTo(new XMLDocument(FieldTest.XMIR))
        );
    }

    @Test
    void createsFieldFromXmir() {
        MatcherAssert.assertThat(
            "Can't convert Field from XMIR",
            new Field(new XmlNode(FieldTest.XMIR), FieldTest.PARSER),
            Matchers.equalTo(new Field(new This(), new Attributes("name", "foo")))
        );
    }

}