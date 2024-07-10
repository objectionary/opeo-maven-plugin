package org.eolang.opeo.ast;

import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.directives.DirectivesData;
import org.eolang.jeo.representation.xmir.HexString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.xembly.Directive;
import org.xembly.Directives;

public final class Handle implements Xmir {

    private final int tag;
    private final String name;
    private final String owner;
    private final String desc;

    private final boolean itf;

    public Handle(final XmlNode root) {
        this(root, root.children().collect(Collectors.toList()));
    }

    public Handle(final XmlNode root, final List<XmlNode> children) {
        this(
            Handle.xtag(children),
            Handle.xname(root),
            Handle.xowner(root),
            Handle.xdesc(children),
            Handle.xitf(children)
        );
    }

    private static String xowner(final XmlNode root) {
        return root.attribute("base")
            .map(s -> s.substring(0, s.lastIndexOf('.')))
            .orElseThrow(() -> new IllegalArgumentException("Owner is required"));
    }

    private static String xname(final XmlNode root) {
        return root.attribute("base")
            .map(s -> s.substring(s.lastIndexOf('.')))
            .orElseThrow(() -> new IllegalArgumentException("Name is required"));
    }

    private static int xtag(final List<XmlNode> children) {
        return new HexString(children.get(0).text()).decodeAsInt();
    }

    private static String xdesc(final List<XmlNode> children) {
        return new HexString(children.get(1).text()).decode();
    }

    private static boolean xitf(final List<XmlNode> children) {
        return new HexString(children.get(2).text()).decodeAsBoolean();
    }

    public Handle(final org.objectweb.asm.Handle handle) {
        this(
            handle.getTag(),
            handle.getName(),
            handle.getOwner(),
            handle.getDesc(),
            handle.isInterface()
        );
    }

    public Handle(
        final int tag,
        final String name,
        final String owner,
        final String desc,
        final boolean itf
    ) {
        this.tag = tag;
        this.name = name;
        this.owner = owner;
        this.desc = desc;
        this.itf = itf;
    }

    public org.objectweb.asm.Handle toAsm() {
        return new org.objectweb.asm.Handle(this.tag, this.owner, this.name, this.desc, this.itf);
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives().add("o")
            .attr("base", String.format("%s.%s", this.owner.replace('/', '.'), this.name))
            .append(new DirectivesData("", this.tag))
            .append(new DirectivesData("", this.desc))
            .append(new DirectivesData("", this.itf))
            .up();
    }
}
