package edu.berkeley.cs.builtin.objects.preprocessor;

import edu.berkeley.cs.builtin.functions.NativeFunction;
import edu.berkeley.cs.builtin.objects.*;
import edu.berkeley.cs.lexer.BufferedScanner;
import edu.berkeley.cs.parser.SymbolTable;

import java.util.ArrayList;

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
public class TokenEater extends CObject {
    public ArrayList<Token> tokens;
    public ArrayList<SymbolToken> parameters;
    public TokenEater parent;
    private String file;

    public BufferedScanner getScanner() {
        return new BufferedScanner(tokens);
    }

    public static CObject thisClass = new CObject();
    static {
        thisClass.addNewRule();
        thisClass.addSymbol(SymbolTable.getInstance().getId("{"));
        thisClass.addAction(new NativeFunction("createNewTokenEater"));

        thisClass.addNewRule();
        thisClass.addSymbol(SymbolTable.getInstance().getId("{"));
        thisClass.addSymbol(SymbolTable.getInstance().getId("|"));
        thisClass.addAction(new NativeFunction("createNewParameterEater"));

        thisClass.addNewRule();
        thisClass.addSymbol(SymbolTable.getInstance().getId("}"));
        thisClass.addAction(new NativeFunction("returnParentTokenEater"));

        thisClass.addNewRule();
        thisClass.addMeta(SymbolTable.getInstance().token);
        thisClass.addAction(new NativeFunction("append"));

        thisClass.addNewRule();
        thisClass.addSymbol(SymbolTable.getInstance().getId("\n"));
        thisClass.addAction(new NativeFunction("appendNewLine"));

        thisClass.addNewRule();
        thisClass.addSymbol(SymbolToken.end.symbol);
        thisClass.addAction(new NativeFunction("returnTokenEater"));
    }

    public TokenEater(TokenEater ss, String file) {
        tokens = new ArrayList<Token>();
        this.parent = ss;
        parameters = new ArrayList<SymbolToken>();
        this.file = file;

        setRule(thisClass);
        //rules = new RuleNode(null);

    }

    public CObject createNewParameterEater() {
        return new CParameterEater(new TokenEater(this,file));
    }

    public CObject createNewTokenEater() {
        return new TokenEater(this,file);
    }

    public CObject append(CObject token) {
        tokens.add((Token)token);
        return this;
    }

    public CObject appendNewLine() {
        tokens.add(new SymbolToken(null,SymbolTable.getInstance().getId("\n")));
        return this;
    }

    public CObject returnTokenEater() {
        setReturn();
        return this;
    }


    public CObject returnParentTokenEater() {
        parent.append(new CompoundToken(this,file));
        return parent;
    }

    public CObject returnNewCompoundToken() {
        return new CompoundToken(this,file);
    }

    public void appendParameters(ArrayList<SymbolToken> parameters) {
        this.parameters.addAll(parameters);
    }
}
