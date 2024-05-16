package org.eolang.opeo.storage;

import com.jcabi.log.Logger;
import java.util.stream.Stream;

public final class DummyStorage implements Storage {
    @Override
    public Stream<XmirEntry> all() {
        return Stream.empty();
    }

    @Override
    public void save(final XmirEntry xmir) {
        Logger.debug(this, "Dummy storage: skip %s", xmir);
    }
}
