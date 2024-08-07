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

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.Collections;
import org.eolang.jeo.representation.xmir.XmlMethod;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.jeo.representation.xmir.XmlProgram;
import org.eolang.opeo.decompilation.DecompilerMachine;
import org.eolang.opeo.decompilation.LocalVariables;
import org.objectweb.asm.Type;
import org.w3c.dom.Node;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Decompiler that gets jeo instructions and decompiles them into high-level EO constructs.
 *
 * @since 0.1
 */
public final class JeoDecompiler {

    /**
     * Program in XMIR format received from jeo maven plugin.
     */
    private final XML prog;

    /**
     * Program package.
     */
    private final String pckg;

    /**
     * Constructor.
     *
     * @param prog Program in XMIR format received from jeo maven plugin.
     */
    public JeoDecompiler(final XML prog) {
        this(prog, "java.lang.Object");
    }

    /**
     * Constructor.
     *
     * @param prog Program in XMIR format received from jeo maven plugin.
     * @param pckg Program package.
     */
    public JeoDecompiler(final XML prog, final String pckg) {
        this.prog = prog;
        this.pckg = pckg;
    }

    /**
     * Decompile program.
     *
     * @return EO program.
     */
    public XML decompile() {
        final Node node = this.prog.node();
        final String descriptor = Type.getObjectType(
            this.pckg.replace(".xmir", "").replace(".", "/")
        ).getDescriptor();
        new XmlProgram(node).top().methods()
            .forEach(method -> this.decompile(method, descriptor));
        return new XMLDocument(node);
    }

    /**
     * Decompile method.
     *
     * @param method Method.
     * @param clazz Class name.
     */
    private void decompile(final XmlMethod method, final String clazz) {
        try {
            if (!method.instructions().isEmpty()) {
                method.withInstructions(
                    new XmlNode(
                        new Xembler(
                            new DecompilerMachine(
                                new LocalVariables(method.access(), method.descriptor(), clazz),
                                Collections.singletonMap("counting", "true")
                            ).decompile(new JeoInstructions(method).instructions()),
                            new Transformers.Node()
                        ).xmlQuietly()
                    ).children().toArray(XmlNode[]::new)
                );
            }
        } catch (final ClassCastException | IllegalStateException exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to decompile method '%s' from the following XMIR: '%s'",
                    method,
                    this.prog
                ),
                exception
            );
        }
    }
}
