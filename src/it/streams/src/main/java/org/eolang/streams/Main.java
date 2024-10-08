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
package org.eolang.streams;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String... args) {
        long start = System.currentTimeMillis();
        String[] strings = IntStream.range(0, 10)
            .mapToObj(i -> String.valueOf(i))
            .toArray(String[]::new);
        int sum = Arrays.stream(strings)
            .filter(s -> Boolean.valueOf(s.equals("")).equals(false))
            .mapToInt(s -> Integer.parseInt(s))
            .sum();
        System.out.printf("sum=%d time=%d%n", sum, System.currentTimeMillis() - start);
        // Here I test {@link Playground} class.
        System.out.printf("Playground is available %b%n", new Playground(0).isAvailable());
    }

    public String map(){
        return Stream.of("a", "b", "c")
            .map(String::toUpperCase)
            .collect(Collectors.joining(", "));
    }
}
