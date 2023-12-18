package org.eolang.opeo.ast;

import java.util.UUID;

public class Literal implements AstNode {

    private final Object object;

    public Literal(final Object asis) {
        this.object = asis;
    }

    @Override
    public String print() {
        return object.toString();
    }

    @Override
    public String id() {
        return UUID.randomUUID().toString();
    }
}
