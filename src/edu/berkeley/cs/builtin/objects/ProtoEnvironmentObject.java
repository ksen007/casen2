package edu.berkeley.cs.builtin.objects;

import edu.berkeley.cs.builtin.functions.Invokable;
import edu.berkeley.cs.builtin.objects.preprocessor.*;
import edu.berkeley.cs.lexer.SourcePosition;
import edu.berkeley.cs.parser.SymbolTable;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

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
    final public static CObject instance =  new StandardObject();
    private static HashMap<SourcePosition,CObject> staticObjects = new HashMap<SourcePosition, CObject>();
    private static TreeMap<String,CompoundToken> codeCache = new TreeMap<String, CompoundToken>();

    static {
        instance.addNewRule();
        instance.addMeta(SymbolTable.getInstance().token);
        instance.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                return args.removeFirst();
            }
        },instance);

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().lcurly);
        instance.addOther(new TokenEater());
        instance.addObject(SymbolTable.getInstance().rcurly);
        instance.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                CObject self = args.removeFirst();
                TokenEater arg = (TokenEater)args.removeFirst();
                CObject ret = new CompoundToken(null,arg,DS);
                arg.clearAll();
                return ret;
            }
        },instance);

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().lcurly);
        instance.addObject(SymbolTable.getInstance().bar);
        instance.addOther(new CParameterEater());
        instance.addObject(SymbolTable.getInstance().bar);
        instance.addOther(new TokenEater());
        instance.addObject(SymbolTable.getInstance().rcurly);
        instance.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                CObject self = args.removeFirst();
                CParameterEater parg = (CParameterEater)args.removeFirst();
                TokenEater arg = (TokenEater)args.removeFirst();
                CObject ret = new CompoundToken(parg,arg,DS);
                parg.clearAll();
                arg.clearAll();
                return ret;
            }
        },instance);

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().lparen);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addObject(SymbolTable.getInstance().rparen);
        instance.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                return args.removeFirst();
            }
        },instance);

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().load);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new Invokable() {
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
                return self.evalFile(file);
            }
        },instance);

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().tokenToExpr);
        instance.addObject(SymbolTable.getInstance().lparen);
        instance.addMeta(SymbolTable.getInstance().token);
        instance.addObject(SymbolTable.getInstance().rparen);
        instance.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                return args.removeFirst();
            }
        },instance);

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().minus);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                CObject operand2 = args.removeFirst();
                if (operand2 instanceof LongToken)
                    return new LongToken(null,-((LongToken)operand2).value);
                return new DoubleToken(null,-((DoubleToken)operand2).value);
            }
        },instance);

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().not);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                BooleanToken arg = (BooleanToken)args.removeFirst();
                return arg.value? BooleanToken.FALSE(): BooleanToken.TRUE();

            }
        },instance);

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().print);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                CObject arg = args.removeFirst();
                System.out.println(arg);
                return arg;
            }
        },instance);

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().printdeep);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                CObject arg = args.removeFirst();
                arg.printDeep();
                return arg;
            }
        },instance);

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().New);
        instance.addObject(SymbolTable.getInstance().object);
        instance.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                return new StandardObject();
                
            }
        },instance);

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().Assert);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                CObject arg = args.removeFirst();
                if (!((BooleanToken)arg).value)
                    throw new RuntimeException("assert failed");
                return VoidToken.VOID();
            }
        },instance);

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().If);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addObject(SymbolTable.getInstance().then);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addObject(SymbolTable.getInstance().Else);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                CObject self = args.removeFirst();
                BooleanToken cond = (BooleanToken) args.removeFirst();
                CompoundToken s1 = (CompoundToken)args.removeFirst();
                CompoundToken s2 = (CompoundToken)args.removeFirst();
                if (cond.value) {
                    CObject ret = s1.execute(self,null);
                    if (ret.isException()) return ret;
                    return VoidToken.VOID();
                } else {
                    CObject ret = s2.execute(self,null);
                    if (ret.isException()) return ret;
                    return VoidToken.VOID();
                }

            }
        },instance);

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().Try);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addObject(SymbolTable.getInstance().Catch);
        instance.addMeta(SymbolTable.getInstance().token);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                CObject self = args.removeFirst();
                CompoundToken s1 = (CompoundToken)args.removeFirst();
                SymbolToken symbol = (SymbolToken)args.removeFirst();
                CompoundToken s2 = (CompoundToken)args.removeFirst();
                CObject ret = s1.execute(self,null);
                if (ret.isException()) {
                    ret.clearException();
                    Reference common = new Reference(ret);
                    self.assign(symbol, common);

                    ret = s2.execute(self,null);
                    if (ret.isException()) return ret;
                }
                return VoidToken.VOID();

            }
        },instance);

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().While);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                CObject self = args.removeFirst();
                CompoundToken s1 = (CompoundToken)args.removeFirst();
                CompoundToken s2 = (CompoundToken)args.removeFirst();
                CObject ret;
                ret = s1.execute(self,null);
                if (ret.isException()) return ret;

                while(((BooleanToken)ret).value) {
                    ret = s2.execute(self, null);
                    if (ret.isException()) return ret;

                    ret = s1.execute(self, null);
                    if (ret.isException()) return ret;
                }
                return VoidToken.VOID();

            }
        },instance);

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().once);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                CObject self = args.removeFirst();
                CompoundToken func = (CompoundToken)args.removeFirst();
                CObject val;
                if ((val = staticObjects.get(DS.getPosition()))==null) {
                    val = func.execute(self,null);
                    if (val.isException()) return val;
                    staticObjects.put(DS.getPosition(),val);
                }
                return val;

            }
        },instance); 

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().Throw);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                CObject arg = args.removeFirst();
                arg.setException();
                return arg;
            }
        },instance);

        instance.addNewRule();
        instance.addObject(SymbolTable.getInstance().Return);
        instance.addMeta(SymbolTable.getInstance().expr);
        instance.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                args.removeFirst();
                CObject arg = args.removeFirst();
                arg.setReturn();
                return arg;
            }
        },instance);

        instance.assign(SymbolTable.getInstance().LS,new Reference(instance));
    }

}
