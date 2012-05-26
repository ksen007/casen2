package edu.berkeley.cs.builtin.objects.singleton;

import edu.berkeley.cs.builtin.functions.Invokable;
import edu.berkeley.cs.builtin.objects.mutable.DefinitionEater;
import edu.berkeley.cs.builtin.objects.mutable.CObject;
import edu.berkeley.cs.builtin.objects.mutable.*;
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
public class ProtoDefinitionEater {
    final public static CObject INSTANCE =  new CObject();
    public static int count = 0;

    static {
        INSTANCE.addNewRule();
        INSTANCE.addMeta(SymbolTable.getInstance().token);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                DefinitionEater self = (DefinitionEater)args.removeFirst();
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
        }, INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().at);
        INSTANCE.addObject(SymbolTable.getInstance().lparen);
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addObject(SymbolTable.getInstance().rparen);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                DefinitionEater self = (DefinitionEater)args.removeFirst();
                CObject arg = args.removeFirst();
                self.parent.addOther((FunctionObject)arg);
                return self;
            }
        }, INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().lcurly);
        INSTANCE.addOther(new NativeFunctionObject(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                return new TokenEater();
            }
        }, INSTANCE,0));
        INSTANCE.addObject(SymbolTable.getInstance().rcurly);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                DefinitionEater self = (DefinitionEater)args.removeFirst();
                TokenEater arg = (TokenEater)args.removeFirst();
                self.parent.addAction(new UserFunctionObject(null, arg, DS), false);
                return VoidToken.VOID();
            }
        }, INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().lcurly);
        INSTANCE.addObject(SymbolTable.getInstance().bar);
        INSTANCE.addOther(new NativeFunctionObject(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                return new ParameterEater();
            }
        }, INSTANCE,0));
        INSTANCE.addObject(SymbolTable.getInstance().bar);
        INSTANCE.addOther(new NativeFunctionObject(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                return new TokenEater();
            }
        }, INSTANCE,0));
        INSTANCE.addObject(SymbolTable.getInstance().rcurly);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                DefinitionEater self = (DefinitionEater)args.removeFirst();
                ParameterEater parg = (ParameterEater)args.removeFirst();
                TokenEater arg = (TokenEater)args.removeFirst();
                self.parent.addAction(new UserFunctionObject(parg, arg, DS), false);
                return VoidToken.VOID();
            }
        }, INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().lcurly);
        INSTANCE.addObject(SymbolTable.getInstance().pound);
        INSTANCE.addOther(new NativeFunctionObject(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                return new TokenEater();
            }
        }, INSTANCE,0));
        INSTANCE.addObject(SymbolTable.getInstance().pound);
        INSTANCE.addObject(SymbolTable.getInstance().rcurly);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS)  {
                DefinitionEater self = (DefinitionEater)args.removeFirst();
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
                    pool.importPackage("edu.berkeley.cs.builtin.objects.mutable.CObject");
                    pool.importPackage("edu.berkeley.cs.builtin.objects.mutable.*");
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
        }, INSTANCE); //@todo comeback to check thisClass
    }



}
