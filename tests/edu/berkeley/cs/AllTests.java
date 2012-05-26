package edu.berkeley.cs;

import edu.berkeley.cs.builtin.objects.CObject;
import edu.berkeley.cs.builtin.objects.preprocessor.*;
import junit.framework.TestCase;

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
public class AllTests extends TestCase {
    public void testNative1() throws Exception {
        CObject ret = Interpreter.interpret("def foo {# public CObject apply(LinkedList args, CObject SS, CObject DS){ System.out.println(\"Hello World!\"); return (CObject)args.removeFirst();} #}; foo");
    }

    public void testPrint() throws Exception {
        Interpreter.interpret("var x = 1156; print x;");
    }

    public void testSemiColon() throws Exception {
        System.out.println("------------- testSemiColon ----------------------");
        Interpreter.interpret("var x = 1156; print x");
    }

    public void testSemiColon2() throws Exception {
        System.out.println("------------- testSemiColon2 ----------------------");
        Interpreter.interpret("var x = 1156; print x;");
    }

    public void testSemiColon5() throws Exception {
        Interpreter.interpret("var x = 1156; ; print x;");
    }

    public void testSemiColon1() throws Exception {
        Interpreter.interpret("print x;");
    }

    public void testSemiColon3() throws Exception {
        Interpreter.interpret("(var x = 1156); print (x = 1157);");
    }

    public void testDef() throws Exception {
        Interpreter.interpret("def foo { var x = 1; }");
    }

    public void testDef2() throws Exception {
        Interpreter.interpret("def foo { print 9999 }; foo; foo");
    }

    public void testDef3() throws Exception {
        System.out.println("------------- testDef3 ----------------------");
        Interpreter.interpret("def foo { print 9999; 1000; }; print foo; foo");
    }


    public void test1() throws Exception {
        Interpreter.interpret("print 100");
    }


    public void test2() throws Exception {
        CObject ret = Interpreter.interpret("print (2 + 2)");
        assertEquals(((LongToken)ret).value,4);
    }

    public void test3() throws Exception {
        CObject ret = Interpreter.interpret("print (7*8)");
        assertEquals(((LongToken)ret).value,56);
    }

    public void test4() throws Exception {
        CObject ret = Interpreter.interpret("print (5/3)");
        assertEquals(((LongToken)ret).value,1);
    }
    public void test5() throws Exception {
        CObject ret = Interpreter.interpret("print (3+4*5)");
        assertEquals(((LongToken)ret).value,23);
    }

    public void test6() throws Exception {
        CObject ret = Interpreter.interpret("print 5.4");
        assertEquals(((DoubleToken)ret).value,5.4);
    }

    public void test7() throws Exception {
        CObject ret = Interpreter.interpret("print (5.4*8.9)");
        assertEquals(((DoubleToken)ret).value,48.06);
    }

    public void test8() throws Exception {
        CObject ret = Interpreter.interpret("print \"Hello World!\n\"");
        assertEquals(((StringToken)ret).value,"Hello World!\n");
    }

    public void test9() throws Exception {
        CObject ret = Interpreter.interpret("print \"Hello\"+\" World!\n\"");
        assertEquals(((StringToken)ret).value,"Hello World!\n");
    }

    public void test10() throws Exception {
        CObject ret = Interpreter.interpret("print true");
        assertEquals(((BooleanToken)ret).value,true);
    }

    public void test11() throws Exception {
        CObject ret = Interpreter.interpret("print (4 > 5)");
        assertEquals(((BooleanToken)ret).value,false);
    }

    public void test12() throws Exception {
        CObject ret = Interpreter.interpret("print (\"Hello\" != \"Hello\")");
        assertEquals(((BooleanToken)ret).value,true);
    }

    public void test13() throws Exception {
        CObject ret = Interpreter.interpret("print (true || false)");
        assertEquals(((BooleanToken)ret).value,true);
    }

    public void test14() throws Exception {
        System.out.println("14.");
        CObject ret = Interpreter.interpret("print ((print false) && (print true))");
        assertEquals(((BooleanToken)ret).value,false);
    }

    public void test15() throws Exception {
        System.out.println("15.");
        CObject ret = Interpreter.interpret("print !true");
        assertEquals(((BooleanToken)ret).value,false);
    }

    public void test16() throws Exception {
        System.out.println("16.");
        CObject ret = Interpreter.interpret("print {1 + 2}");
        assertTrue(ret instanceof UserFunctionObject);
        System.out.println(ret);
    }

    public void test17() throws Exception {
        System.out.println("17.");
        CObject ret = Interpreter.interpret("print {| x, y |\n" +
                "x = x + 1\n" +
                "return x + y\n" +
                "}");
        assertTrue(ret instanceof UserFunctionObject);
        System.out.println(ret);
    }

    public void testClosure() throws Exception {
        Interpreter.interpret("load \"examples/tutorial/closure.sn\"");
    }

    public void testTypes() throws Exception {
        Interpreter.interpret("load \"examples/tutorial/types.sn\"");
    }

    public void testPatterns() throws Exception {
        Interpreter.interpret("load \"examples/tutorial/patterns.sn\"");
    }

    public void testAssignment() throws Exception {
        Interpreter.interpret("load \"examples/tutorial/assignment.sn\"");
    }


//    public void test200() throws Exception {
//        CObject ret = Interpreter.interpret("");
//    }
//    public void test200() throws Exception {
//        CObject ret = Interpreter.interpret("");
//    }
//    public void test200() throws Exception {
//        CObject ret = Interpreter.interpret("");
//    }
}

//// ///////////////////////////////////////
//// function types
//print { 1 + 2 }
////  1 `+ 2
//
//print {|x, y|
//    x = x + y
//    return x + y
//}
//
////  |`x,`y,|
////  `
////  `x `= `x `+ `y `
////  `return `x `+ `y `
////
//
//// invoking a function
//print ({1 + 2}())
//// 3
//
//print ({|x, y|
//    x = x + y
//    return (x + y)
//}(2,3))
//// 8
//
//print ({ return "Hello World!" }())
//// Hello World!
//
//print ({ "Hello World!" }())
//// Hello World!
//
//// ///////////////////////////////////////
//// symbol type
//// any identifier
//print tokenToExpr (x)
//// `x
//print tokenToExpr (#)
//// `#
//
//print tokenToExpr (*)
//// `*
//
//print tokenToExpr (abc123)
//// `abc123
//
//// ///////////////////////////////////////
//// meta token type
//// any identifier preceded by @
//// @token, @expr, @nl, @eof are reserved
//print @hello
//// @hello
//print @token
//// @token
//print @x
//// @x
//print @expr
//// @expr
//print @eof
//// @eof
//
//// ///////////////////////////////////////
//// null is a special object
//print null
//// null
//
//// ///////////////////////////////////////
//// object type
//// everything is object
//// even integral numbers, floating point numbers, strings, Booleans, functions, symbols, meta tokens are objects
//// a scope is an object---more on this later
//// new objects can be created as follows
//print new Object
//
//
//
//
////(var x = 5000 + 2 * 1156); print x; assert (x== 2688); ");
