package edu.berkeley.cs.parser;

import edu.berkeley.cs.builtin.objects.preprocessor.SymbolToken;
import edu.berkeley.cs.builtin.objects.preprocessor.Token;
import gnu.trove.map.hash.TIntIntHashMap;

/**
 * Copyright (c) 2006-2011,
 * Koushik Sen    <ksen@cs.berkeley.edu>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * <p/>
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * <p/>
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p/>
 * 3. The names of the contributors may not be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class OperatorPrecedence {
    private static OperatorPrecedence ourInstance = new OperatorPrecedence();
    private TIntIntHashMap precedenceTable = new TIntIntHashMap();
    // even number => left-associative
    // odd number => right associative


    public static OperatorPrecedence getInstance() {
        return ourInstance;
    }

    private OperatorPrecedence() {
        precedenceTable.put(SymbolTable.getInstance().getId(","),90);

        precedenceTable.put(SymbolTable.getInstance().getId("="),-105);

        precedenceTable.put(SymbolTable.getInstance().getId("&&"),120);
        precedenceTable.put(SymbolTable.getInstance().getId("||"),130);


        precedenceTable.put(SymbolTable.getInstance().getId("|"),140);
        precedenceTable.put(SymbolTable.getInstance().getId("^"),150);
        precedenceTable.put(SymbolTable.getInstance().getId("&"),160);

        precedenceTable.put(SymbolTable.getInstance().getId("=="),170);
        precedenceTable.put(SymbolTable.getInstance().getId("!="),170);

        precedenceTable.put(SymbolTable.getInstance().getId("<"),180);
        precedenceTable.put(SymbolTable.getInstance().getId("<="),180);
        precedenceTable.put(SymbolTable.getInstance().getId(">"),180);
        precedenceTable.put(SymbolTable.getInstance().getId(">="),180);

        precedenceTable.put(SymbolTable.getInstance().getId(">>"),190);
        precedenceTable.put(SymbolTable.getInstance().getId("<<"),190);
        precedenceTable.put(SymbolTable.getInstance().getId("+"),200);
        precedenceTable.put(SymbolTable.getInstance().getId("-"),200);

        precedenceTable.put(SymbolTable.getInstance().getId("*"),210);
        precedenceTable.put(SymbolTable.getInstance().getId("%"),210);
        precedenceTable.put(SymbolTable.getInstance().getId("/"),210);

        precedenceTable.put(SymbolTable.getInstance().getId("!"),215);

        precedenceTable.put(SymbolTable.getInstance().getId("."),220);
    }

    public boolean isShift(int exprPrecedence, Token shiftOp) {
        if (shiftOp instanceof SymbolToken) {
            int sym2 = precedenceTable.get(((SymbolToken)shiftOp).symbol);
            if (sym2 > exprPrecedence || (exprPrecedence == sym2 && sym2 % 2 == 1)) {
                return true;
            }
        }
        return false;
    }

    public Integer getPrecedence(Token t) {
        if (t instanceof SymbolToken) {
            return precedenceTable.get(((SymbolToken)t).symbol);
        }
        return 0;
    }
}
