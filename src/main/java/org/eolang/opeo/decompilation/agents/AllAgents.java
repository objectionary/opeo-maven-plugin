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
package org.eolang.opeo.decompilation.agents;

import java.util.Map;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.opeo.LabelInstruction;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.ast.OpcodeName;
import org.eolang.opeo.decompilation.DecompilerState;
import org.objectweb.asm.Opcodes;

/**
 * All agents that try to decompile incoming instructions.
 * @since 0.2
 * @todo #376:90min Decompilation Finish Condition.
 *  Currently we decompile until we out of instructions.
 *  But this might be incorrect when we start decompile high-level constructs.
 *  We should check decompilation stack instead.
 *  If it changes, we should continue. Otherwise, we should stop.
 *  The current implementation you can find here {@link #handle(DecompilerState)}.
 * @todo #376:90min Unsupported Opcodes.
 *  Currently we have the ugly map {@link #agents} that contains instructions we can handle.
 *  We should refactor it to a more elegant solution.
 *  For example, each agent might provide a list of instructions it can decompile.
 *  By doing this we can infer the entire list of supported and unsupported instructions.
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 */
public final class AllAgents implements DecompilationAgent {

    /**
     * Index of unimplemented agent.
     */
    private static final int UNIMPLEMENTED = -1;

    /**
     * ALl instruction handlers.
     */
    private final Map<Integer, DecompilationAgent> agents;

    /**
     * Constructor.
     * @param counting Do we put numbers to opcodes?
     */
    public AllAgents(final boolean counting) {
        this(
            new MapOf<Integer, DecompilationAgent>(
                new MapEntry<>(Opcodes.ICONST_M1, new ConstAgent()),
                new MapEntry<>(Opcodes.ICONST_0, new ConstAgent()),
                new MapEntry<>(Opcodes.ICONST_1, new ConstAgent()),
                new MapEntry<>(Opcodes.ICONST_2, new ConstAgent()),
                new MapEntry<>(Opcodes.ICONST_3, new ConstAgent()),
                new MapEntry<>(Opcodes.ICONST_4, new ConstAgent()),
                new MapEntry<>(Opcodes.ICONST_5, new ConstAgent()),
                new MapEntry<>(Opcodes.LCONST_0, new ConstAgent()),
                new MapEntry<>(Opcodes.LCONST_1, new ConstAgent()),
                new MapEntry<>(Opcodes.FCONST_0, new ConstAgent()),
                new MapEntry<>(Opcodes.FCONST_1, new ConstAgent()),
                new MapEntry<>(Opcodes.FCONST_2, new ConstAgent()),
                new MapEntry<>(Opcodes.DCONST_0, new ConstAgent()),
                new MapEntry<>(Opcodes.DCONST_1, new ConstAgent()),
                new MapEntry<>(Opcodes.IADD, new AddAgent()),
                new MapEntry<>(Opcodes.LADD, new AddAgent()),
                new MapEntry<>(Opcodes.FADD, new AddAgent()),
                new MapEntry<>(Opcodes.DADD, new AddAgent()),
                new MapEntry<>(Opcodes.ISUB, new SubAgent()),
                new MapEntry<>(Opcodes.LSUB, new SubAgent()),
                new MapEntry<>(Opcodes.FSUB, new SubAgent()),
                new MapEntry<>(Opcodes.DSUB, new SubAgent()),
                new MapEntry<>(Opcodes.IMUL, new MulAgent()),
                new MapEntry<>(Opcodes.IF_ICMPGT, new IfAgent()),
                new MapEntry<>(Opcodes.I2B, new CastAgent()),
                new MapEntry<>(Opcodes.I2C, new CastAgent()),
                new MapEntry<>(Opcodes.I2S, new CastAgent()),
                new MapEntry<>(Opcodes.I2L, new CastAgent()),
                new MapEntry<>(Opcodes.I2F, new CastAgent()),
                new MapEntry<>(Opcodes.I2D, new CastAgent()),
                new MapEntry<>(Opcodes.L2I, new CastAgent()),
                new MapEntry<>(Opcodes.L2F, new CastAgent()),
                new MapEntry<>(Opcodes.L2D, new CastAgent()),
                new MapEntry<>(Opcodes.F2I, new CastAgent()),
                new MapEntry<>(Opcodes.F2L, new CastAgent()),
                new MapEntry<>(Opcodes.F2D, new CastAgent()),
                new MapEntry<>(Opcodes.D2I, new CastAgent()),
                new MapEntry<>(Opcodes.D2L, new CastAgent()),
                new MapEntry<>(Opcodes.D2F, new CastAgent()),
                new MapEntry<>(Opcodes.ILOAD, new LoadAgent()),
                new MapEntry<>(Opcodes.LLOAD, new LoadAgent()),
                new MapEntry<>(Opcodes.FLOAD, new LoadAgent()),
                new MapEntry<>(Opcodes.DLOAD, new LoadAgent()),
                new MapEntry<>(Opcodes.ALOAD, new LoadAgent()),
                new MapEntry<>(Opcodes.ISTORE, new StoreAgent()),
                new MapEntry<>(Opcodes.LSTORE, new StoreAgent()),
                new MapEntry<>(Opcodes.FSTORE, new StoreAgent()),
                new MapEntry<>(Opcodes.DSTORE, new StoreAgent()),
                new MapEntry<>(Opcodes.ASTORE, new StoreAgent()),
                new MapEntry<>(Opcodes.AASTORE, new StoreToArrayAgent()),
                new MapEntry<>(Opcodes.ANEWARRAY, new NewArrayAgent()),
                new MapEntry<>(Opcodes.CHECKCAST, new CheckCastAgent()),
                new MapEntry<>(Opcodes.NEW, new NewAgent()),
                new MapEntry<>(Opcodes.DUP, new DupAgent()),
                new MapEntry<>(Opcodes.BIPUSH, new BipushAgent()),
                new MapEntry<>(Opcodes.INVOKESPECIAL, new InvokespecialAgent()),
                new MapEntry<>(Opcodes.INVOKEVIRTUAL, new InvokevirtualAgent()),
                new MapEntry<>(Opcodes.INVOKESTATIC, new InvokestaticAgent()),
                new MapEntry<>(Opcodes.INVOKEINTERFACE, new InvokeinterfaceAgent()),
                new MapEntry<>(Opcodes.INVOKEDYNAMIC, new InvokedynamicAgent()),
                new MapEntry<>(Opcodes.GETFIELD, new GetFieldAgent()),
                new MapEntry<>(Opcodes.PUTFIELD, new PutFieldAgent()),
                new MapEntry<>(Opcodes.GETSTATIC, new GetStaticAgent()),
                new MapEntry<>(Opcodes.LDC, new LdcAgent()),
                new MapEntry<>(Opcodes.POP, new PopAgent()),
                new MapEntry<>(Opcodes.RETURN, new ReturnAgent()),
                new MapEntry<>(Opcodes.IRETURN, new ReturnAgent()),
                new MapEntry<>(Opcodes.ARETURN, new ReturnAgent()),
                new MapEntry<>(LabelInstruction.LABEL_OPCODE, new LabelAgent()),
                new MapEntry<>(AllAgents.UNIMPLEMENTED, new UnimplementedAgent(counting))
            )
        );
    }

    /**
     * Constructor.
     * @param agents All handlers that will try to handle incoming instructions.
     */
    private AllAgents(final Map<Integer, DecompilationAgent> agents) {
        this.agents = agents;
    }

    @Override
    public void handle(final DecompilerState state) {
        while (state.hasInstructions()) {
            this.agents.values().forEach(agent -> agent.handle(state));
        }
    }

    @Override
    public Supported supported() {
        return this.agents.values().stream()
            .map(DecompilationAgent::supported).
            reduce(new Supported(), Supported::merge);
    }

    /**
     * Get supported opcodes.
     * @return Supported opcodes.
     */
    public String[] supportedOpcodes() {
        return this.supported().names();
    }

    /**
     * Unimplemented instruction handler.
     * @since 0.1
     */
    private static final class UnimplementedAgent implements DecompilationAgent {

        /**
         * Do we put numbers to opcodes?
         */
        private final boolean counting;

        /**
         * Constructor.
         * @param counting Flag which decides if we need to count opcodes.
         */
        private UnimplementedAgent(final boolean counting) {
            this.counting = counting;
        }

        @Override
        public void handle(final DecompilerState state) {
            if (!new AllAgents(false).supported().isSupported(state.current())) {
                state.stack().push(
                    new Opcode(
                        state.current().opcode(),
                        state.current().params(),
                        this.counting
                    )
                );
                state.popInstruction();
            }
        }

        @Override
        public Supported supported() {
            return new Supported();
        }
    }
}
