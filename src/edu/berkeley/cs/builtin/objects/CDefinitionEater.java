package edu.berkeley.cs.builtin.objects;

import edu.berkeley.cs.builtin.functions.*;
import edu.berkeley.cs.builtin.objects.preprocessor.CompoundToken;
import edu.berkeley.cs.builtin.objects.preprocessor.MetaToken;
import edu.berkeley.cs.builtin.objects.preprocessor.NullToken;
import edu.berkeley.cs.builtin.objects.preprocessor.SymbolToken;
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
public class CDefinitionEater extends CObject {
    public CObject parent;

    private static CObject superClass =  new CObject();

    static {
        superClass.addNewRule();
        superClass.addMeta(SymbolTable.getInstance().token);
        superClass.addAction(new NativeFunction("addToken"));
    }



    public CDefinitionEater(CObject parent) {
        this.parent = parent;
        parent.addNewRule();
        setRule(superClass);

//        setRule(methods);
//        rules = methods.getRuleNode();
        //rules = new RuleNode(null);
//        this.addNewRule();
//        this.addMeta(SymbolTable.getInstance().symbol);
//        this.addAction(new NativeFunction("addToken"));
//
//        this.addNewRule();
//        this.addMeta(SymbolTable.getInstance().meta);
//        this.addAction(new NativeFunction("addToken"));
//
//        this.addNewRule();
//        this.addMeta(SymbolTable.getInstance().block);
//        this.addAction(new NativeFunction("addToken"));

    }

    public CObject addToken(CObject arg) {
        CDefinitionEater self = this;

        if (arg instanceof CompoundToken) {
            self.parent.addAction(new UserDefinedFunction((CompoundToken) arg));
            return NullToken.NULL();
        }
        if (arg instanceof MetaToken) {
            MetaToken mt = (MetaToken) arg;
            if (mt.argument!= SymbolTable.getInstance().expr && mt.argument != SymbolTable.getInstance().token) {
                return new JavaClassEater(self.parent,SymbolTable.getInstance().getSymbol(mt.argument));
            }
        }
        if(arg instanceof SymbolToken) {
            self.parent.addSymbol(((SymbolToken) arg).symbol);
        } else         if(arg instanceof MetaToken) {
            self.parent.addMeta(((MetaToken)arg).argument);
        } else {
            throw new RuntimeException("Token must be Symbol or Argument "+arg);
        }
//        SS.parent.addToken((Token)arg);
        return self;
    }


}
