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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.ast.Add;
import org.eolang.opeo.ast.Literal;
import org.eolang.opeo.ast.Opcode;
import org.hamcrest.Description;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Tests for {@link OpeoNodes}.
 * @since 0.1
 */
final class OpeoNodesTest {

    @Test
    void convertsOpcodesAsIs() {
        final List<XmlNode> nodes = new OpeoNodes(
            new Opcode(Opcodes.ICONST_0, false), new Opcode(Opcodes.POP, false)
        ).toJeoNodes();
        MatcherAssert.assertThat(
            "We expect to retrieve 2 opcodes, but got something else instead: %n%s%n",
            nodes,
            Matchers.hasSize(2)
        );
        MatcherAssert.assertThat(
            "We expect to have specific opcodes in right order as is",
            nodes,
            new HasInstructions(
                Opcodes.ICONST_0,
                Opcodes.POP
            )
        );
    }

    @Test
    void convertsAddition() {
        final List<XmlNode> nodes = new OpeoNodes(
            new Add(new Literal(1), new Literal(2))
        ).toJeoNodes();
        MatcherAssert.assertThat(
            String.format(
                "We expect to retrieve 3 opcodes, but got something else instead: %n%s%n",
                nodes
            ),
            nodes,
            Matchers.hasSize(3)
        );
        MatcherAssert.assertThat(
            "We expect to have specific opcodes in right order",
            nodes,
            new HasInstructions(
                Opcodes.ICONST_1,
                Opcodes.ICONST_2,
                Opcodes.IADD
            )
        );
    }

    @Test
    void convertsDeepAddition() {
        final List<XmlNode> nodes = new OpeoNodes(
            new Add(
                new Add(
                    new Literal(1),
                    new Literal(2)
                ),
                new Add(
                    new Literal(3),
                    new Literal(4)
                )
            )
        ).toJeoNodes();
        MatcherAssert.assertThat(
            nodes,
            new HasInstructions()
        );
    }

    /**
     * Matcher for {@link List} of {@link XmlNode} to have specific instructions.
     * @since 0.1
     */
    private static class HasInstructions extends TypeSafeMatcher<List<XmlNode>> {

        /**
         * Expected opcodes.
         */
        private final List<Integer> opcodes;

        /**
         * Bag of collected warnings.
         */
        private final List<String> warnings;

        /**
         * Constructor.
         * @param opcodes Expected opcodes.
         */
        private HasInstructions(int... opcodes) {
            this(IntStream.of(opcodes).boxed().collect(Collectors.toList()));
        }

        /**
         * Constructor.
         * @param opcodes Expected opcodes.
         */
        private HasInstructions(final List<Integer> opcodes) {
            this.opcodes = opcodes;
            this.warnings = new ArrayList<>(0);
        }

        @Override
        protected boolean matchesSafely(final List<XmlNode> item) {
            final int size = item.size();
            for (int index = 0; index < size; ++index) {
                final XmlNode node = item.get(index);
                final Optional<String> obase = node.attribute("base");
                if (obase.isPresent()) {
                    final String base = obase.get();
                    if (base.equals("opcode")) {
                        final Optional<String> oname = node.attribute("name");
                        if (oname.isPresent()) {
                            final String expected = HasInstructions.name(this.opcodes.get(index));
                            if (!oname.get().contains(expected)) {
                                this.warnings.add(
                                    String.format(
                                        "Expected to have opcode with name %s at index %d, but got %s instead",
                                        expected,
                                        index,
                                        oname.get()
                                    )
                                );
                                return false;
                            }
                        } else {
                            this.warnings.add(
                                String.format(
                                    "Expected to have opcode name at index %d, but got nothing instead",
                                    index
                                )
                            );
                            return false;
                        }
                    } else {
                        this.warnings.add(
                            String.format(
                                "Expected to have opcode at index %d, but got %s instead",
                                index,
                                base
                            )
                        );
                        return false;
                    }
                }
            }
            return true;
        }

        private static String name(final int opcode) {
            final Field[] fields = Arrays.stream(Opcodes.class.getFields())
                .filter(field -> !field.getName().startsWith("T_"))
                .filter(field -> !field.getName().startsWith("H_"))
                .filter(field -> !field.getName().startsWith("F_"))
                .filter(field -> !field.getName().startsWith("ACC"))
                .filter(field -> !field.getName().startsWith("ASM"))
                .toArray(Field[]::new);
            for (final Field field : fields) {
                if (field.getType() == int.class) {
                    try {
                        if (opcode == field.getInt(Opcodes.class)) {
                            return field.getName();
                        }
                    } catch (final IllegalAccessException exception) {
                        throw new RuntimeException(exception);
                    }
                }
            }
            throw new IllegalStateException(String.format("Unknown opcode: %d", opcode));
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText(
                String.format(
                    "Expected to have %d opcodes, but got %d instead. %n%s%n",
                    this.opcodes.size(),
                    this.warnings.size(),
                    String.join("\n", this.warnings)
                )
            );
        }
    }
}
