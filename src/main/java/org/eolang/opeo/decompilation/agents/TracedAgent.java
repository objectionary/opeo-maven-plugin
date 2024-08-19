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
import org.eolang.opeo.decompilation.DecompilerState;

/**
 * Agent that knows how to log additional information about a decompilation process.
 *
 * @since 0.4
 * @todo #393:90min Check Applicability of The Original Agent Before Printing.
 *  The {@link TracedAgent} prints the stack and instructions before and after the original agent
 *  tries to handle the state.
 *  So it prints the state even if the original agent is not applicable to the state.
 *  We should check if the original agent is applicable to the state before printing the state.
 *  This will avoid unnecessary prints and make the logs more readable.
 */
public final class TracedAgent implements DecompilationAgent {

    /**
     * Original agent.
     */
    private final DecompilationAgent original;

    /**
     * Output target.
     */
    private final Output output;

    /**
     * Constructor.
     * @param original Original agent.
     */
    public TracedAgent(final DecompilationAgent original) {
        this(original, new Log());
    }

    /**
     * Constructor.
     * @param original Original agent.
     * @param output Output target.
     */
    public TracedAgent(final DecompilationAgent original, final Output output) {
        this.original = original;
        this.output = output;
    }

    @Override
    public void handle(final DecompilerState state) {
        final String name = this.original.getClass().getSimpleName();
        this.output.write(
            String.format(
                "Stack before %s: [%s]",
                name,
                state.stack().pretty()
            )
        );
        this.output.write(
            String.format(
                "Instructions before %s: [%s]",
                name,
                state.prettyOpcodes()
            )
        );
        this.original.handle(state);
        this.output.write(
            String.format(
                "Stack after %s: [%s]",
                name,
                state.stack().pretty()
            )
        );
        this.output.write(
            String.format(
                "Instructions after %s: [%s]",
                name,
                state.prettyOpcodes()
            )
        );
    }

    @Override
    public Supported supported() {
        return this.original.supported();
    }

    /**
     * Output target.
     * Target for the output of the traced agent.
     * @since 0.4
     */
    private interface Output {

        /**
         * Write a message.
         * @param message Message to write.
         */
        void write(String message);

    }

    /**
     * Log output.
     * Output target that logs messages using DEBUG level.
     * @since 0.4
     */
    public static final class Log implements Output {

        @Override
        public void write(final String message) {
            Logger.debug(this, message);
        }
    }

    /**
     * Container output.
     * Output target that stores messages in a container.
     * @since 0.4
     */
    public static final class Container implements Output {

        /**
         * Message queue.
         */
        private Deque<String> queue;

        /**
         * Default constructor.
         */
        Container() {
            this(new LinkedList<>());
        }

        /**
         * Constructor.
         * @param queue Message queue.
         */
        Container(final Deque<String> queue) {
            this.queue = queue;
        }

        @Override
        public void write(final String message) {
            this.queue.push(message);
        }

        /**
         * Get all messages.
         * @return All messages.
         */
        Collection<String> messages() {
            return Collections.unmodifiableCollection(this.queue);
        }
    }
}
