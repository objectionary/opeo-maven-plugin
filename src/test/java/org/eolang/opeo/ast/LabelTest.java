package org.eolang.opeo.ast;

import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test case for {@link Label}.
 * @since 0.2
 */
final class LabelTest {

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "The label should be converted to XMIR",
            new Xembler(new Label("foo").toXmir(), new Transformers.Node()).xml(),
            Matchers.equalTo("<o base=\"label\" data=\"bytes\">66 6F 6F</o>")
        );
    }

    @Test
    void parsesXml() throws ImpossibleModificationException {
        final String initial = "<o base=\"label\" data=\"bytes\">66 6F 6F</o>";
        MatcherAssert.assertThat(
            "The label should be able parse itself from XMIR and convert back to the same XMIR",
            new Xembler(new Label(new XmlNode(initial)).toXmir(), new Transformers.Node()).xml(),
            Matchers.equalTo(initial)
        );
    }

}