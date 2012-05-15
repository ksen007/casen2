package edu.berkeley.cs;

import edu.berkeley.cs.builtin.objects.CNonPrimitiveObject;
import edu.berkeley.cs.builtin.objects.CObject;
import edu.berkeley.cs.builtin.objects.EnvironmentObject;
import edu.berkeley.cs.builtin.objects.preprocessor.CompoundToken;

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
        CNonPrimitiveObject tmp = new CNonPrimitiveObject();
        return tmp.eval(s);
    }

    public static void main(String[] args) {
        CompoundToken pgm = (CompoundToken)CObject.load(args[0], false);
        CObject ret = pgm.execute(new CNonPrimitiveObject(),true);
        if (ret.isException()) {
            System.err.println(ret);
        }
    }
}

//@todo: void token
//@todo: allow creation of CompoundToken from a sequence of tokens
//@todo: provide undef, isdef, redef, getAction
//@todo: handle return from blocks
//@todo: provide support for reflection
//@todo: coroutine
//@todo: eliminate NativeFunction

