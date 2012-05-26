package edu.berkeley.cs.builtin.objects.singleton;

import edu.berkeley.cs.builtin.functions.Invokable;
import edu.berkeley.cs.builtin.objects.mutable.CObject;
import edu.berkeley.cs.builtin.objects.mutable.NativeFunctionObject;
import edu.berkeley.cs.builtin.objects.mutable.SymbolToken;
import edu.berkeley.cs.builtin.objects.mutable.TokenEater;
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
public class ProtoTokenEater {
    final public static CObject INSTANCE = new CObject();

    static {
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
                TokenEater self = (TokenEater)args.removeFirst();
                TokenEater arg = (TokenEater)args.removeFirst();
                self.tokens.add(new SymbolToken(DS.getPosition(),SymbolTable.getInstance().getId("{"))); //@todo not the correct position
                self.tokens.addAll(arg.tokens);
                self.tokens.add(new SymbolToken(DS.getPosition(),SymbolTable.getInstance().getId("}")));
                return self;
            }
        }, INSTANCE); //@todo make sure that the second argument is thisClass

        INSTANCE.addNewRule();
        INSTANCE.addMeta(SymbolTable.getInstance().token);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                TokenEater self = (TokenEater)args.removeFirst();
                self.tokens.add(args.removeFirst());
                return self;
            }
        }, INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().newline);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                TokenEater self = (TokenEater)args.removeFirst();
                self.tokens.add(new SymbolToken(DS.getPosition(),SymbolTable.getInstance().getId("\n")));
                return self;

            }
        }, INSTANCE);

        INSTANCE.addNewRule();
        INSTANCE.addObject(SymbolTable.getInstance().exprToToken);
        INSTANCE.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                TokenEater self = (TokenEater)args.removeFirst();
                self.tokens.add(new SymbolToken(DS.getPosition(),SymbolTable.getInstance().exprToToken.symbol));
                return self;

            }
        }, INSTANCE);
    }
}
