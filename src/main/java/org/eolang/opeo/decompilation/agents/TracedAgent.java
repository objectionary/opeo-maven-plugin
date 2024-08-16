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
package org.eolang.opeo.decompilation.agents;

import com.jcabi.log.Logger;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.logging.Level;
import org.eolang.opeo.decompilation.DecompilerState;

/**
 * Agent that knows how to log additional information about a decompilation process.
 *
 * @since 0.4
 */
public final class TracedAgent implements DecompilationAgent {

    private final DecompilationAgent original;
    private final Output output;

    public TracedAgent(final DecompilationAgent original, final Output output) {
        this.original = original;
        this.output = output;
    }

    @Override
    public void handle(final DecompilerState state) {
        this.output.write(
            String.format(
                "%s starts to decompile. Initial state: %s",
                this.original.getClass().getSimpleName(),
                state.stack()
            )
        );
        this.original.handle(state);
        this.output.write(
            String.format(
                "%s ends decompilation. Final state: %s",
                this.original.getClass().getSimpleName(),
                state.stack()
            )
        );
    }

    @Override
    public Supported supported() {
        return this.original.supported();
    }


    private interface Output {
        void write(final String message);

    }

    public static class Stdout implements Output {
        @Override
        public void write(final String message) {
            System.out.println(message);
        }
    }


    public static class Log implements Output {

        private final Level level;

        public Log() {
            this(Level.INFO);
        }

        public Log(final Level level) {
            this.level = level;
        }

        @Override
        public void write(final String message) {
            Logger.log(this.level, this, message);
        }
    }

    public static class Container implements Output {

        private Deque<String> queue;

        public Container() {
            this(new LinkedList<>());
        }

        public Container(final Deque<String> queue) {
            this.queue = queue;
        }

        @Override
        public void write(final String message) {
            this.queue.push(message);
        }

        public Collection<String> messages() {
            return Collections.unmodifiableCollection(this.queue);
        }
    }


}
