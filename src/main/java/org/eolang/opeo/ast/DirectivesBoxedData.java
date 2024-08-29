package org.eolang.opeo.ast;


import java.util.Iterator;
import java.util.Random;
import org.eolang.jeo.representation.HexData;
import org.xembly.Directive;
import org.xembly.Directives;

public final class DirectivesBoxedData implements Iterable<Directive> {

    private final Object data;

    public DirectivesBoxedData(final Object data) {
        this.data = data;
    }

    @Override
    public Iterator<Directive> iterator() {
        final HexData hex = new HexData(this.data);
        return new Directives().add("o")
            .attr("base", String.format("const-%s", hex.type()))
            .attr("data", "bytes")
            .attr("line", new Random().nextInt(Integer.MAX_VALUE))
            .set(hex.value()).up().iterator();
    }
}
