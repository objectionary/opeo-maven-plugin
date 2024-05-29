package org.eolang.opeo.ast;

import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

class LabeledTest {

    /**
     * Example of XMIR representation of labeled constant.
     */
    private static final String XMIR = "<o base='labeled'><o base='load-constant'><o base='int' data='bytes'>00 00 00 00 00 00 00 01</o></o><o base='label' data='bytes'>01</o></o>";

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            new XMLDocument(
                new Xembler(
                    new Labeled(new Constant(1), new Label("1")).toXmir()
                ).xml()
            ),
            Matchers.equalTo(new XMLDocument(LabeledTest.XMIR))
        );
    }

    @Test
    void convertsFromXmir() {
        MatcherAssert.assertThat(
            new Labeled(new XmlNode(LabeledTest.XMIR), node -> new Constant(1)),
            Matchers.equalTo(
                new Labeled(new Constant(1), new Label("1"))
            )
        );
    }

    @Test
    void convertsToOpcodes() {
        final Label label = new Label("1");
        MatcherAssert.assertThat(
            new Labeled(new Constant(1), label).opcodes(),
            Matchers.contains(
                new Opcode(Opcodes.LDC, 1),
                label
            )
        );
    }

}