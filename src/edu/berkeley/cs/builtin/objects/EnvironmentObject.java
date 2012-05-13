package edu.berkeley.cs.builtin.objects;

import edu.berkeley.cs.builtin.functions.NativeFunction;
import edu.berkeley.cs.parser.SymbolTable;

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
public class EnvironmentObject extends CObject {
    public static EnvironmentObject instance = new EnvironmentObject();

    private EnvironmentObject() {
        this.addNewRule();
        this.addObject(SymbolTable.getInstance().var);
        this.addMeta(SymbolTable.getInstance().token);
        this.addObject(SymbolTable.getInstance().assign);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addAction(new NativeFunction("assignment"));

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().def);
        this.addAction(new NativeFunction("newDefinitionEater"));

        this.addNewRule();
        this.addMeta(SymbolTable.getInstance().token);
        this.addAction(new NativeFunction("returnToken"));

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().lparen);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addObject(SymbolTable.getInstance().rparen);
        this.addAction(new NativeFunction("returnArgument"));

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().load);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addAction(new NativeFunction("loadFile"));

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().tokenToExpr);
        this.addObject(SymbolTable.getInstance().lparen);
        this.addMeta(SymbolTable.getInstance().token);
        this.addObject(SymbolTable.getInstance().rparen);
        this.addAction(new NativeFunction("returnToken"));

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().minus);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addAction(new NativeFunction("unaryMinus"));

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().not);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addAction(new NativeFunction("unaryNot"));

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().print);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addAction(new NativeFunction("print"));

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().New);
        this.addObject(SymbolTable.getInstance().object);
        this.addAction(new NativeFunction("newObject"));

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().Assert);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addAction(new NativeFunction("assertEquality"));

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().If);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addObject(SymbolTable.getInstance().then);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addObject(SymbolTable.getInstance().Else);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addAction(new NativeFunction("ifAction"));

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().Try);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addObject(SymbolTable.getInstance().Catch);
        this.addMeta(SymbolTable.getInstance().token);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addAction(new NativeFunction("tryCatch"));

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().While);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addAction(new NativeFunction("whileAction"));

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().once);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addAction(new NativeFunction("onceAction"));

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().Throw);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addAction(new NativeFunction("cException"));

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().Return);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addAction(new NativeFunction("cReturn"));


//        this.addNewRule();
//        this.addMeta(SymbolTable.getInstance().token);
//        this.addAction(new NativeFunction("returnArgument"));
    }

}
