package edu.berkeley.cs.builtin.objects.singleton;

import edu.berkeley.cs.builtin.Reference;
import edu.berkeley.cs.builtin.functions.Invokable;
import edu.berkeley.cs.builtin.objects.mutable.*;
import edu.berkeley.cs.lexer.SourcePosition;
import edu.berkeley.cs.lexer.StandardLexer;
import edu.berkeley.cs.parser.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
public class ProtoEnvironmentObject {
    final public static CObject INSTANCE =  new StandardObject();
    private static HashMap<SourcePosition,CObject> staticObjects = new HashMap<SourcePosition, CObject>();

    static {
        INSTANCE.addNewRule();
        INSTANCE.addMeta(SymbolTable.getInstance().token);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                return args.removeFirst();
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
                CObject self = args.removeFirst();
                TokenEater arg = (TokenEater)args.removeFirst();
                CObject ret = new UserFunctionObject(null,arg.tokens,DS);
                return ret;
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
                CObject self = args.removeFirst();
                ParameterEater parg = (ParameterEater)args.removeFirst();
                TokenEater arg = (TokenEater)args.removeFirst();
                CObject ret = new UserFunctionObject(parg.parameters,arg.tokens,DS);
                return ret;
            }
        }, INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().lparen);
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addObject(SymbolTable.getInstance().rparen);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                return args.removeFirst();
            }
        }, INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().load);
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                CObject self = args.removeFirst();
                CObject fobj = args.removeFirst();
                File f;
                String currentFile = DS.getPosition().getFilename();

                if (currentFile != null) {
                    f = new File(currentFile);
                    f = new File(f.getParent(),((StringToken)fobj).value);
                } else {
                    f = new File(((StringToken)fobj).value);
                }
                String file = f.getAbsolutePath();

                ArrayList<CObject> tokens = null;
                try {
                    tokens = new StandardLexer(new BufferedReader(new FileReader(file)),file,false).getTokens();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                UserFunctionObject fun = new UserFunctionObject(null,tokens,DS);
                return fun;
            }
        }, INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().tokenToExpr);
        INSTANCE.addObject(SymbolTable.getInstance().lparen);
        INSTANCE.addMeta(SymbolTable.getInstance().token);
        INSTANCE.addObject(SymbolTable.getInstance().rparen);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                return args.removeFirst();
            }
        }, INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().minus);
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                CObject operand2 = args.removeFirst();
                if (operand2 instanceof LongToken)
                    return new LongToken(null,-((LongToken)operand2).value);
                return new DoubleToken(null,-((DoubleToken)operand2).value);
            }
        }, INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().not);
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                BooleanToken arg = (BooleanToken)args.removeFirst();
                return arg.value? BooleanToken.FALSE(): BooleanToken.TRUE();

            }
        }, INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().print);
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                CObject arg = args.removeFirst();
                System.out.println(arg);
                return arg;
            }
        }, INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().printdeep);
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                CObject arg = args.removeFirst();
                arg.printDeep();
                return arg;
            }
        }, INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().New);
        INSTANCE.addObject(SymbolTable.getInstance().object);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                return new StandardObject();
                
            }
        }, INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().Assert);
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                CObject arg = args.removeFirst();
                if (!((BooleanToken)arg).value)
                    throw new RuntimeException("assert failed");
                return VoidToken.VOID();
            }
        }, INSTANCE);

//        INSTANCE.addNewRule();
//        INSTANCE.addObject(SymbolTable.getInstance().If);
//        INSTANCE.addMeta(SymbolTable.getInstance().expr);
//        INSTANCE.addObject(SymbolTable.getInstance().then);
//        INSTANCE.addMeta(SymbolTable.getInstance().expr);
//        INSTANCE.addObject(SymbolTable.getInstance().Else);
//        INSTANCE.addMeta(SymbolTable.getInstance().expr);
//        INSTANCE.addAction(new Invokable() {
//            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
//                CObject self = args.removeFirst();
//                BooleanToken cond = (BooleanToken) args.removeFirst();
//                UserFunctionObject s1 = (UserFunctionObject)args.removeFirst();
//                UserFunctionObject s2 = (UserFunctionObject)args.removeFirst();
//                if (cond.value) {
//                    CObject ret = s1.executeInScope();
//                    if (ret.isException()) return ret;
//                    return VoidToken.VOID();
//                } else {
//                    CObject ret = s2.executeInScope();
//                    if (ret.isException()) return ret;
//                    return VoidToken.VOID();
//                }
//
//            }
//        }, INSTANCE);

//        INSTANCE.addNewRule();
//        INSTANCE.addObject(SymbolTable.getInstance().Try);
//        INSTANCE.addMeta(SymbolTable.getInstance().expr);
//        INSTANCE.addObject(SymbolTable.getInstance().Catch);
//        INSTANCE.addMeta(SymbolTable.getInstance().token);
//        INSTANCE.addMeta(SymbolTable.getInstance().expr);
//        INSTANCE.addAction(new Invokable() {
//            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
//                CObject self = args.removeFirst();
//                UserFunctionObject s1 = (UserFunctionObject)args.removeFirst();
//                SymbolToken symbol = (SymbolToken)args.removeFirst();
//                UserFunctionObject s2 = (UserFunctionObject)args.removeFirst();
//                CObject ret = s1.executeInScope();
//                if (ret.isException()) {
//                    ret.clearException();
//                    Reference common = new Reference(ret);
//                    self.assign(symbol, common);
//
//                    ret = s2.executeInScope();
//                    if (ret.isException()) return ret;
//                }
//                return VoidToken.VOID();
//
//            }
//        }, INSTANCE);

//        INSTANCE.addNewRule();
//        INSTANCE.addObject(SymbolTable.getInstance().While);
//        INSTANCE.addMeta(SymbolTable.getInstance().expr);
//        INSTANCE.addMeta(SymbolTable.getInstance().expr);
//        INSTANCE.addAction(new Invokable() {
//            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
//                CObject self = args.removeFirst();
//                UserFunctionObject s1 = (UserFunctionObject)args.removeFirst();
//                UserFunctionObject s2 = (UserFunctionObject)args.removeFirst();
//                CObject ret;
//                ret = s1.executeInScope();
//                if (ret.isException()) return ret;
//
//                while(((BooleanToken)ret).value) {
//                    ret = s2.executeInScope();
//                    if (ret.isException()) return ret;
//
//                    ret = s1.executeInScope();
//                    if (ret.isException()) return ret;
//                }
//                return VoidToken.VOID();
//
//            }
//        }, INSTANCE);

//        INSTANCE.addNewRule();
//        INSTANCE.addObject(SymbolTable.getInstance().once);
//        INSTANCE.addMeta(SymbolTable.getInstance().expr);
//        INSTANCE.addAction(new Invokable() {
//            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
//                CObject self = args.removeFirst();
//                FunctionObject func = (FunctionObject)args.removeFirst();
//                CObject val;
//                if ((val = staticObjects.get(DS.getPosition()))==null) {
//                    val = func.execute(DS);
//                    if (val.isException()) return val;
//                    staticObjects.put(DS.getPosition(),val);
//                }
//                return val;
//
//            }
//        }, INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().Throw);
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                CObject arg = args.removeFirst();
                arg.setException();
                return arg;
            }
        }, INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().Return);
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new ReturnToDSAction());

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().loop);
        INSTANCE.addAction(new LoopAction());

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().Return);
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addObject(SymbolTable.getInstance().to);
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new ReturnToAction());
//        INSTANCE.addAction(new Invokable() {
//            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
//                args.removeFirst();
//                CObject arg = args.removeFirst();
//                arg.setReturn();
//                return arg;
//            }
//        }, INSTANCE);

        INSTANCE.assign(SymbolTable.getInstance().LS,new Reference(INSTANCE));
    }

    //@todo need to add Java token eater

}
