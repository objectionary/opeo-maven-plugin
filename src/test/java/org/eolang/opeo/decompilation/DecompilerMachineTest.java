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

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import org.eolang.jeo.representation.xmir.AllLabels;
import org.eolang.opeo.LabelInstruction;
import org.eolang.opeo.OpcodeInstruction;
import org.eolang.opeo.SameXml;
import org.eolang.opeo.ast.Add;
import org.eolang.opeo.ast.ArrayConstructor;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.Attributes;
import org.eolang.opeo.ast.ClassField;
import org.eolang.opeo.ast.Constant;
import org.eolang.opeo.ast.Field;
import org.eolang.opeo.ast.FieldAssignment;
import org.eolang.opeo.ast.Invocation;
import org.eolang.opeo.ast.Literal;
import org.eolang.opeo.ast.LocalVariable;
import org.eolang.opeo.ast.Opcode;
import org.eolang.opeo.ast.Owner;
import org.eolang.opeo.ast.Popped;
import org.eolang.opeo.ast.Root;
import org.eolang.opeo.ast.StaticInvocation;
import org.eolang.opeo.ast.StoreArray;
import org.eolang.opeo.ast.This;
import org.eolang.opeo.ast.VariableAssignment;
import org.hamcrest.Description;
import org.hamcrest.MatcherAssert;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DecompilerMachine}.
 * @since 0.1
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods"})
final class DecompilerMachineTest {

    /**
     * Test decompilation of instance call instructions.
     * <p>
     *     {@code
     *       new A().bar();
     *     }
     * </p>
     */
    @Test
    void decompilesSimpleInstanceCall() {
        Assertions.assertDoesNotThrow(
            () -> new DecompilerMachine().decompile(
                new OpcodeInstruction(Opcodes.NEW, "A"),
                new OpcodeInstruction(Opcodes.DUP),
                new OpcodeInstruction(Opcodes.INVOKESPECIAL, "A", "<init>", "()V", false),
                new OpcodeInstruction(Opcodes.INVOKEVIRTUAL, "A", "bar", "()V", false)
            ),
            "Can't decompile method call instructions for 'new A().bar();'"
        );
    }

    /**
     * Test decompilation of instance call instructions with arguments.
     * <p>
     *     {@code
     *       new A(28).bar(29);
     *     }
     * </p>
     */
    @Test
    void decompilesInstanceCallWithArguments() {
        Assertions.assertDoesNotThrow(
            () -> new DecompilerMachine().decompile(
                new OpcodeInstruction(Opcodes.NEW, "A"),
                new OpcodeInstruction(Opcodes.DUP),
                new OpcodeInstruction(Opcodes.BIPUSH, 28),
                new OpcodeInstruction(Opcodes.INVOKESPECIAL, "A", "<init>", "(I)V", false),
                new OpcodeInstruction(Opcodes.BIPUSH, 29),
                new OpcodeInstruction(Opcodes.INVOKEVIRTUAL, "A", "bar", "(I)V", false)
            ),
            "Can't decompile method call instructions for 'new A(28).bar(29);'"
        );
    }

    /**
     * Test decompilation of nested instance call instructions with arguments.
     * <p>
     *     {@code
     *        foo(bar()) + 3;
     *     }
     * </p>
     */
    @Test
    void decompilesNestedInstanceCallWithArguments() {
        Assertions.assertDoesNotThrow(
            () -> new DecompilerMachine().decompile(
                new OpcodeInstruction(Opcodes.ALOAD, 0),
                new OpcodeInstruction(Opcodes.ALOAD, 0),
                new OpcodeInstruction(Opcodes.INVOKEVIRTUAL, "App", "bar", "()I", false),
                new OpcodeInstruction(Opcodes.INVOKEVIRTUAL, "App", "foo", "(I)I", false),
                new OpcodeInstruction(Opcodes.ICONST_3),
                new OpcodeInstruction(Opcodes.IADD)
            ),
            "Can't decompile method call instructions for 'foo(bar()) + 3;'"
        );
    }

    /**
     * Test decompilation of instance field access.
     * <p>
     *     {@code
     *       this.a + this.b;
     *     }
     * </p>
     */
    @Test
    void decompilesFieldAccess() {
        Assertions.assertDoesNotThrow(
            () -> new DecompilerMachine().decompile(
                new OpcodeInstruction(Opcodes.ALOAD, 0),
                new OpcodeInstruction(Opcodes.GETFIELD, "App", "a", "I"),
                new OpcodeInstruction(Opcodes.ALOAD, 0),
                new OpcodeInstruction(Opcodes.GETFIELD, "App", "b", "I"),
                new OpcodeInstruction(Opcodes.IADD)
            ),
            "Can't decompile field access instructions for 'this.a + this.b;'"
        );
    }

    /**
     * Test decompilation of instance field access and method invocation.
     * <p>
     *     {@code
     *       this.a.intValue() + 1;
     *     }
     * </p>
     */
    @Test
    void decompilesFieldAccessAndMethodInvocation() {
        Assertions.assertDoesNotThrow(
            () -> new DecompilerMachine().decompile(
                new OpcodeInstruction(Opcodes.ALOAD, 0),
                new OpcodeInstruction(Opcodes.GETFIELD, "App", "a", "Ljava/lang/Integer;"),
                new OpcodeInstruction(Opcodes.INVOKEVIRTUAL, "App", "intValue", "()I", false),
                new OpcodeInstruction(Opcodes.ICONST_1),
                new OpcodeInstruction(Opcodes.IADD)
            ),
            "Can't decompile field access and method invocation instructions for 'this.a.intValue() + 1;'"
        );
    }

    /**
     * Decimpilation test of a simple method with "if" clause.
     * <p>
     *     {@code
     *         public int get() {
     *         if (d <= 0) {
     *             return d;
     *         }
     *         return new A(d - 1).get();
     *         }
     *     }
     * </p>
     */
    @Test
    void decompilesIfStatement() {
        final AllLabels labels = new AllLabels();
        final Label label = labels.label("66 6F 6F");
        Assertions.assertDoesNotThrow(
            () -> {
                new DecompilerMachine().decompile(
                    new OpcodeInstruction(Opcodes.ALOAD, 0),
                    new OpcodeInstruction(Opcodes.GETFIELD, "org/eolang/other/A", "d", "I"),
                    new OpcodeInstruction(Opcodes.IFGT, label),
                    new OpcodeInstruction(Opcodes.ALOAD, 0),
                    new OpcodeInstruction(Opcodes.GETFIELD, "org/eolang/other/A", "d", "I"),
                    new OpcodeInstruction(Opcodes.IRETURN),
                    new LabelInstruction(label),
                    new OpcodeInstruction(Opcodes.NEW, "org/eolang/other/A"),
                    new OpcodeInstruction(Opcodes.DUP),
                    new OpcodeInstruction(Opcodes.ALOAD, 0),
                    new OpcodeInstruction(Opcodes.GETFIELD, "org/eolang/other/A", "d", "I"),
                    new OpcodeInstruction(Opcodes.ICONST_1),
                    new OpcodeInstruction(Opcodes.ISUB),
                    new OpcodeInstruction(
                        Opcodes.INVOKESPECIAL, "org/eolang/other/A", "<init>", "(I)V", false
                    ),
                    new OpcodeInstruction(
                        Opcodes.INVOKEVIRTUAL, "org/eolang/other/A", "get", "()I", false
                    ),
                    new OpcodeInstruction(Opcodes.IRETURN)
                );
            },
            "Compiles without exceptions"
        );
    }

    @Test
    void decompilesInvokeVirtual() {
        Assertions.assertDoesNotThrow(
            () ->
                new Xembler(
                    new DecompilerMachine()
                        .decompile(
                            new OpcodeInstruction(Opcodes.LLOAD, 4),
                            new OpcodeInstruction(Opcodes.ALOAD, 1),
                            new OpcodeInstruction(Opcodes.INVOKEVIRTUAL, "App", "run", "()I", false)
                        )
                ).xml(),
            "Can't decompile invoke virtual"
        );
    }

    @Test
    void decompilesArrayCreation() throws ImpossibleModificationException {
        final String type = "java/lang/Object";
        MatcherAssert.assertThat(
            "Can't decompile array creation",
            new Xembler(
                new DecompilerMachine()
                    .decompile(
                        new OpcodeInstruction(Opcodes.ICONST_2),
                        new OpcodeInstruction(Opcodes.ICONST_3),
                        new OpcodeInstruction(Opcodes.IADD),
                        new OpcodeInstruction(Opcodes.ANEWARRAY, type)
                    )
            ).xml(),
            new SameXml(
                new Xembler(
                    new Root(
                        new ArrayConstructor(
                            new Add(new Literal(2), new Literal(3)),
                            type
                        )
                    ).toXmir()
                ).xml()
            )
        );
    }

    @Test
    void decompilesArrayInsertion() throws ImpossibleModificationException {
        final String type = "java/lang/Object";
        MatcherAssert.assertThat(
            "Can't decompile array insertion",
            new Xembler(
                new DecompilerMachine()
                    .decompile(
                        new OpcodeInstruction(Opcodes.ICONST_2),
                        new OpcodeInstruction(Opcodes.ANEWARRAY, type),
                        new OpcodeInstruction(Opcodes.DUP),
                        new OpcodeInstruction(Opcodes.ICONST_0),
                        new OpcodeInstruction(Opcodes.ALOAD, 0),
                        new OpcodeInstruction(Opcodes.AASTORE)
                    )
            ).xml(),
            new SameXml(
                new Xembler(
                    new Root(
                        new StoreArray(
                            new ArrayConstructor(new Literal(2), type),
                            new Literal(0),
                            new This()
                        )
                    ).toXmir()
                ).xml()
            )
        );
    }

    @Test
    void decompilesVariableAssignment() {
        MatcherAssert.assertThat(
            "Can't decompile variable assignment",
            new DecompilerMachine().decompile(
                new OpcodeInstruction(Opcodes.ILOAD, 1),
                new OpcodeInstruction(Opcodes.ISTORE, 2),
                new OpcodeInstruction(Opcodes.ILOAD, 2),
                new OpcodeInstruction(Opcodes.ICONST_2),
                new OpcodeInstruction(Opcodes.IADD),
                new OpcodeInstruction(Opcodes.ISTORE, 2)
            ),
            new SameNode(
                new Root(
                    new VariableAssignment(
                        new LocalVariable(2, Type.INT_TYPE),
                        new LocalVariable(1, Type.INT_TYPE)
                    ),
                    new VariableAssignment(
                        new LocalVariable(2, Type.INT_TYPE),
                        new Add(
                            new LocalVariable(2, Type.INT_TYPE),
                            new Literal(2)
                        )
                    )
                )
            )
        );
    }

    @Test
    void decompilesFieldAssignment() {
        final String app = "App";
        final String name = "a";
        final String type = "I";
        MatcherAssert.assertThat(
            "Can't decompile assignment",
            new DecompilerMachine().decompile(
                new OpcodeInstruction(Opcodes.ALOAD, 0),
                new OpcodeInstruction(Opcodes.ILOAD, 1),
                new OpcodeInstruction(Opcodes.PUTFIELD, app, name, type)
            ),
            new SameNode(
                new Root(
                    new FieldAssignment(
                        new Field(
                            new This(),
                            new Attributes().name(name).owner(app).descriptor(type)
                        ),
                        new LocalVariable(1, Type.INT_TYPE)
                    )
                )
            )
        );
    }

    @Test
    void decompilesVarargInvocation() {
        final String type = "java/lang/Object";
        MatcherAssert.assertThat(
            "Can't decompile vararg invocation",
            new DecompilerMachine(Collections.singletonMap("counting", "false"))
                .decompile(
                    new OpcodeInstruction(
                        Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"
                    ),
                    new OpcodeInstruction(Opcodes.LDC, "Number is %s"),
                    new OpcodeInstruction(Opcodes.ICONST_1),
                    new OpcodeInstruction(Opcodes.ANEWARRAY, type),
                    new OpcodeInstruction(Opcodes.DUP),
                    new OpcodeInstruction(Opcodes.ICONST_0),
                    new OpcodeInstruction(Opcodes.ICONST_2),
                    new OpcodeInstruction(
                        Opcodes.INVOKESTATIC,
                        "java/lang/Integer",
                        "valueOf",
                        "(I)Ljava/lang/Integer;",
                        false
                    ),
                    new OpcodeInstruction(Opcodes.AASTORE),
                    new OpcodeInstruction(
                        Opcodes.INVOKEVIRTUAL,
                        "java/io/PrintStream",
                        "printf",
                        "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;",
                        false
                    ),
                    new OpcodeInstruction(Opcodes.POP),
                    new OpcodeInstruction(Opcodes.RETURN)
                ),
            new SameNode(
                new Root(
                    new Popped(
                        new Invocation(
                            new ClassField(
                                "java/lang/System",
                                "out",
                                "Ljava/io/PrintStream;"
                            ),
                            new Attributes()
                                .name("printf")
                                .descriptor(
                                    "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;"
                                )
                                .owner("java/io/PrintStream")
                                .interfaced(false),
                            new Constant("Number is %s"),
                            new StoreArray(
                                new ArrayConstructor(
                                    new Literal(1),
                                    type
                                ),
                                new Literal(0),
                                new StaticInvocation(
                                    new Attributes()
                                        .owner("java/lang/Integer")
                                        .name("valueOf")
                                        .descriptor("(I)Ljava/lang/Integer;")
                                        .interfaced(false),
                                    new Owner("java/lang/Integer"),
                                    new Literal(2)
                                )
                            )
                        )),
                    new Opcode(Opcodes.RETURN, false)
                )
            )
        );
    }

    /**
     * Matcher for the same node.
     * @since 0.2
     */
    private static final class SameNode extends TypeSafeMatcher<Iterable<Directive>> {

        /**
         * Expected node.
         */
        private final AstNode expected;

        /**
         * Expected node.
         */
        private final AtomicReference<String> exp;

        /**
         * Actual node.
         */
        private final AtomicReference<String> actual;

        /**
         * Constructor.
         * @param expected Expected node.
         */
        private SameNode(final AstNode expected) {
            this.expected = expected;
            this.exp = new AtomicReference<>();
            this.actual = new AtomicReference<>();
        }

        @Override
        public void describeTo(final Description description) {
            description.appendValue(this.exp.get());
        }

        @Override
        public boolean matchesSafely(final Iterable<Directive> item) {
            try {
                final String xactual = new Xembler(item).xml();
                final String xpected = new Xembler(this.expected.toXmir()).xml();
                this.actual.set(xactual);
                this.exp.set(xpected);
                return new SameXml(xpected).matchesSafely(xactual);
            } catch (final ImpossibleModificationException exception) {
                throw new IllegalStateException(
                    String.format(
                        "Can't convert to XML the following instructions: %n%s%n",
                        item
                    ),
                    exception
                );
            }
        }

        @Override
        public void describeMismatchSafely(
            final Iterable<Directive> item, final Description mismatch
        ) {
            mismatch.appendText("was ").appendValue(this.actual.get());
        }

    }
}
