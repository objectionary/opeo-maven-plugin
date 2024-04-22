/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 Objectionary.com
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
package org.eolang.jeo.spring;

/**
 * Implementation of factorial using pure OOP approach.
 * The methods in this class are going to be replaced by optimization.
 * @since 0.2
 */
class Factorial {
    /**
     * Current value.
     */
    private int d;

    /**
     * Constructor.
     * @param d Value.
     */
    Factorial(int d) {
        this.d = d;
    }

    /**
     * Get factorial.
     * @return Factorial.
     */
    public int get() {
        if (d <= 1) {
            return 1;
        }
        return new Factorial(d - 1).get() * d;
    }
}


