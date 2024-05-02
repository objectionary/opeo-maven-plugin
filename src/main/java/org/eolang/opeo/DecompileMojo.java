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
package org.eolang.opeo;

import com.jcabi.log.Logger;
import java.io.File;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eolang.opeo.decompilation.Decompiler;
import org.eolang.opeo.decompilation.DummyDecompiler;

/**
 * Decompiles bytecode in EO representation into high-level EO representation.
 * The input for this mojo produced by the "jeo-maven-plugin":
 * <a href="https://github.com/objectionary/jeo-maven-plugin">link</a>
 *
 * @since 0.1
 */
@Mojo(name = "decompile", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public final class DecompileMojo extends AbstractMojo {

    /**
     * Source directory.
     * Where to take jeo xmir from.
     *
     * @since 0.2.0
     * @checkstyle MemberNameCheck (6 lines)
     */
    @Parameter(
        property = "opeo.decompile.sourcesDir",
        defaultValue = "${project.build.directory}/generated-sources/jeo-xmir"
    )
    private File sourcesDir;

    /**
     * Target directory.
     * Where to save opeo decompiler representations to.
     *
     * @since 0.2.0
     * @checkstyle MemberNameCheck (6 lines)
     */
    @Parameter(
        property = "opeo.decompile.outputDir",
        defaultValue = "${project.build.directory}/generated-sources/opeo-xmir"
    )
    private File outputDir;

    /**
     * Whether the plugin is disabled.
     * If it's disabled, then it won't do anything.
     *
     * @since 0.2.0
     * @checkstyle MemberNameCheck (6 lines)
     */
    @Parameter(
        property = "opeo.decompile.disabled",
        defaultValue = "false"
    )
    private boolean disabled;

    @Override
    public void execute() {
        if (this.disabled) {
            Logger.info(this, "Decompiler is disabled");
            new DummyDecompiler(this.sourcesDir.toPath(), this.outputDir.toPath()).decompile();
        } else {
            new Decompiler(this.sourcesDir.toPath(), this.outputDir.toPath()).decompile();
        }
    }
}
