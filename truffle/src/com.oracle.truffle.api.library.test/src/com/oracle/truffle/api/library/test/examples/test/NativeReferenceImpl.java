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
package com.oracle.truffle.api.library.test.examples.test;

import java.util.Arrays;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;

@ExportLibrary(NativeReferenceLibrary.class)
public class NativeReferenceImpl {

    public Object[] table;

    NativeReferenceImpl(Object[] table) {
        this.table = table;
    }

    @ExportMessage
    final Object read(int offset, @Cached IndexComputation compute) {
        return table[compute.execute(offset)];
    }

    @ExportMessage
    static class WriteNode extends Node {

        @Specialization(guards = "offset < receiver.table.length")
        static void doInBounds(NativeReferenceImpl receiver, int offset, Object value,
                        @Cached IndexComputation compute) {
            receiver.table[compute.execute(offset)] = value;
        }

        @Specialization(guards = "offset >= receiver.table.length")
        static void doOutOfBounds(NativeReferenceImpl receiver, int offset, Object value,
                        @Cached IndexComputation compute) {
            receiver.table = Arrays.copyOf(receiver.table, offset + 1);
        }
    }

    @GenerateUncached
    abstract static class IndexComputation extends Node {

        abstract int execute(int index);

        @Specialization(guards = "value == cachedValue")
        static int doCached(int value,
                        @Cached("value") int cachedValue,
                        @Cached("doGeneric(value)") int cachedResult) {
            return cachedResult;
        }

        @Specialization(replaces = "doCached")
        static int doGeneric(int value) {
            return value / 8;
        }

    }

}
