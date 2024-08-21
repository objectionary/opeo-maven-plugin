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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cactoos.Scalar;
import org.cactoos.scalar.Sticky;
import org.eolang.jeo.representation.xmir.AllLabels;
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
import org.objectweb.asm.Label;
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
         * The pattern to match instructions.
         * It matches:
         * 1. Boolean values. Example: true, false.
         * 2. Double values. Example: 10.12.
         * 3. Long values. Example: 100L.
         * 4. Integer values. Example: 100.
         * 5. String values. Example: "Hello world!".
         * 6. Label. Example: LABEL:labelid.
         * 7. Instruction names. Example: LDC.
         */
        private static final Pattern INSTRUCTION = Pattern.compile(
            "(\\bfalse|true\\b)|(\\d+\\.\\d+)|(\\d+L)|(\\d+)|\"([^\"]*)\"|\\bLABEL\\b:(\\S+)|(\\S+)"
        );

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
            final Matcher matcher = InstructionPack.INSTRUCTION.matcher(instr);
            final AtomicInteger opcode = new AtomicInteger();
            final List<Object> arguments = new ArrayList<>(0);
            while (matcher.find()) {
                if (matcher.group(1) != null) {
                    arguments.add(Boolean.parseBoolean(matcher.group(1)));
                } else if (matcher.group(2) != null) {
                    arguments.add(Double.parseDouble(matcher.group(2)));
                } else if (matcher.group(3) != null) {
                    arguments.add(Long.parseLong(matcher.group(3)));
                } else if (matcher.group(4) != null) {
                    arguments.add(Integer.parseInt(matcher.group(4)));
                } else if (matcher.group(5) != null) {
                    final String group = matcher.group(5);
                    arguments.add(group);
                } else if (matcher.group(6) != null) {
                    arguments.add(new AllLabels().label(matcher.group(6)));
                } else {
                    opcode.set(new OpcodeName(matcher.group(7)).code());
                }
            }
            return new OpcodeInstruction(opcode.get(), arguments.toArray());
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
