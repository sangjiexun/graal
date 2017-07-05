/*
 * Copyright (c) 2017, Oracle and/or its affiliates.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.truffle.llvm.parser.metadata.debuginfo;

import com.oracle.truffle.llvm.parser.metadata.MDBaseNode;
import com.oracle.truffle.llvm.parser.metadata.MDGlobalVariable;
import com.oracle.truffle.llvm.parser.metadata.MDSymbolReference;
import com.oracle.truffle.llvm.parser.metadata.MDValue;
import com.oracle.truffle.llvm.runtime.types.symbols.Symbol;

final class MDSymbolExtractor implements MDFollowRefVisitor {

    private static final Symbol DEFAULT_SYMBOL = null;

    static Symbol getSymbol(MDBaseNode container) {
        if (container == null) {
            return DEFAULT_SYMBOL;
        }

        final MDSymbolExtractor visitor = new MDSymbolExtractor();
        container.accept(visitor);
        return visitor.sym;
    }

    private MDSymbolExtractor() {
    }

    private Symbol sym = DEFAULT_SYMBOL;

    @Override
    public void visit(MDValue mdVal) {
        mdVal.getValue().accept(this);
    }

    @Override
    public void visit(MDSymbolReference symRef) {
        if (symRef.isPresent()) {
            sym = symRef.get();
        }
    }

    @Override
    public void visit(MDGlobalVariable mdGlobal) {
        mdGlobal.getVariable().accept(this);
    }
}
