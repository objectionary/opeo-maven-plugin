package org.eolang.opeo.ast;

import com.jcabi.xml.XMLDocument;
import java.util.Collections;
import java.util.List;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.w3c.dom.Node;
import org.xembly.Directive;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

public final class RawXml implements AstNode {

    private final XmlNode node;

    public RawXml(final XmlNode node) {
        this.node = node;
    }

    @Override
    public List<AstNode> opcodes() {
        return Collections.singletonList(this);
    }

    @Override
    public Iterable<Directive> toXmir() {
        //todo!
        return new Directives().append(new XMLDocument(
            new XMLDocument(this.node.node()).toString()).node());
    }
}
