package org.eolang.opeo.ast;

import java.util.UUID;

public class Keyword implements AstNode {

    private final String word;

    public Keyword(final String word) {
        this.word = word;
    }

    @Override
    public String print() {
        return this.word;
    }

    @Override
    public String id() {
        return UUID.randomUUID().toString();
    }
}
