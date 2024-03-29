package edu.berkeley.cs.parser;

import edu.berkeley.cs.builtin.objects.mutable.CObject;
import edu.berkeley.cs.builtin.objects.mutable.SymbolToken;
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
    public final int offset = 500;
    private static OperatorPrecedence ourInstance = new OperatorPrecedence();
    private TIntIntHashMap precedenceTable = new TIntIntHashMap();
    // odd number => left-associative
    // even number => right associative
    // 0 is the default precedence


    public static OperatorPrecedence getInstance() {
        return ourInstance;
    }

    private OperatorPrecedence() {
//       precedenceTable.put(SymbolTable.getInstance().getId(","),90-offset);

//        precedenceTable.put(SymbolTable.getInstance().token,50-offset);

        precedenceTable.put(SymbolTable.getInstance().assign.symbol,101-offset);

//        precedenceTable.put(SymbolTable.getInstance().getId("?"),-111-offset);

        precedenceTable.put(SymbolTable.getInstance().getId("||"),120-offset);
        precedenceTable.put(SymbolTable.getInstance().getId("&&"),130-offset);


        precedenceTable.put(SymbolTable.getInstance().bar.symbol,140-offset);
        precedenceTable.put(SymbolTable.getInstance().getId("^"),150-offset);
        precedenceTable.put(SymbolTable.getInstance().getId("&"),160-offset);

        precedenceTable.put(SymbolTable.getInstance().eq.symbol,170-offset);
        precedenceTable.put(SymbolTable.getInstance().ne.symbol,170-offset);

        precedenceTable.put(SymbolTable.getInstance().getId("<"),180-offset);
        precedenceTable.put(SymbolTable.getInstance().getId("<="),180-offset);
        precedenceTable.put(SymbolTable.getInstance().getId(">"),180-offset);
        precedenceTable.put(SymbolTable.getInstance().getId(">="),180-offset);

        precedenceTable.put(SymbolTable.getInstance().getId(">>"),190-offset);
        precedenceTable.put(SymbolTable.getInstance().getId("<<"),190-offset);
        
        precedenceTable.put(SymbolTable.getInstance().getId("+"),200-offset);
        precedenceTable.put(SymbolTable.getInstance().minus.symbol,200-offset);

        precedenceTable.put(SymbolTable.getInstance().getId("*"),210-offset);
        precedenceTable.put(SymbolTable.getInstance().getId("%"),210-offset);
        precedenceTable.put(SymbolTable.getInstance().getId("/"),210-offset);

//        precedenceTable.put(SymbolTable.getInstance().not.symbol,221-offset);
//        precedenceTable.put(SymbolTable.getInstance().getId("~"),221-offset);

//        precedenceTable.put(SymbolTable.getInstance().semi.symbol,81-offset);
//        precedenceTable.put(SymbolTable.getInstance().newline.symbol,81-offset);
//        precedenceTable.put(SymbolTable.getInstance().getId("."),230-offset);
//        precedenceTable.put(SymbolTable.getInstance().getId("("),230-offset);
//        precedenceTable.put(SymbolTable.getInstance().getId("["),230-offset);
    }

    public boolean isShift(int exprPrecedence, CObject shiftOp) {
        if (shiftOp instanceof SymbolToken) {
            int sym2 = precedenceTable.get(((SymbolToken)shiftOp).symbol);
            return isShift(exprPrecedence,sym2);
        }
        return false;
    }

    public boolean isShift(int exprPrecedence, int sym2) {
//        if (sym2==10) {
//            System.out.println("Prev ="+exprPrecedence);
//        }
        if (sym2 > exprPrecedence || (exprPrecedence == sym2 && sym2 % 2 == 1)) {
            return true;
        }
        return false;
    }

    public int getPrecedence(CObject t) {
        if (t instanceof SymbolToken) {
            return precedenceTable.get(((SymbolToken)t).symbol);
        }
        return 0;
    }

    public int getPrecedence(int sym) {
        return precedenceTable.get(sym);
    }

    public boolean isDefined(String op) {
        return precedenceTable.containsKey(SymbolTable.getInstance().getId(op));
    }

}
