package edu.berkeley.cs.parser;

import edu.berkeley.cs.builtin.Reference;
import edu.berkeley.cs.builtin.objects.mutable.CObject;
import edu.berkeley.cs.builtin.objects.mutable.EnvironmentObject;
import edu.berkeley.cs.builtin.objects.mutable.SymbolToken;
import edu.berkeley.cs.builtin.objects.singleton.ProtoStatementEater;
import edu.berkeley.cs.lexer.TokenList;

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
public class UserExtraAction implements Action {
    UserFunctionObject child;

    public UserExtraAction(UserFunctionObject child) {
        this.child = child;
    }

    public Continuation apply(Stack<CObject> computationStack, Continuation cf) {
        int arguments = child.getArgCount()+1;

        LinkedList<CObject> args = new LinkedList<CObject>();
        for(int i=0; i<=arguments;i++) {
            args.addFirst(computationStack.pop());
        }

        EnvironmentObject LS;
        CObject self = args.removeFirst();
        LS = (EnvironmentObject)args.removeFirst();

        if (child.parameters !=null ) {
            for(SymbolToken param:child.parameters) {
                Reference common = new Reference(args.removeFirst());
                LS.assign(param, common);
            }
        }
        TokenList scnr = child.getTokenList();
        Continuation ret = new Continuation(LS, ProtoStatementEater.INSTANCE,scnr,cf);
        return ret;
    }

    public int getArgCount() {
        return child.getArgCount()+1;
    }

}
