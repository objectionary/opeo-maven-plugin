package org.eolang.opeo.vmachine;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eolang.opeo.Instruction;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.Constructor;
import org.eolang.opeo.ast.Keyword;
import org.eolang.opeo.ast.Literal;
import org.eolang.opeo.ast.Root;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class DecompilerMachine {

    private final Deque<Object> stack;

    private final Root root;

    private final Map<Integer, InstructionHandler> handlers;

    public DecompilerMachine() {
        this(new LinkedList<>());
    }

    public DecompilerMachine(final Deque<Object> stack) {
        this.stack = stack;
        this.root = new Root();
        this.handlers = Map.of(
            Opcodes.NEW, new NewHandler(),
            Opcodes.DUP, new DupHandler(),
            Opcodes.BIPUSH, new BipushHandler(),
            Opcodes.INVOKESPECIAL, new InvokespecialHandler(),
            Opcodes.POP, new PopHandler(),
            Opcodes.RETURN, new ReturnHandler()
        );
    }

    public String decompile(Instruction... instructions) {
        Arrays.stream(instructions)
            .forEach(inst -> this.handler(inst.opcode()).handle(inst));
        return this.root.print();
    }

    private InstructionHandler handler(final int opcode) {
        return this.handlers.getOrDefault(opcode, new UnimplementedHandler());
    }

    /**
     * Pops n arguments from the stack.
     * @param n Number of arguments to pop.
     * @return List of arguments.
     */
    private List<AstNode> arguments(final int n) {
        final List<AstNode> arguments = new LinkedList<>();
        for (int index = 0; index < n; index++) {
            final Object arg = this.stack.pop();
            final AstNode node = this.root.child(String.valueOf(arg))
                .orElseGet(() -> new Literal(arg));
            this.root.disconnect(node);
            arguments.add(node);
        }
        return arguments;
    }


    private interface InstructionHandler {
        void handle(Instruction instruction);

    }

    private class NewHandler implements InstructionHandler {
        @Override
        public void handle(Instruction instruction) {
            DecompilerMachine.this.stack.push(
                new ObjectReference((String) instruction.operand(0)).toString()
            );
        }
    }

    private class DupHandler implements InstructionHandler {
        @Override
        public void handle(Instruction instruction) {
            DecompilerMachine.this.stack.push(DecompilerMachine.this.stack.peek());
        }
    }

    private class BipushHandler implements InstructionHandler {
        @Override
        public void handle(Instruction instruction) {
            DecompilerMachine.this.stack.push(instruction.operand(0));
        }
    }

    private class PopHandler implements InstructionHandler {
        @Override
        public void handle(Instruction instruction) {
            DecompilerMachine.this.stack.pop();
        }
    }

    private class ReturnHandler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            DecompilerMachine.this.root.append(new Keyword("return"));
        }
    }

    private class InvokespecialHandler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            if (!instruction.operand(1).equals("<init>")) {
                throw new UnsupportedOperationException(
                    String.format("Instruction %s is not supported yet", instruction)
                );
            }
            final List<AstNode> args = DecompilerMachine.this.arguments(
                Type.getArgumentCount((String) instruction.operand(2))
            );
            final String type = (String) DecompilerMachine.this.stack.pop();
            DecompilerMachine.this.root.append(
                new Constructor((String) instruction.operand(0), type, args)
            );
        }
    }

    private class UnimplementedHandler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            DecompilerMachine.this.root
                .append(new Keyword(String.format("Unimplemented %s", instruction)));
        }
    }


}
