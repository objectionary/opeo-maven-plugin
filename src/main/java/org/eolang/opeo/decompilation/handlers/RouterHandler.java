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
package org.eolang.opeo.decompilation.handlers;

import java.util.Map;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.ast.OpcodeName;
import org.eolang.opeo.decompilation.DecompilerState;
import org.eolang.opeo.decompilation.InstructionHandler;
import org.eolang.opeo.jeo.JeoLabel;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * General Instruction Handler.
 * This handler redirects handling of instructions depending on an incoming instruction.
 * @since 0.2
 */
public final class RouterHandler implements InstructionHandler {

    /**
     * Index of unimplemented handler.
     */
    private static final int UNIMPLEMENTED = -1;

    /**
     * ALl instruction handlers.
     */
    private final Map<Integer, InstructionHandler> handlers;

    /**
     * Constructor.
     * @param counting Do we put numbers to opcodes?
     */
    public RouterHandler(final boolean counting) {
        this(
            new MapOf<>(
                new MapEntry<>(Opcodes.ICONST_M1, new ConstHandler(Type.INT_TYPE)),
                new MapEntry<>(Opcodes.ICONST_0, new ConstHandler(Type.INT_TYPE)),
                new MapEntry<>(Opcodes.ICONST_1, new ConstHandler(Type.INT_TYPE)),
                new MapEntry<>(Opcodes.ICONST_2, new ConstHandler(Type.INT_TYPE)),
                new MapEntry<>(Opcodes.ICONST_3, new ConstHandler(Type.INT_TYPE)),
                new MapEntry<>(Opcodes.ICONST_4, new ConstHandler(Type.INT_TYPE)),
                new MapEntry<>(Opcodes.ICONST_5, new ConstHandler(Type.INT_TYPE)),
                new MapEntry<>(Opcodes.LCONST_0, new ConstHandler(Type.LONG_TYPE)),
                new MapEntry<>(Opcodes.LCONST_1, new ConstHandler(Type.LONG_TYPE)),
                new MapEntry<>(Opcodes.FCONST_0, new ConstHandler(Type.FLOAT_TYPE)),
                new MapEntry<>(Opcodes.FCONST_1, new ConstHandler(Type.FLOAT_TYPE)),
                new MapEntry<>(Opcodes.FCONST_2, new ConstHandler(Type.FLOAT_TYPE)),
                new MapEntry<>(Opcodes.DCONST_0, new ConstHandler(Type.DOUBLE_TYPE)),
                new MapEntry<>(Opcodes.DCONST_1, new ConstHandler(Type.DOUBLE_TYPE)),
                new MapEntry<>(Opcodes.IADD, new AddHandler()),
                new MapEntry<>(Opcodes.LADD, new AddHandler()),
                new MapEntry<>(Opcodes.FADD, new AddHandler()),
                new MapEntry<>(Opcodes.DADD, new AddHandler()),
                new MapEntry<>(Opcodes.ISUB, new SubstractionHandler()),
                new MapEntry<>(Opcodes.LSUB, new SubstractionHandler()),
                new MapEntry<>(Opcodes.FSUB, new SubstractionHandler()),
                new MapEntry<>(Opcodes.DSUB, new SubstractionHandler()),
                new MapEntry<>(Opcodes.IMUL, new MulHandler(counting)),
                new MapEntry<>(Opcodes.I2B, new CastHandler(Type.BYTE_TYPE)),
                new MapEntry<>(Opcodes.I2C, new CastHandler(Type.CHAR_TYPE)),
                new MapEntry<>(Opcodes.I2S, new CastHandler(Type.SHORT_TYPE)),
                new MapEntry<>(Opcodes.I2L, new CastHandler(Type.LONG_TYPE)),
                new MapEntry<>(Opcodes.I2F, new CastHandler(Type.FLOAT_TYPE)),
                new MapEntry<>(Opcodes.I2D, new CastHandler(Type.DOUBLE_TYPE)),
                new MapEntry<>(Opcodes.L2I, new CastHandler(Type.INT_TYPE)),
                new MapEntry<>(Opcodes.L2F, new CastHandler(Type.FLOAT_TYPE)),
                new MapEntry<>(Opcodes.L2D, new CastHandler(Type.DOUBLE_TYPE)),
                new MapEntry<>(Opcodes.F2I, new CastHandler(Type.INT_TYPE)),
                new MapEntry<>(Opcodes.F2L, new CastHandler(Type.LONG_TYPE)),
                new MapEntry<>(Opcodes.F2D, new CastHandler(Type.DOUBLE_TYPE)),
                new MapEntry<>(Opcodes.D2I, new CastHandler(Type.INT_TYPE)),
                new MapEntry<>(Opcodes.D2L, new CastHandler(Type.LONG_TYPE)),
                new MapEntry<>(Opcodes.D2F, new CastHandler(Type.FLOAT_TYPE)),
                new MapEntry<>(Opcodes.ILOAD, new LoadHandler(Type.INT_TYPE)),
                new MapEntry<>(Opcodes.LLOAD, new LoadHandler(Type.LONG_TYPE)),
                new MapEntry<>(Opcodes.FLOAD, new LoadHandler(Type.FLOAT_TYPE)),
                new MapEntry<>(Opcodes.DLOAD, new LoadHandler(Type.DOUBLE_TYPE)),
                new MapEntry<>(Opcodes.ALOAD, new LoadHandler(Type.getType(Object.class))),
                new MapEntry<>(Opcodes.ISTORE, new StoreHandler(Type.INT_TYPE)),
                new MapEntry<>(Opcodes.LSTORE, new StoreHandler(Type.LONG_TYPE)),
                new MapEntry<>(Opcodes.FSTORE, new StoreHandler(Type.FLOAT_TYPE)),
                new MapEntry<>(Opcodes.DSTORE, new StoreHandler(Type.DOUBLE_TYPE)),
                new MapEntry<>(Opcodes.ASTORE, new StoreHandler(Type.getType(Object.class))),
                new MapEntry<>(Opcodes.AASTORE, new StoreToArrayHandler()),
                new MapEntry<>(Opcodes.ANEWARRAY, new NewArrayHandler()),
                new MapEntry<>(Opcodes.NEW, new NewHandler()),
                new MapEntry<>(Opcodes.DUP, new DupHandler()),
                new MapEntry<>(Opcodes.BIPUSH, new BipushHandler()),
                new MapEntry<>(Opcodes.INVOKESPECIAL, new InvokespecialHandler()),
                new MapEntry<>(Opcodes.INVOKEVIRTUAL, new InvokevirtualHandler()),
                new MapEntry<>(Opcodes.INVOKESTATIC, new InvokestaticHander()),
                new MapEntry<>(Opcodes.INVOKEINTERFACE, new InvokeinterfaceHandler()),
                new MapEntry<>(Opcodes.GETFIELD, new GetFieldHandler()),
                new MapEntry<>(Opcodes.PUTFIELD, new PutFieldHnadler()),
                new MapEntry<>(Opcodes.GETSTATIC, new GetStaticHnadler()),
                new MapEntry<>(Opcodes.LDC, new LdcHandler()),
                new MapEntry<>(Opcodes.POP, new PopHandler()),
                new MapEntry<>(Opcodes.RETURN, new ReturnHandler(counting)),
                new MapEntry<>(Opcodes.IRETURN, new ReturnHandler(counting)),
                new MapEntry<>(JeoLabel.LABEL_OPCODE, new LabelHandler()),
                new MapEntry<>(
                    RouterHandler.UNIMPLEMENTED, new UnimplementedHandler(counting)
                )
            )
        );
    }

    /**
     * Constructor.
     * @param handlers All handlers that will try to handle incoming instructions.
     */
    private RouterHandler(final Map<Integer, InstructionHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void handle(final DecompilerState state) {
        this.handler(state.instruction().opcode()).handle(state);
    }

    /**
     * Get supported opcodes.
     * @return Supported opcodes.
     */
    public String[] supportedOpcodes() {
        return this.handlers.keySet()
            .stream()
            .map(OpcodeName::new)
            .map(OpcodeName::simplified)
            .toArray(String[]::new);
    }

    /**
     * Get instruction handler.
     * @param opcode Instruction opcode.
     * @return Instruction handler.
     */
    private InstructionHandler handler(final int opcode) {
        return this.handlers.getOrDefault(opcode, this.handlers.get(RouterHandler.UNIMPLEMENTED));
    }

    /**
     * Unimplemented instruction handler.
     * @since 0.1
     */
    private static final class UnimplementedHandler implements InstructionHandler {

        /**
         * Do we put numbers to opcodes?
         */
        private final boolean counting;

        /**
         * Constructor.
         * @param counting Flag which decides if we need to count opcodes.
         */
        private UnimplementedHandler(final boolean counting) {
            this.counting = counting;
        }

        @Override
        public void handle(final DecompilerState state) {
            state.stack().push(
                new Opcode(
                    state.instruction().opcode(),
                    state.instruction().operands(),
                    this.counting
                )
            );
        }
    }
}
