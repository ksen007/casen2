package edu.berkeley.cs.builtin.objects;

import edu.berkeley.cs.builtin.functions.Invokable;
import edu.berkeley.cs.builtin.functions.NativeFunction;
import edu.berkeley.cs.builtin.objects.preprocessor.NullToken;
import edu.berkeley.cs.builtin.objects.preprocessor.SymbolToken;
import edu.berkeley.cs.builtin.objects.preprocessor.VoidToken;
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
public class JavaClassEater extends CObject {
    private CObject parent;
    private StringBuilder symbols;
    private boolean isNativeFunction;

    public static CObject superClass = new CObject();
    static {
        superClass.addNewRule();
        superClass.addObject(SymbolTable.getInstance().dot);
        superClass.addMeta(SymbolTable.getInstance().token);
        superClass.addAction(new NativeFunction("addPackage"));

        superClass.addNewRule();
        superClass.addObject(SymbolTable.getInstance().endef);
        superClass.addAction(new NativeFunction("addNative"));
    }

    public JavaClassEater(CObject parent, String symbol) {
        super();
        this.parent = parent;
        this.symbols = new StringBuilder(symbol);
        this.isNativeFunction = true;

        setRule(superClass);
        //rules = new RuleNode(null);
//        this.addNewRule();
//        this.addSymbol(SymbolTable.getInstance().getId("."));
//        this.addMeta(SymbolTable.getInstance().symbol);
//        this.addAction(new NativeFunction("addPackage"));
//
//        this.addNewRule();
//        this.addSymbol(SymbolTable.getInstance().getId(":"));
//        this.addAction(new NativeFunction("addNative"));
    }

    public CObject addPackage(CObject sym) {
        symbols.append('.').append(SymbolTable.getInstance().getSymbol(((SymbolToken)sym).symbol));
        this.isNativeFunction = false;
        return this;
    }

    public CObject addNative() {
        if (isNativeFunction) {
            parent.addAction(new NativeFunction(symbols.toString()));
        } else {
            try {
                parent.addAction((Invokable)(Class.forName(symbols.toString()).newInstance()));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return VoidToken.VOID();
    }
}
