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
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.opeo.Instruction;
import org.eolang.opeo.ast.Add;
import org.eolang.opeo.ast.ArrayConstructor;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.Attributes;
import org.eolang.opeo.ast.ClassField;
import org.eolang.opeo.ast.Constructor;
import org.eolang.opeo.ast.InstanceField;
import org.eolang.opeo.ast.Invocation;
import org.eolang.opeo.ast.Label;
import org.eolang.opeo.ast.Literal;
import org.eolang.opeo.ast.Mul;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.ast.Reference;
import org.eolang.opeo.ast.Root;
import org.eolang.opeo.ast.StaticInvocation;
import org.eolang.opeo.ast.StoreArray;
import org.eolang.opeo.ast.StoreLocal;
import org.eolang.opeo.ast.Substraction;
import org.eolang.opeo.ast.Super;
import org.eolang.opeo.ast.WriteField;
import org.eolang.opeo.jeo.JeoLabel;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;

/**
 * Decompiler machine.
 * @since 0.1
 */
public final class DecompilerMachine {

    /**
     * Output Stack.
     */
    private final Deque<AstNode> stack;

    /**
     * Local variables.
     */
    private final LocalVariables locals;

    /**
     * Instruction handlers.
     */
    private final Map<Integer, InstructionHandler> handlers;

    /**
     * Arguments provided to decompiler.
     */
    private final Map<String, String> arguments;

    /**
     * Constructor.
     */
    public DecompilerMachine() {
        this(new HashMap<>());
    }

    /**
     * Constructor.
     * @param args Arguments provided to decompiler.
     */
    public DecompilerMachine(final Map<String, String> args) {
        this(new LocalVariables(), args);
    }

    /**
     * Constructor.
     * @param locals Local variables.
     * @param arguments Arguments provided to decompiler.
     */
    public DecompilerMachine(final LocalVariables locals, final Map<String, String> arguments) {
        this.stack = new LinkedList<>();
        this.locals = locals;
        this.arguments = arguments;
        this.handlers = new MapOf<>(
            new MapEntry<>(Opcodes.ICONST_1, new IconstHandler()),
            new MapEntry<>(Opcodes.ICONST_2, new IconstHandler()),
            new MapEntry<>(Opcodes.ICONST_3, new IconstHandler()),
            new MapEntry<>(Opcodes.ICONST_4, new IconstHandler()),
            new MapEntry<>(Opcodes.ICONST_5, new IconstHandler()),
            new MapEntry<>(Opcodes.IADD, new AddHandler()),
            new MapEntry<>(Opcodes.ISUB, new SubstractionHandler()),
            new MapEntry<>(Opcodes.IMUL, new MulHandler()),
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
            new MapEntry<>(Opcodes.GETFIELD, new GetFieldHandler()),
            new MapEntry<>(Opcodes.PUTFIELD, new PutFieldHnadler()),
            new MapEntry<>(Opcodes.GETSTATIC, new GetStaticHnadler()),
            new MapEntry<>(Opcodes.LDC, new LdcHandler()),
            new MapEntry<>(Opcodes.POP, new PopHandler()),
            new MapEntry<>(Opcodes.RETURN, new ReturnHandler()),
            new MapEntry<>(JeoLabel.LABEL_OPCODE, new LabelHandler())
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
        return new Root(new ListOf<>(this.stack.descendingIterator())).toXmir();
    }

    /**
     * Decompile instructions.
     * @param instructions Instructions to decompile.
     * @return Decompiled code.
     */
    public String decompile(final Instruction... instructions) {
        Arrays.stream(instructions)
            .forEach(inst -> this.handler(inst.opcode()).handle(inst));
        return new Root(new ListOf<>(this.stack.descendingIterator())).print();
    }

    /**
     * Do we add number to opcode name or not?
     * if true then we add number to opcode name:
     *  RETURN -> RETURN-1
     * if false then we do not add number to opcode name:
     *  RETURN -> RETURN
     * @return Opcodes counting.
     */
    private boolean counting() {
        return this.arguments.getOrDefault("counting", "true").equals("true");
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
        final List<AstNode> args = new LinkedList<>();
        for (int index = 0; index < number; ++index) {
            args.add(this.stack.pop());
        }
        return args;
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
    private final class LoadHandler implements InstructionHandler {

        /**
         * Type of the variable.
         */
        private final Type type;

        /**
         * Constructor.
         * @param type Type of the variable.
         */
        private LoadHandler(final Type type) {
            this.type = type;
        }

        @Override
        public void handle(final Instruction instruction) {
            DecompilerMachine.this.stack.push(
                DecompilerMachine.this.locals.variable(
                    (Integer) instruction.operands().get(0),
                    this.type,
                    true
                )
            );
        }
    }

    /**
     * Store instruction handler.
     * @since 0.1
     */
    private final class StoreHandler implements InstructionHandler {

        /**
         * Type of the variable.
         */
        private final Type type;

        /**
         * Constructor.
         * @param type Type of the variable.
         */
        private StoreHandler(final Type type) {
            this.type = type;
        }

        @Override
        public void handle(final Instruction instruction) {
            DecompilerMachine.this.stack.push(
                new StoreLocal(
                    DecompilerMachine.this.locals.variable(
                        (Integer) instruction.operands().get(0),
                        this.type,
                        false
                    ),
                    DecompilerMachine.this.stack.pop()
                )
            );
        }

    }

    /**
     * Label instruction handler.
     * @since 0.1
     */
    private class StoreToArrayHandler implements InstructionHandler {

        @Override
        public void handle(final Instruction instruction) {
            final AstNode value = DecompilerMachine.this.stack.pop();
            final AstNode index = DecompilerMachine.this.stack.pop();
            final AstNode array = DecompilerMachine.this.stack.pop();
            DecompilerMachine.this.stack.push(new StoreArray(array, index, value));
        }
    }

    /**
     * New array instruction handler.
     * @since 0.1
     */
    private class NewArrayHandler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            final String type = (String) instruction.operand(0);
            final AstNode size = DecompilerMachine.this.stack.pop();
            final Reference reference = new Reference();
            reference.link(new ArrayConstructor(size, type));
            DecompilerMachine.this.stack.push(reference);
        }

    }

    /**
     * New instruction handler.
     * @since 0.1
     */
    private class NewHandler implements InstructionHandler {

        @Override
        public void handle(final Instruction instruction) {
            DecompilerMachine.this.stack.push(new Reference());
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
     * Getfield instruction handler.
     * @since 0.1
     */
    private class GetFieldHandler implements InstructionHandler {

        @Override
        public void handle(final Instruction instruction) {
            final String owner = (String) instruction.operand(0);
            final String name = (String) instruction.operand(1);
            final String descriptor = (String) instruction.operand(2);
            DecompilerMachine.this.stack.push(
                new InstanceField(
                    DecompilerMachine.this.stack.pop(),
                    new Attributes()
                        .name(name)
                        .descriptor(descriptor)
                        .owner(owner)
                        .type("field")
                )
            );
        }

    }

    /**
     * Putfield instruction handler.
     * @since 0.1
     */
    private class PutFieldHnadler implements InstructionHandler {

        @Override
        public void handle(final Instruction instruction) {
            final AstNode value = DecompilerMachine.this.stack.pop();
            final AstNode target = DecompilerMachine.this.stack.pop();
            final Attributes attributes = new Attributes()
                .type("field")
                .owner((String) instruction.operand(0))
                .name((String) instruction.operand(1))
                .descriptor((String) instruction.operand(2));
            DecompilerMachine.this.stack.push(
                new WriteField(target, value, attributes)
            );
        }

    }

    /**
     * Bipush instruction handler.
     * @since 0.1
     */
    private class BipushHandler implements InstructionHandler {

        @Override
        public void handle(final Instruction instruction) {
            DecompilerMachine.this.stack.push(new Literal(instruction.operand(0)));
        }

    }

    /**
     * Pop instruction handler.
     * @since 0.1
     */
    private class PopHandler implements InstructionHandler {

        @Override
        public void handle(final Instruction ignore) {
            // We ignore this instruction intentionally.
        }

    }

    /**
     * Return instruction handler.
     * @since 0.1
     */
    private class ReturnHandler implements InstructionHandler {

        @Override
        public void handle(final Instruction instruction) {
            DecompilerMachine.this.stack.push(
                new Opcode(
                    instruction.opcode(),
                    instruction.operands(),
                    DecompilerMachine.this.counting()
                )
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
            final String descriptor = (String) instruction.operand(2);
            final List<AstNode> args = DecompilerMachine.this.popArguments(
                Type.getArgumentCount(descriptor)
            );
            final String target = (String) instruction.operand(0);
            //@checkstyle MethodBodyCommentsCheck (10 lines)
            // @todo #76:90min Target might not be an Object.
            //  Here we just compare with object, but if the current class has a parent, the
            //  target might not be an Object. We should compare with the current class name
            //  instead. Moreover, we have to pass the 'target' as an argument to the
            //  constructor of the 'Super' class somehow.
            if ("java/lang/Object".equals(target)) {
                DecompilerMachine.this.stack.push(
                    new Super(DecompilerMachine.this.stack.pop(), args, descriptor)
                );
            } else {
                ((Reference) DecompilerMachine.this.stack.pop())
                    .link(new Constructor(target, new Attributes().descriptor(descriptor), args));
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
            final String owner = (String) instruction.operand(0);
            final String method = (String) instruction.operand(1);
            final String descriptor = (String) instruction.operand(2);
            final List<AstNode> args = DecompilerMachine.this.popArguments(
                Type.getArgumentCount(descriptor)
            );
            final AstNode source = DecompilerMachine.this.stack.pop();
            DecompilerMachine.this.stack.push(
                new Invocation(
                    source, new Attributes().name(method).descriptor(descriptor).owner(owner), args
                )
            );
        }

    }

    private class InvokestaticHander implements InstructionHandler {

        @Override
        public void handle(final Instruction instruction) {
            final String owner = (String) instruction.operand(0);
            final String method = (String) instruction.operand(1);
            final String descriptor = (String) instruction.operand(2);
            final List<AstNode> args = DecompilerMachine.this.popArguments(
                Type.getArgumentCount(descriptor)
            );
            DecompilerMachine.this.stack.push(
                new StaticInvocation(owner, method, descriptor, args)
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
            DecompilerMachine.this.stack.push(new Literal(instruction.operand(0)));
        }

    }

    /**
     * Unimplemented instruction handler.
     * @since 0.1
     */
    private class UnimplementedHandler implements InstructionHandler {

        @Override
        public void handle(final Instruction instruction) {
            DecompilerMachine.this.stack.push(
                new Opcode(
                    instruction.opcode(),
                    instruction.operands(),
                    DecompilerMachine.this.counting()
                )
            );
        }

    }

    /**
     * Add instruction handler.
     * @since 0.1
     */
    private class AddHandler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            if (instruction.opcode() == Opcodes.IADD) {
                final AstNode right = DecompilerMachine.this.stack.pop();
                final AstNode left = DecompilerMachine.this.stack.pop();
                DecompilerMachine.this.stack.push(new Add(left, right));
            } else {
                DecompilerMachine.this.stack.push(
                    new Opcode(
                        instruction.opcode(),
                        instruction.operands(),
                        DecompilerMachine.this.counting()
                    )
                );
            }
        }

    }

    /**
     * Substraction instruction handler.
     * @since 0.1
     */
    private class SubstractionHandler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            if (instruction.opcode() == Opcodes.ISUB) {
                final AstNode right = DecompilerMachine.this.stack.pop();
                final AstNode left = DecompilerMachine.this.stack.pop();
                DecompilerMachine.this.stack.push(new Substraction(left, right));
            } else {
                DecompilerMachine.this.stack.push(
                    new Opcode(
                        instruction.opcode(),
                        instruction.operands(),
                        DecompilerMachine.this.counting()
                    )
                );
            }
        }

    }

    /**
     * Mul instruction handler.
     * @since 0.1
     */
    private class MulHandler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            if (instruction.opcode() == Opcodes.IMUL) {
                final AstNode right = DecompilerMachine.this.stack.pop();
                final AstNode left = DecompilerMachine.this.stack.pop();
                DecompilerMachine.this.stack.push(new Mul(left, right));
            } else {
                DecompilerMachine.this.stack.push(
                    new Opcode(
                        instruction.opcode(),
                        instruction.operands(),
                        DecompilerMachine.this.counting()
                    )
                );
            }
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
                    DecompilerMachine.this.stack.push(new Literal(1));
                    break;
                case Opcodes.ICONST_2:
                    DecompilerMachine.this.stack.push(new Literal(2));
                    break;
                case Opcodes.ICONST_3:
                    DecompilerMachine.this.stack.push(new Literal(3));
                    break;
                case Opcodes.ICONST_4:
                    DecompilerMachine.this.stack.push(new Literal(4));
                    break;
                case Opcodes.ICONST_5:
                    DecompilerMachine.this.stack.push(new Literal(5));
                    break;
                default:
                    throw new UnsupportedOperationException(
                        String.format("Instruction %s is not supported yet", instruction)
                    );
            }
        }

    }

    private class LabelHandler implements InstructionHandler {

        @Override
        public void handle(final Instruction instruction) {
            DecompilerMachine.this.stack.push(
                new Label(new Literal(instruction.operand(0)))
            );
        }


    }

    private class GetStaticHnadler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            final String klass = (String) instruction.operand(0);
            final String method = (String) instruction.operand(1);
            final String descriptor = (String) instruction.operand(2);
            DecompilerMachine.this.stack.push(new ClassField(klass, method, descriptor));
        }
    }

}
