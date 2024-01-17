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
package org.eolang.opeo.jeo;

import org.eolang.jeo.representation.xmir.XmlBytecodeEntry;
import org.eolang.jeo.representation.xmir.XmlInstruction;
import org.eolang.jeo.representation.xmir.XmlLabel;
import org.eolang.jeo.representation.xmir.XmlMethod;
import org.eolang.opeo.Instruction;

/**
 * Class that represents the instructions provided by jeo maven plugin.
 * @since 0.1
 */
public final class JeoInstructions {

    /**
     * Method.
     */
    private final XmlMethod method;

    /**
     * Constructor.
     * @param method Method.
     */
    public JeoInstructions(final XmlMethod method) {
        this.method = method;
    }

    /**
     * Parse instructions.
     * @return Instructions array.
     */
    public Instruction[] instructions() {
        return this.method.instructions().stream()
            .map(JeoInstructions::toInstruction)
            .toArray(Instruction[]::new);
    }

    /**
     * Convert XML instruction to instruction.
     * @param entry XML instruction.
     * @return Instruction.
     */
    private static Instruction toInstruction(final XmlBytecodeEntry entry) {
        if (entry instanceof XmlInstruction) {
            return new JeoInstruction((XmlInstruction) entry);
        } else if (entry instanceof XmlLabel) {
            return new JeoLabel((XmlLabel) entry);
        } else {
            throw new IllegalArgumentException(
                String.format("Unknown bytecode entry: %s, class is %s", entry, entry.getClass())
            );
        }
    }
}
