package edu.berkeley.cs.builtin.objects;

import edu.berkeley.cs.builtin.functions.*;
import edu.berkeley.cs.builtin.objects.preprocessor.*;
import edu.berkeley.cs.parser.SymbolTable;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;

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
public class CDefinitionEater extends CObject {
    public CObject parent;
    public static int count = 0;

    private static CObject superClass =  new CObject();

    static {
        superClass.addNewRule();
        superClass.addMeta(SymbolTable.getInstance().token);
        superClass.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                CDefinitionEater self = (CDefinitionEater)args.removeFirst();
                CObject arg = args.removeFirst();
                if (arg instanceof MetaToken) {
                    int argument = ((MetaToken)arg).metaSymbol;
                    if (argument == SymbolTable.getInstance().nl) {
                        self.parent.addObject(SymbolTable.getInstance().newline);
                    } else if (argument == SymbolTable.getInstance().eof) {
                        self.parent.addObject(SymbolToken.end);
                    } else {
                        self.parent.addMeta(argument);
                    }
                } else {
                    self.parent.addObject(arg);
                }
                return self;
            }
        },superClass);

        superClass.addNewRule();
        superClass.addObject(SymbolTable.getInstance().at);
        superClass.addObject(SymbolTable.getInstance().lparen);
        superClass.addMeta(SymbolTable.getInstance().expr);
        superClass.addObject(SymbolTable.getInstance().rparen);
        superClass.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                CDefinitionEater self = (CDefinitionEater)args.removeFirst();
                CObject arg = args.removeFirst();
                self.parent.addOther((FunctionObject)arg);
                return self;
            }
        },superClass);

        superClass.addNewRule();
        superClass.addObject(SymbolTable.getInstance().lcurly);
        superClass.addOther(new NativeFunctionObject(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                return new TokenEater();
            }
        },superClass,0));
        superClass.addObject(SymbolTable.getInstance().rcurly);
        superClass.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                CDefinitionEater self = (CDefinitionEater)args.removeFirst();
                TokenEater arg = (TokenEater)args.removeFirst();
                self.parent.addAction(new UserFunctionObject(null, arg, DS), false);
                arg.clearAll();
                return VoidToken.VOID();
            }
        },superClass);

        superClass.addNewRule();
        superClass.addObject(SymbolTable.getInstance().lcurly);
        superClass.addObject(SymbolTable.getInstance().bar);
        superClass.addOther(new NativeFunctionObject(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                return new CParameterEater();
            }
        },superClass,0));
        superClass.addObject(SymbolTable.getInstance().bar);
        superClass.addOther(new NativeFunctionObject(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                return new TokenEater();
            }
        },superClass,0));
        superClass.addObject(SymbolTable.getInstance().rcurly);
        superClass.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                CDefinitionEater self = (CDefinitionEater)args.removeFirst();
                CParameterEater parg = (CParameterEater)args.removeFirst();
                TokenEater arg = (TokenEater)args.removeFirst();
                self.parent.addAction(new UserFunctionObject(parg, arg, DS), false);
                parg.clearAll();
                arg.clearAll();
                return VoidToken.VOID();
            }
        },superClass);

        superClass.addNewRule();
        superClass.addObject(SymbolTable.getInstance().lcurly);
        superClass.addObject(SymbolTable.getInstance().pound);
        superClass.addOther(new NativeFunctionObject(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                return new TokenEater();
            }
        },superClass,0));
        superClass.addObject(SymbolTable.getInstance().pound);
        superClass.addObject(SymbolTable.getInstance().rcurly);
        superClass.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS)  {
                CDefinitionEater self = (CDefinitionEater)args.removeFirst();
                TokenEater arg = (TokenEater) args.removeFirst();
                StringBuilder sb = new StringBuilder();
                try {
                    for(CObject token: arg.tokens) {
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
                    pool.importPackage("edu.berkeley.cs.builtin.objects.preprocessor.*");
                    pool.importPackage("java.util.LinkedList");
                    CtClass nativeClass = pool.makeClass("Native"+(++count));
                    nativeClass.addMethod(CtNewMethod.make(sb.toString(), nativeClass));
                    nativeClass.setInterfaces(new CtClass[] { pool.makeClass("edu.berkeley.cs.builtin.functions.Invokable") });
                    Class clazz = nativeClass.toClass();
                    self.parent.addAction((Invokable)clazz.newInstance(),DS);
                    return VoidToken.VOID();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Cannot compile native method "+sb.toString());
                }
            }
        },superClass); //@todo comeback to check thisClass
    }



    public CDefinitionEater(CObject parent) {
        this.parent = parent;
        parent.addNewRule();
        setRule(superClass);
    }

}
