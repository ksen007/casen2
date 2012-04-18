package edu.berkeley.cs.builtin.objects;

import edu.berkeley.cs.Interpreter;
import junit.framework.TestCase;

import java.io.IOException;

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
public class CIntegerObjectTest extends TestCase {
    public static void run(String s) throws IOException {
        Interpreter.interpret(s);

//        Lexer lexer = new Lexer(new StringReader(s));
//        Scanner scnr = new BasicScanner(lexer);
//
//        CObject LS = new TokenEater(null);
//        CallFrame cf = new CallFrame(LS,LS,scnr);
//        CompoundToken pgm = (CompoundToken)cf.interpret();
//
//        scnr = pgm.getScanner();
//        LS = new CNonPrimitiveObject();
//        cf = new CallFrame(LS,scnr);
//        cf.interpret();
    }

//    public void testSimple() throws Exception {
//
//        eval("1");
//    }

    public void testAdd() throws Exception {

        run("print x = 1156 + \n 5;");
    }

    public void testAdd2() throws Exception {
        run("print x = 1156 + 1156 + 1156; assert x == 3468;");
    }

    public void testSub1() throws Exception {
        run("print x = 5000 -  1156 - 1156; assert x== 2688; ");
    }

    public void testAddMul1() throws Exception {
        run("print x = 5000 - 2 * 1156; assert x== 2688; ");
    }

    public void testAddDiv1() throws Exception {
        run("print x = 1156 - 1156 * 1156/1156; assert x== 0; ");
    }

    public void testVarAddDiv1() throws Exception {
        run("y = 1156; x = y - y*y/y; print y; assert x==0; assert y==1156;");
    }

    public void testVarAddDiv2() throws Exception {
        run("y = 1156; y = y = y + 10; print y; assert y==1166;");
    }

    public void testVarAddDiv3() throws Exception {
        run("y = 1156; ; ; x = y - y*y/y; print y; assert x==0; assert y==1156;");
    }
}
