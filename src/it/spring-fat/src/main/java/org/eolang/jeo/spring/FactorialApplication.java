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
package org.eolang.jeo.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Application Entry Point.
 * @since 0.2
 */
@SpringBootApplication

/**
 @ComponentScan( basePackages = {
 //    "org.springframework.boot",
 //    "org.springframework.boot.web",
 //    "org.springframework.boot.logging",

 "org.springframework.aop",
 "org.springframework.beans",
 //        "org.springframework.context",
 //        "org.springframework.core",
 //        "org.springframework.expression",
 "org.springframework.test",
 "org.springframework.web",



 //    "org.springframework.aop",
 //    "org.springframework.asm",
 //    "org.springframework.beans",
 //    "org.springframework.cache",
 //    "org.springframework.cglib",
 //    "org.springframework.context",
 //    "org.springframework.core",
 //    "org.springframework.ejb",
 //    "org.springframework.expression",
 //    "org.springframework.format",
 //    "org.springframework.http",
 //    "org.springframework.instrument",
 //    "org.springframework.jmx",
 //    "org.springframework.jndi",
 //    "org.springframework.lang",
 //    "org.springframework.mock",
 //    "org.springframework.objenesis",
 //    "org.springframework.remoting",

 //"org.springframework.scheduling",

 //    "org.springframework.scripting",
 //    "org.springframework.stereotype",
 //    "org.springframework.test",
 //    "org.springframework.ui",
 //    "org.springframework.util",
 //    "org.springframework.validation",
 //    "org.springframework.web",

 //    aop
 //asm
 //beans
 //boot
 //cache
 //cglib
 //context
 //core
 //ejb
 //expression
 //format
 //http
 //instrument
 //jmx
 //jndi
 //lang
 //mock
 //objenesis
 //remoting
 //scheduling
 //scripting
 //stereotype
 //test
 //ui
 //util
 //validation
 //web
 "net.minidev",
 "com.fasterxml",
 "ch.qos",
 "jakarta.annotation",
 "ch.qos.logback",
 "ch",
 "com",
 "javax",
 "net",
 "org.aopalliance",
 "org.apache",
 "org.assertj",
 "org.eolang",
 "org.hamcrest",
 "org.json",
 "org.junit",
 "org.mockito",
 "org.objectweb",
 "org.objenesis",
 "org.opentest4j",
 "org.skyscreamer",
 "org.slf4j",
 "org.xmlunit",
 "org.yaml",
 })
 */


@ComponentScan(
    basePackages = {
        "org.springframework.boot.web",
        "org.springframework.boot.logging",
    }
)
public class FactorialApplication {

    /**
     * Entry point for Factorial Spring Application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(FactorialApplication.class, args);
    }

}
