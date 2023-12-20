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
import org.xembly.Directive;
import org.xembly.Transformers;
import org.xembly.Xembler;

public class JeoDecompiler {

    private final XML prog;

    public JeoDecompiler(final XML prog) {
        this.prog = prog;
    }

    public XML decompile() {
        final Node node = prog.node();
        final XmlProgram program = new XmlProgram(node);
        program.top()
            .methods()
            .stream()
            .forEach(this::decompile);
        return new XMLDocument(node);
    }

    private void decompile(final XmlMethod method) {
        DecompilerMachine machine = new DecompilerMachine();
        final Instruction[] instructions = new JeoInstructions(method).instructions();
        final Iterable<Directive> directives = machine.decompileToXmir(
            instructions
        );
        final List<XmlNode> collect = new XmlNode(
            new Xembler(directives, new Transformers.Node()).xmlQuietly()).children()
            .collect(Collectors.toList());
        method.replaceInstructions(collect.toArray(XmlNode[]::new));
    }


}
