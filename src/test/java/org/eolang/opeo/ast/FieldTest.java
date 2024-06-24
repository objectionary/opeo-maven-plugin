package org.eolang.opeo.ast;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.xembly.Directive;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

class FieldTest {


    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        final Iterable<Directive> dirs = new Field(
            new This(),
            new Attributes("name", "foo")
        ).toXmir();
        System.out.println(new Xembler(dirs).xml());
    }

}