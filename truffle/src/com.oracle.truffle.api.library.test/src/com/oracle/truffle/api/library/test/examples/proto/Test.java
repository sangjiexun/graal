/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oracle.truffle.api.library.test.examples.proto;

import static com.oracle.truffle.api.library.Libraries.createCached;
import static com.oracle.truffle.api.library.Libraries.getUncached;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.Message;
import com.oracle.truffle.api.library.ReflectionLibrary;
import com.oracle.truffle.api.nodes.Node;

public class Test {

    public static void main(String[] args) throws Exception {
        Object o = new TestObject();

        System.out.println(getUncached(MyLibrary.class, o).foo(o, 42));
        System.out.println(createCached(MyLibrary.class, o).foo(o, 42));
        System.out.println(getUncached(ReflectionLibrary.class, o).send(o, Message.resolve(MyLibrary.class, "foo"), 42));

        o = new Object();
        System.out.println(getUncached(MyLibrary.class, o).bar(o, 42));
        System.out.println(createCached(MyLibrary.class, o).bar(o, 42));
        System.out.println(getUncached(ReflectionLibrary.class, o).send(o, Message.resolve(MyLibrary.class, "bar"), 42));

        o = new TestObjectProxy(new TestObject());
        System.out.println(getUncached(MyLibrary.class, o).foo(o, 42));
        System.out.println(createCached(MyLibrary.class, o).foo(o, 42));
        System.out.println(getUncached(ReflectionLibrary.class, o).send(o, Message.resolve(MyLibrary.class, "foo"), 42));

        o = new TestObjectProxy(new Object());
        System.out.println(getUncached(MyLibrary.class, o).bar(o, 42));
        System.out.println(createCached(MyLibrary.class, o).bar(o, 42));
        System.out.println(getUncached(ReflectionLibrary.class, o).send(o, Message.resolve(MyLibrary.class, "bar"), 42));
    }

    abstract static class TestNode extends Node {

        abstract Object execute(Object arg);

        @Specialization(limit = "3")
        Object s0(Object arg, @CachedLibrary("arg") MyLibrary lib) {
            return arg;
        }

    }

}
