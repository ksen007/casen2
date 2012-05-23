package edu.berkeley.cs.builtin.objects.preprocessor;

import edu.berkeley.cs.builtin.functions.Invokable;
import edu.berkeley.cs.builtin.functions.NativeFunction;
import edu.berkeley.cs.builtin.objects.CDefinitionEater;
import edu.berkeley.cs.builtin.objects.CObject;
import edu.berkeley.cs.lexer.SourcePosition;
import edu.berkeley.cs.parser.SymbolTable;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;

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
public class JavaEater extends CObject {
    public ArrayList<CObject> tokens;
    public CDefinitionEater defEater;
    private static int count=0;

    public static CObject thisClass = new CObject();
    static {
        thisClass.addNewRule();
        thisClass.addObject(SymbolTable.getInstance().pound);
        thisClass.addObject(SymbolTable.getInstance().rcurly);
        thisClass.addAction(new NativeFunction("createJavaClass"));

        thisClass.addNewRule();
        thisClass.addMeta(SymbolTable.getInstance().token);
        thisClass.addAction(new NativeFunction("append"));

        thisClass.addNewRule();
        thisClass.addObject(SymbolTable.getInstance().newline);
        thisClass.addAction(new NativeFunction("appendNewLine"));

        thisClass.addNewRule();
        thisClass.addObject(SymbolTable.getInstance().semi);
        thisClass.addAction(new NativeFunction("appendSemi"));
    }

    public JavaEater(CDefinitionEater defEater) {
        tokens = new ArrayList<CObject>();
        this.defEater = defEater;
        setRule(thisClass);
    }

    public CObject createJavaClass() throws CannotCompileException, IllegalAccessException, InstantiationException {
        StringBuilder sb = new StringBuilder();
        for(CObject token: tokens) {
            if (!token.isNoSpace()) {
                sb.append(' ');
            }
            if (token instanceof SymbolToken) {
                sb.append(SymbolTable.getInstance().getSymbol(((SymbolToken)token).symbol));
            } else if (token instanceof StringToken) {
                sb.append('"');
                sb.append(((StringToken)token).value);
                sb.append('"');
            }
        }

        ClassPool pool = ClassPool.getDefault();
        pool.importPackage("edu.berkeley.cs.builtin.objects.CObject");
        pool.importPackage("java.util.LinkedList");
        CtClass nativeClass = pool.makeClass("Native"+(++count));
        nativeClass.addMethod(CtNewMethod.make(sb.toString(),nativeClass));
        nativeClass.setInterfaces(new CtClass[] { pool.makeClass("edu.berkeley.cs.builtin.functions.Invokable") });
        Class clazz = nativeClass.toClass();
        defEater.parent.addAction((Invokable)clazz.newInstance());
        return VoidToken.VOID();
    }

    public CObject append(CObject token) {
        tokens.add(token);
        return this;
    }

    public CObject appendNewLine() {
        SourcePosition pos = tokens.size()>1?tokens.get(tokens.size()-1).getPosition():null;
        tokens.add(new SymbolToken(pos,SymbolTable.getInstance().getId("\n")));
        return this;
    }

    public CObject appendSemi() {
        SourcePosition pos = tokens.size()>1?tokens.get(tokens.size()-1).getPosition():null;
        tokens.add(new SymbolToken(pos,SymbolTable.getInstance().getId(";")));
        return this;
    }
}
