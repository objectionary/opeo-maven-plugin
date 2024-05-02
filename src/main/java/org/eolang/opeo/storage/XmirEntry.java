package org.eolang.opeo.storage;

import com.jcabi.xml.XML;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public final class XmirEntry {

    private final XML xml;
    private final String pckg;

    public XmirEntry(final XML xml, final String pckg) {
        this.xml = xml;
        this.pckg = pckg;
    }

    public XmirEntry transform(Function<? super XML, ? extends XML> transformer) {
        return new XmirEntry(transformer.apply(this.xml), this.pckg);
    }

    XML xml() {
        return this.xml;
    }

    public String pckg() {
        return this.pckg;
    }
}
