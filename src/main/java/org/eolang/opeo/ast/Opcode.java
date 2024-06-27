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
package org.eolang.opeo.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesInstruction;
import org.xembly.Directive;

/**
 * Opcode output node.
 * @since 0.1
 */
@ToString
@EqualsAndHashCode
public final class Opcode implements AstNode {

    /**
     * Opcodes counting.
     */
    private static final AtomicBoolean COUNTING = new AtomicBoolean(true);

    /**
     * Opcode.
     */
    private final int bytecode;

    /**
     * Opcode operands.
     */
    private final List<Object> operands;

    /**
     * Opcodes counting.
     * Do we add number to opcode name or not?
     * if true then we add number to opcode name:
     *   RETURN -> RETURN-1
     * if false then we do not add number to opcode name:
     *   RETURN -> RETURN
     */
    private final boolean counting;

    /**
     * Constructor.
     * @param opcode Opcode
     * @param operands Opcode operands
     */
    public Opcode(final int opcode, final Object... operands) {
        this(opcode, Arrays.asList(operands));
    }

    /**
     * Constructor.
     * @param opcode Opcode
     * @param counting Opcodes counting
     */
    public Opcode(final int opcode, final boolean counting) {
        this(opcode, Collections.emptyList(), counting);
    }

    /**
     * Constructor.
     * @param opcode Opcode
     * @param operands Opcode operands
     */
    public Opcode(final int opcode, final List<Object> operands) {
        this(opcode, operands, Opcode.COUNTING.get());
    }

    /**
     * Constructor.
     * @param bytecode Bytecode
     * @param operands Opcode operands
     * @param counting Opcodes counting
     */
    public Opcode(final int bytecode, final List<Object> operands, final boolean counting) {
        this.bytecode = bytecode;
        this.operands = new ArrayList<>(operands);
        this.counting = counting;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new DirectivesInstruction(this.bytecode, this.counting, this.operands.toArray());
    }

    @Override
    public List<AstNode> opcodes() {
        return Arrays.asList(this);
    }

    /**
     * Disable opcodes counting.
     * It is useful for tests.
     * @todo #65:30min Remove public static method 'disableCounting()' from Opcode.
     *  Currently it is used in tests. We should find another
     *  way to disable opcodes counting in tests. When we find
     *  the way to do it, we should remove this method.
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static void disableCounting() {
        Opcode.COUNTING.set(false);
    }
}
