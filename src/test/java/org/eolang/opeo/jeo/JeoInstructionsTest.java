package org.eolang.opeo.jeo;

import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.xmir.XmlInstruction;
import org.eolang.jeo.representation.xmir.XmlMethod;
import org.eolang.jeo.representation.xmir.XmlProgram;
import org.eolang.opeo.Instruction;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link JeoInstructions}.
 * @since 0.1
 */
class JeoInstructionsTest {

    @Test
    void parsesJeoInstructions() {
        final XmlMethod method = new XmlMethod();
        method.replaceInstructions(
            new XmlInstruction(Opcodes.LDC, "Hello, world!").toNode(),
            new XmlInstruction(
                Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V"
            ).toNode(),
            new XmlInstruction(Opcodes.RETURN).toNode()
        );
        final Instruction[] instructions = new JeoInstructions(method).instructions();
        MatcherAssert.assertThat(
            "The resulting array of instructions should have exactly 3 elements",
            instructions,
            Matchers.arrayWithSize(3)
        );
        MatcherAssert.assertThat(
            "The first instruction should be LDC",
            instructions[0].opcode(),
            Matchers.equalTo(Opcodes.LDC)
        );
        MatcherAssert.assertThat(
            "The second instruction should be INVOKEVIRTUAL",
            instructions[1].opcode(),
            Matchers.equalTo(Opcodes.INVOKEVIRTUAL)
        );
        MatcherAssert.assertThat(
            "The third instruction should be RETURN",
            instructions[2].opcode(),
            Matchers.equalTo(Opcodes.RETURN)
        );
    }

}