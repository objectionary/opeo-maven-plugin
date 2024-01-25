package org.eolang.opeo.ast;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Substraction output node.
 * @since 0.1
 * @todo #122:30min Add unit tests for Substraction class.
 *  Add unit tests for Substraction class.
 *  We have to test the both transformations: toXmir and opcodes.
 *  Don't forget to remove this puzzle after adding the tests.
 */
public final class Substraction implements AstNode {

    private final AstNode left;
    private final AstNode right;

    public Substraction(final AstNode left, final AstNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String print() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives().add("o")
            .attr("base", ".minus")
            .append(this.left.toXmir())
            .append(this.right.toXmir())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(0);
        res.addAll(this.left.opcodes());
        res.addAll(this.right.opcodes());
        res.add(new Opcode(Opcodes.IADD));
        return res;
    }
}
