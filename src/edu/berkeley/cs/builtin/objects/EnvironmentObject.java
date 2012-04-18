package edu.berkeley.cs.builtin.objects;

import edu.berkeley.cs.builtin.functions.GetField;
import edu.berkeley.cs.builtin.functions.NativeFunction;
import edu.berkeley.cs.builtin.objects.preprocessor.BooleanToken;
import edu.berkeley.cs.builtin.objects.preprocessor.CompoundToken;
import edu.berkeley.cs.builtin.objects.preprocessor.MetaToken;
import edu.berkeley.cs.builtin.objects.preprocessor.SymbolToken;
import edu.berkeley.cs.parser.RuleNode;
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
    public static CObject superClass = new CObject();


    static {
        superClass.addNewRule();
        superClass.addMeta(SymbolTable.getInstance().symbol);
        superClass.addSymbol(SymbolTable.getInstance().getId("="));
        superClass.addMeta(SymbolTable.getInstance().argument);
        superClass.addAction(new NativeFunction("assignment"));

        superClass.addNewRule();
        superClass.addSymbol(SymbolTable.getInstance().getId("def"));
        superClass.addAction(new NativeFunction("newDefinitionEater"));

        superClass.addNewRule();
        superClass.addMeta(SymbolTable.getInstance().literal);
        superClass.addAction(new NativeFunction("returnArgument"));

        superClass.addNewRule();
        superClass.addMeta(SymbolTable.getInstance().block);
        superClass.addAction(new NativeFunction("returnArgument"));

        superClass.addNewRule();
        superClass.addSymbol(SymbolTable.getInstance().getId("=="));
        superClass.addMeta(SymbolTable.getInstance().argument);
        superClass.addAction(new NativeFunction("equality"));

        superClass.addNewRule();
        superClass.addSymbol(SymbolTable.getInstance().getId("!="));
        superClass.addMeta(SymbolTable.getInstance().argument);
        superClass.addAction(new NativeFunction("disequality"));

        superClass.addNewRule();
        superClass.addSymbol(SymbolTable.getInstance().getId("("));
        superClass.addMeta(SymbolTable.getInstance().argument);
        superClass.addSymbol(SymbolTable.getInstance().getId(")"));
        superClass.addAction(new NativeFunction("returnArgument"));

        superClass.addNewRule();
        superClass.addSymbol(SymbolTable.getInstance().getId("load"));
        superClass.addMeta(SymbolTable.getInstance().argument);
        superClass.addAction(new NativeFunction("loadFile"));

        superClass.addNewRule();
        superClass.addSymbol(SymbolTable.getInstance().getId("print"));
        superClass.addMeta(SymbolTable.getInstance().argument);
        superClass.addAction(new NativeFunction("print"));

        superClass.addNewRule();
        superClass.addSymbol(SymbolTable.getInstance().getId("new"));
        superClass.addSymbol(SymbolTable.getInstance().getId("Object"));
        superClass.addAction(new NativeFunction("newObject"));

        superClass.addNewRule();
        superClass.addSymbol(SymbolTable.getInstance().getId("assert"));
        superClass.addMeta(SymbolTable.getInstance().argument);
        superClass.addAction(new NativeFunction("assertEquality"));

        superClass.addNewRule();
        superClass.addSymbol(SymbolTable.getInstance().getId("if"));
        superClass.addMeta(SymbolTable.getInstance().argument);
        superClass.addSymbol(SymbolTable.getInstance().getId("then"));
        superClass.addMeta(SymbolTable.getInstance().block);
        superClass.addSymbol(SymbolTable.getInstance().getId("else"));
        superClass.addMeta(SymbolTable.getInstance().block);
        superClass.addAction(new NativeFunction("ifAction"));

        superClass.addNewRule();
        superClass.addSymbol(SymbolTable.getInstance().getId("while"));
        superClass.addMeta(SymbolTable.getInstance().block);
        superClass.addMeta(SymbolTable.getInstance().block);
        superClass.addAction(new NativeFunction("whileAction"));

        superClass.addNewRule();
        superClass.addSymbol(SymbolTable.getInstance().getId("once"));
        superClass.addMeta(SymbolTable.getInstance().block);
        superClass.addAction(new NativeFunction("onceAction"));
    }

    public EnvironmentObject() {
        setSuperClass(superClass);

        this.addNewRule();
        this.addSymbol(SymbolTable.getInstance().getId("LS"));
        this.addAction(new GetField(new Reference(this)));
    }

    public CObject print(CObject ret) {
        System.out.println(ret);
        return ret;
    }


    public CObject assertEquality(CObject first) {
        if (!((BooleanToken)first).value)
            throw new RuntimeException("assert failed");
        return first;
    }

    public CObject returnArgument(CObject arg) {
        return arg;
    }

    public CObject whileAction(CObject S1, CObject S2) {
        CompoundToken s1 = (CompoundToken)S1;
        CompoundToken s2 = (CompoundToken)S2;
        while(((BooleanToken)s1.execute(this)).value) {
            s2.execute(this);
        }
        return this;
    }



    public CObject onceAction(CObject S) {
        CompoundToken s = (CompoundToken)S;
        CObject val;
        if ((val = CObject.staticObjects.get(s))==null) {
            val = s.execute(this);
            CObject.staticObjects.put(s,val);
        }
        return val;
    }

    public CObject ifAction(CObject c1, CObject S1, CObject S2) {
        BooleanToken cond = (BooleanToken) c1;
        CompoundToken s1 = (CompoundToken)S1;
        CompoundToken s2 = (CompoundToken)S2;
        if (cond.value) {
            s1.execute(this);
            return this;
        } else {
            s2.execute(this);
            return this;
        }
    }


    public CObject newObject() {
        return new CNonPrimitiveObject();
    }
}
