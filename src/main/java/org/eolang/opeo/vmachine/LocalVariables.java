package org.eolang.opeo.vmachine;

import java.util.ArrayList;
import java.util.List;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.This;
import org.eolang.opeo.ast.Variable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Local variables.
 * @since 0.1
 */
public final class LocalVariables {

    /**
     * Local variables as ast nodes.
     */
    private List<AstNode> variables;

    /**
     * Constructor.
     * @param modifiers Method access modifiers.
     * @param descriptor Method descriptor.
     */
    public LocalVariables(final int modifiers, final String descriptor) {
        this(LocalVariables.fromMethod(modifiers, descriptor));
    }

    public LocalVariables() {
        this(List.of(new This()));
    }

    public LocalVariables(final List<AstNode> variables) {
        this.variables = variables;
    }

    public AstNode variable(final int index) {
        return this.variables.get(index);
    }

    /**
     * Size of local variables.
     * Important for tests.
     * @return Size.
     */
    int size() {
        return this.variables.size();
    }

    /**
     * Create local variables from method description.
     * @param modifiers Method access modifiers.
     * @param descriptor Method descriptor.
     * @return Local variables.
     */
    private static List<AstNode> fromMethod(final int modifiers, final String descriptor) {
        final Type[] args = Type.getArgumentTypes(descriptor);
        final int size = args.length;
        final List<AstNode> res = new ArrayList<>(size);
        if ((modifiers & Opcodes.ACC_STATIC) == 0) {
            res.add(new This());
        }
        for (int index = 0; index < size; index++) {
            res.add(new Variable(args[index], index));
        }
        return res;
    }

}
