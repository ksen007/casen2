package edu.berkeley.cs.parser;

import edu.berkeley.cs.builtin.functions.Invokable;
import edu.berkeley.cs.builtin.objects.mutable.CObject;
import edu.berkeley.cs.builtin.objects.mutable.FunctionObject;

import java.util.LinkedList;
import java.util.Stack;

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
public class NativeFunctionObject extends FunctionObject {
    public Invokable fun;
    int arguments;

    public NativeFunctionObject(Invokable fun, CObject scope, int argCount) {
        super(null, scope);
        this.fun = fun;
        this.arguments = argCount;

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().lparen);
        for(int i=0; i<argCount; i++) {
            this.addMeta(SymbolTable.getInstance().expr);
            if (i<argCount-1)
                this.addObject(SymbolTable.getInstance().comma);
        }
        this.addObject(SymbolTable.getInstance().rparen);
        this.addAction(this);
    }


    public Continuation apply(Stack<CObject> computationStack, Continuation cf) {
        LinkedList<CObject> args = new LinkedList<CObject>();
        for(int i=0; i<=arguments;i++) {
            args.addFirst(computationStack.pop());
        }
        computationStack.push(fun.apply(args,scope,cf.LS));
        return cf;
    }

    public int getArgCount() {
        return arguments;
    }

    public Continuation execute(Continuation DS) {
        Stack<CObject> args = new Stack<CObject>();
        args.push(this);
        return apply(args,DS);
    }

}
