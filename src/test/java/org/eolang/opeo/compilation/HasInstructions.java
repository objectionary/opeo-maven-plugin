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
package org.eolang.opeo.compilation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.eolang.jeo.representation.directives.DirectivesData;
import org.eolang.jeo.representation.xmir.HexString;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.ast.OpcodeName;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.xembly.Xembler;

/**
 * Matcher for {@link List} of {@link XmlNode} to have specific instructions.
 * @since 0.1
 */
@SuppressWarnings({"JTCOP.RuleAllTestsHaveProductionClass", "JTCOP.RuleCorrectTestName"})
public final class HasInstructions extends TypeSafeMatcher<List<XmlNode>> {

    /**
     * Expected opcodes.
     */
    private final List<Instruction> instructions;

    /**
     * Bag of collected warnings.
     */
    private final List<String> warnings;

    /**
     * Constructor.
     * @param opcodes Expected opcodes.
     */
    public HasInstructions(final int... opcodes) {
        this(IntStream.of(opcodes).boxed().collect(Collectors.toList()));
    }

    /**
     * Constructor.
     * @param opcodes Expected opcodes.
     */
    private HasInstructions(final List<Integer> opcodes) {
        this(
            opcodes.stream().map(Instruction::new).collect(Collectors.toList()),
            new ArrayList<>(0)
        );
    }

    /**
     * Constructor.
     * @param instructions Expected instructions.
     */
    public HasInstructions(final Instruction... instructions) {
        this(Arrays.asList(instructions), new ArrayList<>(0));
    }

    /**
     * Constructor.
     * @param instructions Expected instructions.
     * @param warnings Bag of collected warnings.
     */
    public HasInstructions(final List<Instruction> instructions, final List<String> warnings) {
        this.instructions = instructions;
        this.warnings = warnings;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText(
            String.format(
                "Expected to have %d opcodes, but got %d instead. %n%s%n",
                this.instructions.size(),
                this.warnings.size(),
                String.join("\n", this.warnings)
            )
        );
    }

    @Override
    public boolean matchesSafely(final List<XmlNode> item) {
        boolean result = true;
        final int size = item.size();
        for (int index = 0; index < size; ++index) {
            if (!this.matches(item.get(index), index)) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Check if node matches expected opcode.
     * @param node Node.
     * @param index Index.
     * @return True if node matches expected opcode.
     */
    private boolean matches(final XmlNode node, final int index) {
        final boolean result;
        final String base = node.attribute("base").orElseThrow();
        if (base.equals("opcode")) {
            result = this.verifyName(node, index) && this.verifyOperands(node, index);
        } else {
            this.warnings.add(
                String.format(
                    "Expected to have opcode at index %d, but got %s instead",
                    index,
                    base
                )
            );
            result = false;
        }
        return result;
    }

    /**
     * Verify opcode operands.
     * @param node Node.
     * @param index Index.
     * @return True if opcode operands are correct.
     */
    private boolean verifyOperands(final XmlNode node, final int index) {
        boolean result = true;
        final Instruction instruction = this.instructions.get(index);
        if (!instruction.isEmpty()) {
            final List<XmlNode> operands = node.children().skip(1).collect(Collectors.toList());
            for (int operindex = 0; operindex < operands.size(); ++operindex) {
                final XmlNode operand = operands.get(operindex);
                final XmlNode expected = new XmlNode(
                    new Xembler(
                        new DirectivesData(instruction.operands.get(operindex))
                    ).xmlQuietly()
                );
                if (!operand.equals(expected)) {
                    this.warnings.add(
                        String.format(
                            "Bytecode instruction at %d index should have opcode with operands %s but got %s instead, ('%s' != '%s')",
                            index,
                            operand,
                            expected,
                            new HexString(operand.text()).decode(),
                            new HexString(expected.text()).decode()
                        )
                    );
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Verify opcode name.
     * @param node Node.
     * @param index Index.
     * @return True if opcode name is correct.
     */
    private boolean verifyName(final XmlNode node, final int index) {
        final boolean result;
        final Optional<String> oname = node.attribute("name");
        if (oname.isPresent()) {
            final String expected = this.instructions.get(index).name();
            final String name = oname.get();
            if (name.contains(expected)) {
                result = true;
            } else {
                this.warnings.add(
                    String.format(
                        "Expected to have opcode with name %s at index %d, but got %s instead",
                        expected,
                        index,
                        name
                    )
                );
                result = false;
            }
        } else {
            this.warnings.add(
                String.format(
                    "Expected to have opcode name at index %d, but got nothing instead",
                    index
                )
            );
            result = false;
        }
        return result;
    }

    /**
     * Instruction.
     * @since 0.1
     */
    public static final class Instruction {

        /**
         * Opcode.
         */
        private final int opcode;

        /**
         * Instruction operands.
         */
        private final List<Object> operands;

        /**
         * Constructor.
         * @param opcode Opcode.
         */
        public Instruction(final int opcode) {
            this(opcode, new ArrayList<>(0));
        }

        /**
         * Constructor.
         * @param opcode Instruction opcode.
         * @param operands Instruction operands.
         */
        public Instruction(final int opcode, final Object... operands) {
            this(opcode, Arrays.asList(operands));
        }

        /**
         * Constructor.
         * @param opcode Instruction opcode.
         * @param operands Instruction operands.
         */
        Instruction(final int opcode, final List<Object> operands) {
            this.opcode = opcode;
            this.operands = operands;
        }

        /**
         * Opcode name.
         * @return Opcode name.
         */
        public String name() {
            return new OpcodeName(this.opcode).simplified();
        }

        /**
         * Check if instruction is empty.
         * @return True if instruction operands are empty.
         */
        boolean isEmpty() {
            return this.operands.isEmpty();
        }
    }
}
