package edu.berkeley.cs.builtin.objects.preprocessor;

import edu.berkeley.cs.builtin.functions.*;
import edu.berkeley.cs.builtin.objects.CObject;
import edu.berkeley.cs.builtin.objects.Reference;
import edu.berkeley.cs.lexer.BufferedScanner;
import edu.berkeley.cs.lexer.Scanner;
import edu.berkeley.cs.parser.CallFrame;
import edu.berkeley.cs.parser.RuleNode;
import edu.berkeley.cs.parser.SymbolTable;
import edu.berkeley.cs.parser.TokenVisitor;

import java.util.ArrayList;
import java.util.LinkedList;

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
public class CompoundToken extends Token {
    private ArrayList<Token> tokens;
    public ArrayList<SymbolToken> parameters;
    private String file;

    public BufferedScanner getScanner() {
        return new BufferedScanner(tokens);
    }

    public CompoundToken(TokenEater ss,String file) {
        super(null);

        //rules = new RuleNode(null);
        tokens = ss.tokens;
        parameters = ss.parameters;
        this.file = file;
        int N = parameters.size();

        this.addNewRule();
        this.addSymbol(SymbolTable.getInstance().getId("("));
        for(int i=0; i<N; i++) {
            this.addMeta(SymbolTable.getInstance().argument);
            if (i<N-1)
                this.addSymbol(SymbolTable.getInstance().getId(","));
        }
        this.addSymbol(SymbolTable.getInstance().getId(")"));
        this.addAction(new DirectCall());

        this.addNewRule();
        this.addSymbol(SymbolTable.getInstance().getId("("));
        this.addMeta(SymbolTable.getInstance().argument);
        if (N > 0)
            this.addSymbol(SymbolTable.getInstance().getId(","));

        for(int i=0; i<N; i++) {
            this.addMeta(SymbolTable.getInstance().argument);
            if (i<N-1)
                this.addSymbol(SymbolTable.getInstance().getId(","));
        }
        this.addSymbol(SymbolTable.getInstance().getId(")"));
        this.addAction(new DirectCallWith());
    }

    public CObject execute(CObject LS) {
        String tmp = CObject.currentFile;
        CObject.currentFile = file;
        try {
        Scanner scnr = getScanner();
        CallFrame cf = new CallFrame(LS,scnr);
        return cf.interpret();
        } finally {
            CObject.currentFile = file;
        }
    }

    public CObject execute(CObject LS, LinkedList<CObject> args) {
        for(SymbolToken param:parameters) {
            Reference common;

            LS.addNewRule();
            LS.addSymbol(param.symbol);
            LS.addAction(new GetField(common = new Reference(args.removeFirst())));

            LS.addNewRule();
            LS.addSymbol(param.symbol);
            LS.addSymbol(SymbolTable.getInstance().getId("="));
            LS.addMeta(SymbolTable.getInstance().argument);
            LS.addAction(new PutField(common));
        }
        return execute(LS);
    }


    public Object accept(TokenVisitor v) {
        return v.visitCompoundToken(this);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{').append(' ');
        if (!parameters.isEmpty()) {
            sb.append('|');
        }
        for(SymbolToken param:parameters) {
            sb.append(param);
            sb.append(',');
        }
        if (!parameters.isEmpty()) {
            sb.append("|\n");
        }
        for(Token t:tokens) {
            sb.append(t.toString()).append(' ');
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

}
