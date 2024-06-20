package org.eolang.opeo.ast;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.compilation.Parser;

public final class Arguments {

    private final XmlNode root;
    private final Parser parser;
    private final int begin;

    public Arguments(final XmlNode root, final Parser parser, final int begin) {
        this.root = root;
        this.parser = parser;
        this.begin = begin;
    }

    public List<AstNode> toList() {
        final List<XmlNode> all = this.root.children().collect(Collectors.toList());
        final List<AstNode> arguments;
        if (all.size() > this.begin) {
            arguments = all.subList(this.begin, all.size())
                .stream()
                .map(this.parser::parse)
                .collect(Collectors.toList());
        } else {
            arguments = Collections.emptyList();
        }
        return arguments;
    }

}
