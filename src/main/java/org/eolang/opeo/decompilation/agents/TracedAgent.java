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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.eolang.opeo.decompilation.DecompilerState;

/**
 * Agent that knows how to log additional information about a decompilation process.
 *
 * @since 0.4
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
    public boolean appropriate(final DecompilerState state) {
        return this.original.appropriate(state);
    }

    @Override
    public Supported supported() {
        return this.original.supported();
    }

    @Override
    public void handle(final DecompilerState state) {
        if (this.appropriate(state)) {
            final String name = this.original.getClass().getSimpleName();
            this.output.register(this.original.getClass());
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
    }

    /**
     * Output target.
     * Target for the output of the traced agent.
     * @since 0.4
     */
    public interface Output {

        /**
         * Write a message.
         * @param message Message to write.
         */
        void write(String message);

        /**
         * Register an agent that was used.
         * @param agent Agent class used.
         */
        void register(Class<? extends DecompilationAgent> agent);

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

        @Override
        public void register(final Class<? extends DecompilationAgent> agent) {
            Logger.debug(this, "Agent used: %s", agent.getSimpleName());
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
         * Agents used.
         */
        private List<Class<? extends DecompilationAgent>> agents;

        /**
         * Default constructor.
         */
        public Container() {
            this(new LinkedList<>());
        }

        /**
         * Constructor.
         * @param queue Message queue.
         */
        Container(final Deque<String> queue) {
            this.queue = queue;
            this.agents = new ArrayList<>(0);
        }

        @Override
        public void write(final String message) {
            this.queue.push(message);
        }

        @Override
        public void register(final Class<? extends DecompilationAgent> agent) {
            this.agents.add(agent);
        }

        /**
         * Get all messages.
         * @return All messages.
         */
        public Collection<String> messages() {
            return Collections.unmodifiableCollection(this.queue);
        }

        /**
         * Get all agents used.
         * @return All agents used.
         */
        public List<String> agents() {
            return this.agents.stream().map(Class::getSimpleName).collect(Collectors.toList());
        }
    }
}
