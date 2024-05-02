package org.eolang.opeo.storage;

import java.util.Collection;

public interface Storage {

    Collection<XmirEntry> all();

    void save(final XmirEntry xml);


}
