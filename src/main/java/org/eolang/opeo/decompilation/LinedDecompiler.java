package org.eolang.opeo.decompilation;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.nio.file.Path;
import org.eolang.opeo.storage.FileStorage;
import org.eolang.opeo.storage.Storage;
import org.eolang.opeo.storage.XmirEntry;
import org.xembly.Directives;
import org.xembly.Xembler;

public final class LinedDecompiler implements Decompiler {

    private final Decompiler original;
    private final Storage modified;

    public LinedDecompiler(final Decompiler original, final Path modified) {
        this(original, new FileStorage(modified, modified));
    }

    private LinedDecompiler(final Decompiler original, final Storage modified) {
        this.original = original;
        this.modified = modified;
    }

    @Override
    public void decompile() {
        this.original.decompile();
        this.modified.all().map(this::withLines).forEach(this.modified::save);
    }

    private XmirEntry withLines(final XmirEntry entry) {
        Logger.info(this, "Adding lines to %s", entry.relative());
        return entry.transform(LinedDecompiler::withLines);
    }

    private static XML withLines(final XML input) {
        final Directives lines = new Directives()
            .xpath("//o[@name='descriptor' or @name='visible']")
            .attr("line", "999");
        final Directives maxs = new Directives()
            .xpath("//o[@name='maxs']")
            .attr("abstract", "");
        return new XMLDocument(
            new Xembler(maxs).applyQuietly(new Xembler(lines).applyQuietly(input.node()))
        );
    }
}
