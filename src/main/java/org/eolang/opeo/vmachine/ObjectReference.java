package org.eolang.opeo.vmachine;

import java.util.concurrent.atomic.AtomicInteger;

public class ObjectReference {

    private static final AtomicInteger GLOBAL = new AtomicInteger();

    private final String type;
    private final AtomicInteger counter;

    public ObjectReference(final String type) {
        this(type, ObjectReference.GLOBAL);
    }

    private ObjectReference(final String type, final AtomicInteger counter) {
        this.type = type;
        this.counter = counter;
    }

    @Override
    public String toString() {
        return String.format("%s%d%s",
            "&",
            this.counter.getAndIncrement(),
            this.type.replace('/', '.')
        );
    }
}
