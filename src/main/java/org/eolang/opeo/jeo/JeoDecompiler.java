package org.eolang.opeo.jeo;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.xmir.XmlMethod;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.jeo.representation.xmir.XmlProgram;
import org.eolang.opeo.Instruction;
import org.eolang.opeo.vmachine.DecompilerMachine;
import org.w3c.dom.Node;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Decompiler that gets jeo instructions and decompiles them into high-level EO constructs.
 * @since 0.1
 */
public class JeoDecompiler {

    /**
     * Program in XMIR format received from jeo maven plugin.
     */
    private final XML prog;

    /**
     * Constructor.
     * @param prog Program in XMIR format received from jeo maven plugin.
     */
    public JeoDecompiler(final XML prog) {
        this.prog = prog;
    }

    /**
     * Decompile program.
     * @return EO program.
     */
    public XML decompile() {
        final Node node = prog.node();
        final XmlProgram program = new XmlProgram(node);
        program.top()
            .methods()
            .stream()
            .forEach(this::decompile);
        return new XMLDocument(node);
    }

    /**
     * Decompile method.
     * @param method Method.
     */
    private void decompile(final XmlMethod method) {
        final DecompilerMachine machine = new DecompilerMachine();
        final Instruction[] instructions = new JeoInstructions(method).instructions();
        final List<XmlNode> collect = new XmlNode(
            new Xembler(
                machine.decompileToXmir(instructions),
                new Transformers.Node()
            ).xmlQuietly()
        ).children().collect(Collectors.toList());
        method.replaceInstructions(collect.toArray(XmlNode[]::new));
    }


}
