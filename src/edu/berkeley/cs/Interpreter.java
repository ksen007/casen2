package edu.berkeley.cs;

import edu.berkeley.cs.builtin.objects.mutable.CObject;
import edu.berkeley.cs.builtin.objects.mutable.EnvironmentObject;
import edu.berkeley.cs.lexer.StandardLexer;
import edu.berkeley.cs.parser.Continuation;
import edu.berkeley.cs.parser.UserFunctionObject;

import java.io.StringReader;

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
public class Interpreter {
    public static CObject interpret(String s) {
        try {
            EnvironmentObject granny = new EnvironmentObject();
            Continuation grannyContinuation = new Continuation(granny, null,null,null);
            granny.thisContinuation = grannyContinuation;

            UserFunctionObject fun = new UserFunctionObject(null,(new StandardLexer(new StringReader(s),s,false)).getTokens(),granny);
            Continuation continuation;
            continuation = fun.execute(grannyContinuation);
            do {
                continuation = continuation.step();
            } while(continuation != grannyContinuation);
            return grannyContinuation.computationStack.pop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String[] args) {
//        EnvironmentObject tmp = new EnvironmentObject();
//        CObject ret = tmp.evalFile(args[0]);
//        if (ret.isException()) {
//            System.err.println(ret);
//        }
//    }
}

//@todo make shift default
//@todo make () special case
//@todo make single pass interpreter
//@todo implement {{ }} to embed Java code
//@todo implement multi lookahead for symbols

//@todo: allow creation of CompoundToken from a sequence of tokens
//@todo: provide undef, isdef, redef, getAction
//@todo: handle return from blocks
//@todo: provide support for reflection
//@todo: coroutine
//@todo: eliminate NativeFunction

