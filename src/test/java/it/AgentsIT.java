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
package it;

import com.jcabi.xml.XMLDocument;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.cactoos.Scalar;
import org.cactoos.scalar.Sticky;
import org.eolang.jucs.ClasspathSource;
import org.eolang.opeo.Instruction;
import org.eolang.opeo.OpcodeInstruction;
import org.eolang.opeo.ast.OpcodeName;
import org.eolang.opeo.decompilation.DecompilerMachine;
import org.eolang.opeo.decompilation.agents.TracedAgent;
import org.eolang.parser.xmir.Xmir;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.xembly.Directives;
import org.xembly.Xembler;
import org.yaml.snakeyaml.Yaml;

/**
 * Test pack that checks how separate agents decompile instructions.
 * @since 0.4
 * @todo #381:30min Remove `program` and `object` elements.
 *  We added this elements to the test to make it pass.
 *  Currently {@link Xmir.Default} does not support the decompilation of partial XMIR documents.
 *  So, by adding these elements we mimic the full XMIR document.
 *  When the related issue is fixed, remove these elements.
 *  You can follow the progress of the issue
 *  <a href="https://github.com/objectionary/eo/issues/3343">here.</a>
 * @todo #381:90min Add More Tests With Instructions That Have Params.
 *  Currently, we only have tests with instructions that do not have parameters.
 *  This is an extremely limited number of instructions.
 *  We need to add more tests with instructions that have parameters.
 */
@SuppressWarnings("JTCOP.RuleCorrectTestName")
final class AgentsIT {

    @ParameterizedTest
    @ClasspathSource(value = "agents", glob = "**.yaml")
    void verifiesAgents(final String yaml) {
        final InstructionPack pack = new InstructionPack(yaml);
        final TracedAgent.Container output = new TracedAgent.Container();
        MatcherAssert.assertThat(
            "Agents should decompile instructions correctly, according to the YAML pack",
            new Xmir.Default(
                new XMLDocument(
                    new Xembler(
                        new Directives().add("program").add("objects")
                            .append(
                                new DecompilerMachine(Collections.singletonMap("output", output))
                                    .decompile(pack.instructions())
                            )
                            .up().up()
                    ).xmlQuietly()
                )
            ).toEO(),
            Matchers.equalTo(pack.expected())
        );
        MatcherAssert.assertThat(
            "We expect that agents is used in the same order as expected, but they didn't",
            output.agentsUsed(),
            Matchers.equalTo(pack.agents())
        );
    }

    /**
     * Instruction pack.
     * You can find examples of the {@link InstructionPack} right
     * <a href="./resources/agents">here.</a>
     * @since 0.4
     */
    private static final class InstructionPack {

        /**
         * Integer pattern.
         */
        private static final Pattern INTEGER = Pattern.compile("\\d+");

        /**
         * Boolean pattern.
         */
        private static final Pattern BOOLEAN = Pattern.compile("true|false");

        /**
         * Double pattern.
         */
        private static final Pattern DOUBLE = Pattern.compile("\\d+\\.\\d+");

        /**
         * Long pattern.
         */
        private static final Pattern LONG = Pattern.compile("\\d+L");

        /**
         * String pattern.
         */
        private static final Pattern STRING = Pattern.compile("\"*\"");

        /**
         * Yaml pack.
         */
        private final Scalar<? extends Map<String, Object>> pack;

        /**
         * Constructor.
         * @param yaml Yaml file content.
         */
        InstructionPack(final String yaml) {
            this(new Sticky<>(() -> new Yaml().load(yaml)));
        }

        /**
         * Constructor.
         * @param pack Yaml pack.
         */
        private InstructionPack(final Scalar<? extends Map<String, Object>> pack) {
            this.pack = pack;
        }

        /**
         * Expected agents used to decompile instructions.
         * @return Agents.
         */
        @SuppressWarnings("unchecked")
        List<String> agents() {
            return (List<String>) this.value("agents");
        }

        /**
         * Expected EO code.
         * @return EO code.
         */
        String expected() {
            return (String) this.value("eo");
        }

        /**
         * Instructions to decompile.
         * @return Instructions.
         */
        @SuppressWarnings("unchecked")
        Instruction[] instructions() {
            return ((Collection<String>) this.value("opcodes"))
                .stream()
                .map(this::parse)
                .toArray(Instruction[]::new);
        }

        /**
         * Parse instruction.
         * @param instr Instruction as string.
         * @return Instruction.
         */
        private Instruction parse(final String instr) {
            final String[] args = instr.split(" ");
            final String opcode = args[0];
            if (args.length > 1) {
                return new OpcodeInstruction(
                    new OpcodeName(opcode).code(),
                    this.typed(Arrays.copyOfRange(args, 1, args.length))
                );
            } else {
                return new OpcodeInstruction(new OpcodeName(opcode).code());
            }
        }

        /**
         * Typed arguments.
         * @param args Arguments as strings.
         * @return Typed arguments.
         */
        private Object[] typed(final String... args) {
            return Arrays.stream(args).map(arg -> {
                if (InstructionPack.INTEGER.matcher(arg).matches()) {
                    return Integer.parseInt(arg);
                } else if (InstructionPack.BOOLEAN.matcher(arg).matches()) {
                    return Boolean.parseBoolean(arg);
                } else if (InstructionPack.DOUBLE.matcher(arg).matches()) {
                    return Double.parseDouble(arg);
                } else if (InstructionPack.LONG.matcher(arg).matches()) {
                    return Long.parseLong(arg.substring(0, arg.length() - 1));
                } else if (InstructionPack.STRING.matcher(arg).matches()) {
                    return arg.substring(1, arg.length() - 1);
                } else {
                    throw new IllegalArgumentException(
                        String.format("Unknown type of argument: %s", arg)
                    );
                }
            }).toArray();
        }

        /**
         * Get Yaml value by key.
         * @param key Yaml key.
         * @return Yaml value.
         * @checkstyle IllegalCatchCheck (6 lines)
         */
        @SuppressWarnings("PMD.AvoidCatchingGenericException")
        private Object value(final String key) {
            try {
                return this.pack.value().get(key);
            } catch (final Exception exception) {
                throw new IllegalStateException("Failed to parse YAML pack", exception);
            }
        }
    }
}
