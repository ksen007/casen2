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
public class ProtoEnvironmentObject extends CObject {
    final public static CObject instance =  new CObject();

    static {
        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().var);
        instance.addMeta(SymbolTable.getInstance().token);
        instance.addObject(SymbolTable.getInstance().assign);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new NativeFunction("assignment"));

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().def);
        instance.addAction(new NativeFunction("newDefinitionEater"));

        instance.addNewRule();
        instance.addMeta(SymbolTable.getInstance().token);
        instance.addAction(new NativeFunction("returnToken"));

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().lparen);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addObject(SymbolTable.getInstance().rparen);
        instance.addAction(new NativeFunction("returnArgument"));

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().load);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new NativeFunction("loadFile"));

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().tokenToExpr);
        instance.addObject(SymbolTable.getInstance().lparen);
        instance.addMeta(SymbolTable.getInstance().token);
        instance.addObject(SymbolTable.getInstance().rparen);
        instance.addAction(new NativeFunction("returnToken"));

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().minus);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new NativeFunction("unaryMinus"));

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().not);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new NativeFunction("unaryNot"));

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().print);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new NativeFunction("print"));

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().printdeep);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new NativeFunction("printDeep"));

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().New);
        instance.addObject(SymbolTable.getInstance().object);
        instance.addAction(new NativeFunction("newObject"));

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().Assert);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new NativeFunction("assertEquality"));

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().If);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addObject(SymbolTable.getInstance().then);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addObject(SymbolTable.getInstance().Else);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new NativeFunction("ifAction"));

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().Try);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addObject(SymbolTable.getInstance().Catch);
        instance.addMeta(SymbolTable.getInstance().token);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new NativeFunction("tryCatch"));

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().While);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new NativeFunction("whileAction"));

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().once);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new NativeFunction("onceAction"));

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().Throw);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new NativeFunction("cException"));

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().Return);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new NativeFunction("cReturn"));

        instance.assign(SymbolTable.getInstance().LS,new Reference(instance));
//        instance.addNewRule();
//        instance.addObject(SymbolTable.getInstance().LS);
//        instance.addAction(new GetField(new Reference(instance)));
    }
    
}
