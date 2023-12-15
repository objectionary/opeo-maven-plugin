package org.eolang.opeo;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

public class DecompilerTest {

    /**
     * Test decompilation of new instructions.
     * <p>
     *     {@code
     *       new B(new A(42));
     *     }
     * </p>
     */
    @Test
    public void decompilesNewInstructions() {
        MatcherAssert.assertThat(
            "Can't decompile new instructions",
            new Decompiler().decompile(
                new Instruction(Opcodes.NEW, "B"),
                new Instruction(Opcodes.DUP),
                new Instruction(Opcodes.NEW, "A"),
                new Instruction(Opcodes.DUP),
                new Instruction(Opcodes.BIPUSH, 42),
                new Instruction(Opcodes.INVOKESPECIAL, "A", "<init>", "(I)V"),
                new Instruction(Opcodes.INVOKESPECIAL, "B", "<init>", "(LA;)V"),
                new Instruction(Opcodes.POP),
                new Instruction(Opcodes.RETURN)
            ),
            Matchers.equalTo(
                "new B(new A(42));\nreturn;\n"
            )
        );
    }


}
