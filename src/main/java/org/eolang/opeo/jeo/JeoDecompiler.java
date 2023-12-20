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
import org.xembly.Directive;
import org.xembly.Transformers;
import org.xembly.Xembler;

public class JeoDecompiler {

    private final DecompilerMachine machine;

    public JeoDecompiler() {
        this(new DecompilerMachine());
    }

    public JeoDecompiler(final DecompilerMachine machine) {
        this.machine = machine;
    }

    public void decompile(final XML prog) {
        new XmlProgram(prog).top()
            .methods()
            .stream()
            .forEach(this::decompile);
    }

    private void decompile(final XmlMethod method) {
        final Instruction[] instructions = new JeoInstructions(method).instructions();
        final Iterable<Directive> directives = this.machine.decompileToXmir(
            instructions);
        final List<XmlNode> collect = new XmlNode(
            new Xembler(directives, new Transformers.Node()).xmlQuietly()).children()
            .collect(Collectors.toList());
        method.replaceInstructions(collect.toArray(XmlNode[]::new));
        System.out.println(method);
        System.out.println(method);
    }


}
