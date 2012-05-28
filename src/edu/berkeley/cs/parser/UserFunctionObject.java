package edu.berkeley.cs.parser;

import edu.berkeley.cs.builtin.Reference;
import edu.berkeley.cs.builtin.functions.Invokable;
import edu.berkeley.cs.builtin.objects.mutable.*;
import edu.berkeley.cs.builtin.objects.singleton.ProtoStatementEater;
import edu.berkeley.cs.lexer.TokenList;

import java.util.ArrayList;
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
public class UserFunctionObject extends FunctionObject {
    public ArrayList<CObject> tokens;
    public ArrayList<SymbolToken> parameters;

    public Continuation apply(Stack<CObject> computationStack, Continuation cf) {
        int arguments = parameters==null?0:parameters.size();

        LinkedList<CObject> args = new LinkedList<CObject>();
        for(int i=0; i<=arguments;i++) {
            args.addFirst(computationStack.pop());
        }

        EnvironmentObject LS;
        CObject self = args.removeFirst();

        LS = new EnvironmentObject();
        LS.setPrototype(scope);
        LS.assign(SymbolTable.getInstance().self,new Reference(self));
        LS.assign(SymbolTable.getInstance().DS, new Reference(cf.LS));

        if (parameters!=null) {
            for(SymbolToken param:parameters) {
                Reference common = new Reference(args.removeFirst());
                LS.assign(param, common);
            }
        }
        
        TokenList scnr = getTokenList();
        Continuation ret = new Continuation(LS, ProtoStatementEater.INSTANCE,scnr,cf);
        LS.thisContinuation = ret;
        return ret;
    }

    public int getArgCount() {
        return parameters==null?0:parameters.size();
    }


    public TokenList getTokenList() {
        return new TokenList(tokens);
    }

    public UserFunctionObject(ArrayList<SymbolToken> par, ArrayList<CObject> tokens, CObject SS) {
        super(null,SS);
        this.tokens = tokens;
        this.parameters = par;
        int N = parameters==null?0:parameters.size();

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().scope);
        this.addAction(new Invokable() {
            public CObject apply(LinkedList<CObject> args, CObject SS, CObject DS) {
                UserFunctionObject self = (UserFunctionObject)args.removeFirst();
                return self.scope;
            }
        },this);

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().lparen);
        for(int i=0; i<N; i++) {
            this.addMeta(SymbolTable.getInstance().expr);
            if (i<N-1)
                this.addObject(SymbolTable.getInstance().comma);
        }
        this.addObject(SymbolTable.getInstance().rparen);
        this.addAction(this);

        this.addNewRule();
        this.addObject(SymbolTable.getInstance().lparen);
        this.addMeta(SymbolTable.getInstance().expr);
        if (N > 0)
            this.addObject(SymbolTable.getInstance().comma);

        for(int i=0; i<N; i++) {
            this.addMeta(SymbolTable.getInstance().expr);
            if (i<N-1)
                this.addObject(SymbolTable.getInstance().comma);
        }
        this.addObject(SymbolTable.getInstance().rparen);
        this.addAction(new UserExtraAction(this));
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{').append(' ');
        if (parameters!=null) {
            sb.append('|');
            for(SymbolToken param:parameters) {
                sb.append(param);
                sb.append(',');
            }
            sb.append("|\n");
        }
        for(CObject t:tokens) {
            sb.append(t.toString()).append(' ');
        }
        sb.append('}');
        return sb.toString();
    }

    public Continuation execute(Continuation DS) {
        Stack<CObject> args = new Stack<CObject>();
        args.push(this);
        return apply(args,DS);
    }

//    public CObject executeInScope() {
//        LinkedList<CObject> args = new LinkedList<CObject>();
//        args.add(this);
//        args.add(scope);
//        return apply(args,null,true);
//    }

}
