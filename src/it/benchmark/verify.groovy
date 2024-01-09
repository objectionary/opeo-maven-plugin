/*
 * MIT License
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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
// Check logs first.
String log = new File(basedir, 'build.log').text;
// Check decompilation ('decompile' goal.)
assert log.contains("Decompiling EO sources from")
assert log.contains("Saving new decompiled EO sources to")
assert log.contains("Decompiled")
assert log.contains("Decompiled 1 EO sources")
// Check compilation ('compile' goal.)
assert log.contains("Compiling EO sources from")
assert log.contains("Saving new compiled EO sources to")
assert log.contains("Compiled app.eo (545 bytes)")
assert log.contains("Compiled main.eo (545 bytes)")
assert log.contains("Compiled 2 EO sources")
// Check success.
assert log.contains("BUILD SUCCESS")

true