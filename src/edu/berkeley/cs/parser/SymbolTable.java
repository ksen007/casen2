package edu.berkeley.cs.parser;

import edu.berkeley.cs.builtin.objects.preprocessor.SymbolToken;

import java.util.ArrayList;
import java.util.TreeMap;

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
public class SymbolTable {
    private static SymbolTable ourInstance = new SymbolTable();

    private TreeMap<String,Integer> symbolTable = new TreeMap<String, Integer>();
    private ArrayList<String> symbols = new ArrayList<String>();

    public int expr = getId("expr");
    public int token = getId("token");
    public int nl = getId("nl");
    public int eof = getId("eof");
    
    public SymbolToken self = new SymbolToken(null,getId("self"));
    public SymbolToken newline = new SymbolToken(null,getId("\n"));
    public SymbolToken var = new SymbolToken(null,getId("var"));
    public SymbolToken eq = new SymbolToken(null,getId("=="));
    public SymbolToken ne = new SymbolToken(null,getId("!="));
    public SymbolToken def = new SymbolToken(null,getId("def"));
    public SymbolToken assign = new SymbolToken(null,getId("="));
    public SymbolToken LS = new SymbolToken(null,getId("LS"));
    public SymbolToken DS = new SymbolToken(null,getId("DS"));
    public SymbolToken lparen = new SymbolToken(null,getId("("));
    public SymbolToken rparen = new SymbolToken(null,getId(")"));
    public SymbolToken dot = new SymbolToken(null,getId("."));
    public SymbolToken endef = new SymbolToken(null,getId("endef"));
    public SymbolToken comma = new SymbolToken(null,getId(","));
    public SymbolToken bar = new SymbolToken(null,getId("|"));
    public SymbolToken lcurly = new SymbolToken(null,getId("{"));
    public SymbolToken rcurly = new SymbolToken(null,getId("}"));
    public SymbolToken prototype = new SymbolToken(null,getId("prototype"));
    public SymbolToken exprToToken = new SymbolToken(null,getId("exprToToken"));
    public SymbolToken semi = new SymbolToken(null,getId(";"));
    public SymbolToken load = new SymbolToken(null,getId("load"));


    public SymbolToken If = new SymbolToken(null,getId("if"));
    public SymbolToken then = new SymbolToken(null,getId("then"));
    public SymbolToken Else = new SymbolToken(null,getId("else"));
    public SymbolToken While = new SymbolToken(null,getId("while"));
    public SymbolToken Try = new SymbolToken(null,getId("try"));
    public SymbolToken Catch = new SymbolToken(null,getId("catch"));
    public SymbolToken Throw = new SymbolToken(null,getId("throw"));
    public SymbolToken Return = new SymbolToken(null,getId("return"));
    public SymbolToken once = new SymbolToken(null,getId("once"));
    public SymbolToken print = new SymbolToken(null,getId("print"));
    public SymbolToken minus = new SymbolToken(null,getId("-"));
    public SymbolToken not = new SymbolToken(null,getId("!"));
    public SymbolToken New = new SymbolToken(null,getId("new"));
    public SymbolToken object = new SymbolToken(null,getId("Object"));
    public SymbolToken tokenToExpr = new SymbolToken(null,getId("tokenToExpr"));
    public SymbolToken Assert = new SymbolToken(null,getId("assert"));
    public SymbolToken printdeep = new SymbolToken(null,getId("printDeep"));
    public SymbolToken pound = new SymbolToken(null,getId("#"));
    public SymbolToken at = new SymbolToken(null,getId("@"));
    public SymbolToken orphan = new SymbolToken(null,getId("orphan"));
//    public int meta = getId("meta");
//    public int literal = getId("literal");
//    public int block = getId("block");
//    public int nl = getId("nl");

    public static SymbolTable getInstance() {
        return ourInstance;
    }


    //symbolTable;
    //;

    private void put(String s) {
        symbolTable.put(s,symbolTable.size());
        symbols.add(s);
    }

    private SymbolTable() {
//         SymbolTable.getInstance().getId("||");
//         SymbolTable.getInstance().getId("&&");
//         SymbolTable.getInstance().getId("==");
//         SymbolTable.getInstance().getId("!=");
//         SymbolTable.getInstance().getId("<=");
//         SymbolTable.getInstance().getId(">=");
//         SymbolTable.getInstance().getId(">>");
//         SymbolTable.getInstance().getId("<<");
    }

    public int getId(String sym) {
        Integer ret = symbolTable.get(sym);
        if (ret != null) {
            return ret;
        } else {
            put(sym);
            return symbolTable.size()-1;
        }
    }

    public String getSymbol(int id) {
        if (id== SymbolToken.end.symbol) return "EOF";
        return symbols.get(id);
    }


}
