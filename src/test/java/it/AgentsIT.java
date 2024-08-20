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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.cactoos.Scalar;
import org.cactoos.scalar.Sticky;
import org.eolang.jucs.ClasspathSource;
import org.eolang.opeo.Instruction;
import org.eolang.opeo.OpcodeInstruction;
import org.eolang.opeo.ast.OpcodeName;
import org.eolang.opeo.decompilation.DecompilerMachine;
import org.eolang.parser.xmir.Xmir;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.xembly.Directive;
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
 */
@SuppressWarnings("JTCOP.RuleCorrectTestName")
final class AgentsIT {

    @ParameterizedTest
    @ClasspathSource(value = "agents", glob = "**.yaml")
    void verifiesAgents(final String yaml) {
        final InstructionPack pack = new InstructionPack(yaml);
        final Iterable<Directive> xmir =
            new Directives().add("program")
                .add("objects")

                .append(
                    new DecompilerMachine().decompile(pack.instructions())

                ).up().up();
        final String xml = new Xembler(xmir).xmlQuietly();
        final String actual = new Xmir.Default(new XMLDocument(xml)).toEO();
        final String expected = pack.expected();
        final List<String> expectedAgents = pack.agents();
        MatcherAssert.assertThat(
            "Agents should decompile instructions correctly, according to the YAML pack",
            actual,
            Matchers.equalTo(expected)
        );

    }

    private static final class InstructionPack {
        private final Scalar<Map<String, Object>> pack;

        InstructionPack(final String yaml) {
            this(new Sticky<>(() -> new Yaml().load(yaml)));
        }

        private InstructionPack(final Scalar<Map<String, Object>> pack) {
            this.pack = pack;
        }

        Instruction[] instructions() {
            try {
                final List<String> opcodes1 = (List<String>) this.pack.value().get("opcodes");
                final Stream<OpcodeInstruction> opcodes = opcodes1
                    .stream()
                    .map(s -> new OpcodeInstruction(new OpcodeName(s).code()));
                final Instruction[] array = opcodes
                    .toArray(Instruction[]::new);
                return array;
            } catch (final Exception exception) {
                throw new IllegalStateException("Failed to parse YAML pack", exception);
            }
        }

        List<String> agents() {
            try {
                final List<String> agents = (List<String>) this.pack.value().get("agents");
                return agents;
            } catch (final Exception exception) {
                throw new IllegalStateException("Failed to parse YAML pack", exception);
            }
        }

        String expected() {
            try {
                final String strings = (String) this.pack.value().get("eo");
                return strings;
            } catch (final Exception exception) {
                throw new IllegalStateException("Failed to parse YAML pack", exception);
            }
        }

    }
}
