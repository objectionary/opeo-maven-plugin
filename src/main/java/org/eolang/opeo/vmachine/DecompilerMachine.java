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
package org.eolang.opeo.vmachine;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.opeo.Instruction;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.Constructor;
import org.eolang.opeo.ast.Invocation;
import org.eolang.opeo.ast.Literal;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.ast.Root;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;

/**
 * Decompiler machine.
 * @since 0.1
 */
public final class DecompilerMachine {

    /**
     * Operand Stack.
     */
    private final Deque<Object> stack;

    /**
     * Output root.
     */
    private final Root root;

    /**
     * Instruction handlers.
     */
    private final Map<Integer, InstructionHandler> handlers;

    /**
     * Constructor.
     */
    public DecompilerMachine() {
        this(new LinkedList<>());
    }

    /**
     * Constructor.
     * @param stack Operand stack.
     */
    private DecompilerMachine(final Deque<Object> stack) {
        this.stack = stack;
        this.root = new Root();
        this.handlers = new MapOf<>(
            new MapEntry<>(Opcodes.ICONST_1, new IconstHandler()),
            new MapEntry<>(Opcodes.ICONST_2, new IconstHandler()),
            new MapEntry<>(Opcodes.ICONST_3, new IconstHandler()),
            new MapEntry<>(Opcodes.ICONST_4, new IconstHandler()),
            new MapEntry<>(Opcodes.ICONST_5, new IconstHandler()),
            new MapEntry<>(Opcodes.ALOAD, new AloadHandler()),
            new MapEntry<>(Opcodes.NEW, new NewHandler()),
            new MapEntry<>(Opcodes.DUP, new DupHandler()),
            new MapEntry<>(Opcodes.BIPUSH, new BipushHandler()),
            new MapEntry<>(Opcodes.INVOKESPECIAL, new InvokespecialHandler()),
            new MapEntry<>(Opcodes.INVOKEVIRTUAL, new InvokevirtualHandler()),
            new MapEntry<>(Opcodes.LDC, new LdcHandler()),
            new MapEntry<>(Opcodes.POP, new PopHandler()),
            new MapEntry<>(Opcodes.RETURN, new ReturnHandler())
        );
    }

    /**
     * Decompile instructions into directives.
     * @param instructions Instructions to decompile.
     * @return Decompiled instructions.
     */
    public Iterable<Directive> decompileToXmir(final Instruction... instructions) {
        Arrays.stream(instructions)
            .forEach(inst -> this.handler(inst.opcode()).handle(inst));
        return this.root.toXmir();
    }

    /**
     * Decompile instructions.
     * @param instructions Instructions to decompile.
     * @return Decompiled code.
     */
    public String decompile(final Instruction... instructions) {
        Arrays.stream(instructions)
            .forEach(inst -> this.handler(inst.opcode()).handle(inst));
        return this.root.print();
    }

    /**
     * Get instruction handler.
     * @param opcode Instruction opcode.
     * @return Instruction handler.
     */
    private InstructionHandler handler(final int opcode) {
        return this.handlers.getOrDefault(opcode, new UnimplementedHandler());
    }

    /**
     * Pops n arguments from the stack.
     * @param number Number of arguments to pop.
     * @return List of arguments.
     */
    private List<AstNode> popArguments(final int number) {
        final List<AstNode> arguments = new LinkedList<>();
        for (int index = 0; index < number; ++index) {
            final Object arg = this.stack.pop();
            final AstNode node = this.root.child(String.valueOf(arg))
                .orElseGet(() -> new Literal(arg));
            this.root.disconnect(node);
            arguments.add(node);
        }
        return arguments;
    }

    /**
     * Instruction handler.
     * @since 0.1
     */
    private interface InstructionHandler {
        /**
         * Handle instruction.
         * @param instruction Instruction to handle.
         */
        void handle(Instruction instruction);

    }

    /**
     * Aload instruction handler.
     * @since 0.1
     */
    private class AloadHandler implements InstructionHandler {

        @Override
        public void handle(final Instruction instruction) {
            if (instruction.operand(0).equals(0)) {
                DecompilerMachine.this.stack.push("this");
            }
            DecompilerMachine.this.root.append(
                new Opcode(instruction.opcode(), instruction.operands())
            );
        }
    }

    /**
     * New instruction handler.
     * @since 0.1
     */
    private class NewHandler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            DecompilerMachine.this.stack.push(
                new ObjectReference((String) instruction.operand(0)).toString()
            );
        }
    }

    /**
     * Dup instruction handler.
     * @since 0.1
     */
    private class DupHandler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            DecompilerMachine.this.stack.push(DecompilerMachine.this.stack.peek());
        }
    }

    /**
     * Bipush instruction handler.
     * @since 0.1
     */
    private class BipushHandler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            DecompilerMachine.this.stack.push(instruction.operand(0));
        }
    }

    /**
     * Pop instruction handler.
     * @since 0.1
     */
    private class PopHandler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            if (!DecompilerMachine.this.stack.isEmpty()) {
                DecompilerMachine.this.stack.pop();
            }
        }
    }

    /**
     * Return instruction handler.
     * @since 0.1
     */
    private class ReturnHandler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            DecompilerMachine.this.root.append(
                new Opcode(instruction.opcode(), instruction.operands())
            );
        }
    }

    /**
     * Invokespecial instruction handler.
     * @since 0.1
     */
    private class InvokespecialHandler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            if (!instruction.operand(1).equals("<init>")) {
                throw new UnsupportedOperationException(
                    String.format("Instruction %s is not supported yet", instruction)
                );
            }
            if (instruction.operand(0).equals("java/lang/Object")) {
                DecompilerMachine.this.root.append(
                    new Opcode(instruction.opcode(), instruction.operands())
                );
            } else {
                final List<AstNode> args = DecompilerMachine.this.popArguments(
                    Type.getArgumentCount((String) instruction.operand(2))
                );
                DecompilerMachine.this.root.append(
                    new Constructor(
                        (String) instruction.operand(0),
                        (String) DecompilerMachine.this.stack.pop(),
                        args
                    )
                );
            }
        }
    }

    /**
     * Invokevirtual instruction handler.
     * @since 0.1
     */
    private class InvokevirtualHandler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            final String method = (String) instruction.operand(1);
            final String descriptor = (String) instruction.operand(2);
            final List<AstNode> args = DecompilerMachine.this.popArguments(
                Type.getArgumentCount(descriptor)
            );
            final AstNode source = DecompilerMachine.this.popArguments(1).get(0);
            DecompilerMachine.this.root.append(
                new Invocation(source, method, args)
            );
        }
    }

    /**
     * Ldc instruction handler.
     * @since 0.1
     */
    private class LdcHandler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            DecompilerMachine.this.stack.push(instruction.operand(0));
        }
    }

    /**
     * Unimplemented instruction handler.
     * @since 0.1
     */
    private class UnimplementedHandler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            DecompilerMachine.this.root.append(
                new Opcode(instruction.opcode(), instruction.operands())
            );
        }
    }

    /**
     * Iconst instruction handler.
     * @since 0.1
     */
    private class IconstHandler implements InstructionHandler {

        @Override
        public void handle(final Instruction instruction) {
            switch (instruction.opcode()) {
                case Opcodes.ICONST_1:
                    DecompilerMachine.this.stack.push(1);
                    break;
                case Opcodes.ICONST_2:
                    DecompilerMachine.this.stack.push(2);
                    break;
                case Opcodes.ICONST_3:
                    DecompilerMachine.this.stack.push(3);
                    break;
                case Opcodes.ICONST_4:
                    DecompilerMachine.this.stack.push(4);
                    break;
                case Opcodes.ICONST_5:
                    DecompilerMachine.this.stack.push(5);
                    break;
                default:
                    throw new UnsupportedOperationException(
                        String.format("Instruction %s is not supported yet", instruction)
                    );
            }
        }
    }

}