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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import org.cactoos.list.ListOf;
import org.eolang.opeo.Instruction;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.ast.Root;
import org.eolang.opeo.decompilation.agents.AllAgents;
import org.xembly.Directive;

/**
 * Decompiler machine.
 *
 * @since 0.1
 */
public final class DecompilerMachine {

    /**
     * Local variables.
     */
    private final LocalVariables locals;

    /**
     * Handler that redirects instructions.
     */
    private final AllAgents agents;

    /**
     * Constructor.
     */
    DecompilerMachine() {
        this(new HashMap<>(0));
    }

    /**
     * Constructor.
     *
     * @param args Arguments provided to decompiler.
     */
    DecompilerMachine(final Map<String, String> args) {
        this(new LocalVariables(), args);
    }

    /**
     * Constructor.
     *
     * @param locals Local variables.
     * @param arguments Arguments provided to decompiler.
     */
    public DecompilerMachine(final LocalVariables locals, final Map<String, String> arguments) {
        this.locals = locals;
        this.agents = new AllAgents(
            "true".equals(arguments.getOrDefault("counting", "true"))
        );
    }

    /**
     * Decompile instructions into directives.
     *
     * @param instructions Instructions to decompile.
     * @return Decompiled instructions.
     */
    public Iterable<Directive> decompile(final Instruction... instructions) {
        final DecompilerState initial = new DecompilerState(
            Arrays.stream(instructions)
                .map(Opcode::new)
                .collect(Collectors.toCollection(LinkedList::new)),
            new OperandStack(),
            this.locals
        );
        this.agents.handle(initial);
        return new Root(new ListOf<>(initial.stack().descendingIterator())).toXmir();
    }
}

