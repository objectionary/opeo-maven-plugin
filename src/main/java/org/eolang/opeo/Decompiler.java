package org.eolang.opeo;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class Decompiler {

    private final Deque<Object> stack;
    private final Deque<String> result;

    private final Map<String, String> heap;

    private final Map<Integer, InstructionHandler> handlers;

    private final AtomicInteger counter;

    public Decompiler() {
        this(new LinkedList<>(), new LinkedList<>(), Map.of(), new AtomicInteger(0));
    }

    public Decompiler(
        final Deque<Object> stack,
        final Deque<String> result,
        final Map<String, String> heap,
        final AtomicInteger counter
    ) {
        this.stack = stack;
        this.result = result;
        this.heap = heap;
        this.counter = counter;
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
        return String.join(";\n", this.result);
    }

    private InstructionHandler handler(final int opcode) {
        return this.handlers.getOrDefault(opcode, new UnimplementedHandler());
    }

    private interface InstructionHandler {
        void handle(Instruction instruction);
    }

    /**
     * Returns a reference to an object.
     * @param type Object type.
     * @return Reference.
     */
    private String reference(final String type) {
        return String.format("%s%d%s",
            "&",
            this.counter.getAndIncrement(),
            type.replace('/', '.')
        );
    }

    /**
     * Pops n arguments from the stack.
     * @param n Number of arguments to pop.
     * @return List of arguments.
     */
    private List<String> popArguments(final int n) {
        final List<String> arguments = new LinkedList<>();
        for (int index = 0; index < n; index++) {
            arguments.add(0, String.valueOf(this.stack.pop()));
        }
        return arguments;
    }

    private class NewHandler implements InstructionHandler {
        @Override
        public void handle(Instruction instruction) {
            Decompiler.this.stack.push(Decompiler.this.reference((String) instruction.operand(0)));
        }
    }

    private class DupHandler implements InstructionHandler {
        @Override
        public void handle(Instruction instruction) {
            Decompiler.this.stack.push(Decompiler.this.stack.peek());
        }
    }

    private class BipushHandler implements InstructionHandler {
        @Override
        public void handle(Instruction instruction) {
            Decompiler.this.stack.push(instruction.operand(0));
        }
    }

    private class PopHandler implements InstructionHandler {
        @Override
        public void handle(Instruction instruction) {
            Decompiler.this.stack.pop();
        }
    }

    private class ReturnHandler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            Decompiler.this.result.add("return");
        }
    }

    private class InvokespecialHandler implements InstructionHandler {
        @Override
        public void handle(final Instruction instruction) {
            final String type = (String) instruction.operand(0);
            final String method = (String) instruction.operand(1);
            final String descriptor = (String) instruction.operand(2);
            if (!method.equals("<init>")) {
                throw new UnsupportedOperationException(
                    String.format("Instruction %s is not supported yet", instruction)
                );
            }
            final List<String> args = Decompiler.this.popArguments(
                Type.getArgumentCount(descriptor));
            Decompiler.this.result.push(String.format("%s.new %s", type, String.join(", ", args)));
        }
    }


    private class UnimplementedHandler implements InstructionHandler {
        @Override
        public void handle(Instruction instruction) {
            Decompiler.this.result.add(String.format("Unimplemented %s", instruction));
        }
    }
}
