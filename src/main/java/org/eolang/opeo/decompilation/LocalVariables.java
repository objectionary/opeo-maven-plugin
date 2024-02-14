/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.opeo.decompilation;

import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.LocalVariable;
import org.eolang.opeo.ast.This;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Local variables.
 * @since 0.1
 */
public final class LocalVariables {

    /**
     * Method access modifiers.
     */
    private final int modifiers;

    /**
     * Constructor.
     */
    public LocalVariables() {
        this(Opcodes.ACC_PUBLIC);
    }

    /**
     * Constructor.
     * @param modifiers Method access modifiers.
     */
    public LocalVariables(final int modifiers) {
        this.modifiers = modifiers;
    }

    /**
     * Get variable by index.
     * @param index Index.
     * @param type Type.
     * @param load Load or store.
     * @return Variable.
     */
//    public AstNode variable(final int index, final Type type, final boolean load) {
//        final AstNode result;
//        if (index == 0 && (this.modifiers & Opcodes.ACC_STATIC) == 0) {
//            result = new This();
//        } else if (load) {
//            result = new Variable(type, Variable.Operation.LOAD, index);
//        } else {
//            result = new Variable(type, Variable.Operation.STORE, index);
//        }
//        return result;
//    }

    /**
     * Get variable by index.
     * @param index Index.
     * @param type Type.
     * @return Variable.
     */
    public AstNode variable(final int index, final Type type) {
        final AstNode result;
        if (index == 0 && (this.modifiers & Opcodes.ACC_STATIC) == 0) {
            result = new This();
        } else {
            result = new LocalVariable(index, type);
        }
        return result;
    }
}
