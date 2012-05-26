package edu.berkeley.cs.builtin.objects.singleton;

import edu.berkeley.cs.builtin.functions.Invokable;
import edu.berkeley.cs.builtin.objects.mutable.CObject;
import edu.berkeley.cs.builtin.objects.mutable.*;
import edu.berkeley.cs.parser.SymbolTable;

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
public class ProtoLongToken {
    final static public StandardObject INSTANCE = new StandardObject();


    static {
        INSTANCE.addNewRule();
        INSTANCE.addObject(new SymbolToken(null, SymbolTable.getInstance().getId("+")));
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                LongToken self = (LongToken)args.removeFirst();
                LongToken operand2 = (LongToken) args.removeFirst();
                return new LongToken(null,self.value+operand2.value);
            }
        },INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(new SymbolToken(null, SymbolTable.getInstance().getId("-")));
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                LongToken self = (LongToken)args.removeFirst();
                LongToken operand2 = (LongToken) args.removeFirst();
                return new LongToken(null,self.value-operand2.value);
            }
        },INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(new SymbolToken(null, SymbolTable.getInstance().getId("*")));
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                LongToken self = (LongToken)args.removeFirst();
                LongToken operand2 = (LongToken) args.removeFirst();
                return new LongToken(null,self.value*operand2.value);
            }
        },INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(new SymbolToken(null, SymbolTable.getInstance().getId("/")));
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                LongToken self = (LongToken)args.removeFirst();
                LongToken operand2 = (LongToken) args.removeFirst();
                return new LongToken(null,self.value/operand2.value);
            }
        },INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(new SymbolToken(null, SymbolTable.getInstance().getId("%")));
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                LongToken self = (LongToken)args.removeFirst();
                LongToken operand2 = (LongToken) args.removeFirst();
                return new LongToken(null,self.value%operand2.value);
            }
        },INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(new SymbolToken(null, SymbolTable.getInstance().getId("<")));
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                LongToken self = (LongToken)args.removeFirst();
                LongToken operand2 = (LongToken) args.removeFirst();
                return self.value<operand2.value? BooleanToken.TRUE():BooleanToken.FALSE();
            }
        },INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(new SymbolToken(null, SymbolTable.getInstance().getId(">")));
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                LongToken self = (LongToken)args.removeFirst();
                LongToken operand2 = (LongToken) args.removeFirst();
                return self.value>operand2.value? BooleanToken.TRUE():BooleanToken.FALSE();
            }
        },INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(new SymbolToken(null, SymbolTable.getInstance().getId("<=")));
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                LongToken self = (LongToken)args.removeFirst();
                LongToken operand2 = (LongToken) args.removeFirst();
                return self.value<=operand2.value? BooleanToken.TRUE():BooleanToken.FALSE();
            }
        },INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(new SymbolToken(null, SymbolTable.getInstance().getId(">=")));
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                LongToken self = (LongToken)args.removeFirst();
                LongToken operand2 = (LongToken) args.removeFirst();
                return self.value>=operand2.value? BooleanToken.TRUE():BooleanToken.FALSE();
            }
        },INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(new SymbolToken(null, SymbolTable.getInstance().getId("==")));
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                LongToken self = (LongToken)args.removeFirst();
                LongToken operand2 = (LongToken) args.removeFirst();
                return self.value==operand2.value? BooleanToken.TRUE():BooleanToken.FALSE();
            }
        },INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(new SymbolToken(null, SymbolTable.getInstance().getId("!=")));
        INSTANCE.addMeta(SymbolTable.getInstance().expr);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                LongToken self = (LongToken)args.removeFirst();
                LongToken operand2 = (LongToken) args.removeFirst();
                return self.value!=operand2.value? BooleanToken.TRUE():BooleanToken.FALSE();
            }
        },INSTANCE);

    }


//    public CObject add(CObject operand2) {
//        return new LongToken(null,value+((LongToken)operand2).value);
//    }
//
//    public CObject subtract(CObject operand2) {
//        return new LongToken(null,value-((LongToken)operand2).value);
//    }
//
//    public CObject multiply(CObject operand2) {
//        return new LongToken(null,value*((LongToken)operand2).value);
//    }
//
//    public CObject divide(CObject operand2) {
//        return new LongToken(null,value/((LongToken)operand2).value);
//    }
//
//    public CObject mod(CObject operand2) {
//        return new LongToken(null,value%((LongToken)operand2).value);
//    }
//
//    public CObject lt(CObject operand2) {
//        return value<((LongToken)operand2).value?BooleanToken.TRUE():BooleanToken.FALSE();
//    }
//
//    public CObject gt(CObject operand2) {
//        return value>((LongToken)operand2).value?BooleanToken.TRUE():BooleanToken.FALSE();
//    }
//
//    public CObject le(CObject operand2) {
//        return value<=((LongToken)operand2).value?BooleanToken.TRUE():BooleanToken.FALSE();
//    }
//
//    public CObject ge(CObject operand2) {
//        return value>=((LongToken)operand2).value?BooleanToken.TRUE():BooleanToken.FALSE();
//    }
//
//    public CObject eq(CObject operand2) {
//        return value==((LongToken)operand2).value?BooleanToken.TRUE():BooleanToken.FALSE();
//    }
//
//    public CObject ne(CObject operand2) {
//        return value!=((LongToken)operand2).value?BooleanToken.TRUE(): BooleanToken.FALSE();
//    }
//
//    static {
//        INSTANCE.eval("def + @expr @add endef");
//        INSTANCE.eval("def - @expr @subtract endef");
//        INSTANCE.eval("def * @expr @multiply endef");
//        INSTANCE.eval("def / @expr @divide endef");
//        INSTANCE.eval("def % @expr @mod endef");
//        INSTANCE.eval("def < @expr @lt endef");
//        INSTANCE.eval("def > @expr @gt endef");
//        INSTANCE.eval("def <= @expr @le endef");
//        INSTANCE.eval("def >= @expr @ge endef");
//        INSTANCE.eval("def == @expr @eq endef");
//        INSTANCE.eval("def != @expr @ne endef");
//    }
}