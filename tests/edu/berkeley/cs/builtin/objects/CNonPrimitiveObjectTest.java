package edu.berkeley.cs.builtin.objects;

import edu.berkeley.cs.lexer.*;
import edu.berkeley.cs.parser.CallFrame;
import edu.berkeley.cs.parser.ParseException;
import junit.framework.TestCase;

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
public class CNonPrimitiveObjectTest extends TestCase {
    public void testAssignmentLeftAssociativity() throws Exception {
        System.out.println("------------- testAssignmentLeftAssociativity ----------------------");
        CIntegerObjectTest.run("LS x = (LS y = 1156); assert x == y;");
    }

    public void testAssert0() throws Exception {
        CIntegerObjectTest.run("assert 1156 == 1156;");
    }

    public void testAssert1() throws Exception {
        CIntegerObjectTest.run("LS x = 1156; assert x == 1156;");
    }

    public void testAssert2() throws Exception {
        System.out.println("------------- testAssert2 ----------------------");
        try {
            CIntegerObjectTest.run("LS x = 1156; assert (x == 0); print x");
            assertFalse("x is not 0",true);
        } catch (RuntimeException e) {
            System.out.println("test is fine because x is 1156");
        }
    }

    public void testSemiColon() throws Exception {
        System.out.println("------------- testSemiColon ----------------------");
        CIntegerObjectTest.run("LS x = 1156; print x");
    }

    public void testSemiColon2() throws Exception {
        System.out.println("------------- testSemiColon2 ----------------------");
        CIntegerObjectTest.run("LS x = 1156; print x;");
    }

    public void testSemiColon5() throws Exception {
        CIntegerObjectTest.run("LS x = 1156; ; print x;");
    }

    public void testSemiColon1() throws Exception {
        try {
            CIntegerObjectTest.run("print x;");
            assertFalse("x is uninitialized",true);
        } catch (ParseException e) {
            System.out.println("test is fine because x is uninitialized \n"+e);
        }
    }

    public void testSemiColon3() throws Exception {
        CIntegerObjectTest.run("print (LS x = 1156);");
    }

    public void testSemiColon4() throws Exception {
        CIntegerObjectTest.run("print 1156;");
    }

    public void testPrint() throws Exception {
        CIntegerObjectTest.run("print 1156;");
    }

    public void testDef() throws Exception {
        CIntegerObjectTest.run("def foo { LS x = 1; }");
    }

    public void testDef2() throws Exception {
        CIntegerObjectTest.run("def foo { print 9999 } foo; foo");
    }

    public void testDef3() throws Exception {
        System.out.println("------------- testDef3 ----------------------");
        CIntegerObjectTest.run("def foo { print 9999; 1000; }; print foo; foo");
    }

    public void testDef4() throws Exception {
        System.out.println("------------- testDef4 ----------------------");
        CIntegerObjectTest.run("LS t = 899; def foo { self t = 788 }; print foo; assert (foo == 788)");
    }


    public void testDef5() throws Exception {
        System.out.println("------------- testDef5 ----------------------");
        CIntegerObjectTest.run("LS t = 899; def foo { print (self t); 1000 }; print foo");
    }

    public void testDef6() throws Exception {
        System.out.println("------------- testDef6 ----------------------");
        CIntegerObjectTest.run("def foo { LS x = 1; LS y = x + 2; LS };  LS t = foo; assert ((t x) == 1); assert ((t y) == 3);");
    }

    public void testDef7() throws Exception {
        System.out.println("------------- testDef7 ----------------------");
        CIntegerObjectTest.run("def foo @argument {|a| LS x = 1; LS y = (a + 5); LS }; LS t = (foo 10); assert ((t x) == 1); assert ((t y) == 15);");
    }

    public void testDef8() throws Exception {
        System.out.println("------------- testDef8 ----------------------");
        CIntegerObjectTest.run("def foo @argument {|a| print a  }; LS t = (foo 10); assert (t == 10)");
    }

    public void testDef9() throws Exception {
        System.out.println("------------- testDef9 ----------------------");
        CIntegerObjectTest.run("LS foo = {LS x = 1; LS y = (x + 5); LS }; print 10; print ((foo ()) y)");
        //System.out.println(cf);
    }

    public void testIf1() throws Exception {
        System.out.println("------------- testIf1 ----------------------");
        CIntegerObjectTest.run("if true then {print 1 } else {print 0} ");
        //System.out.println(cf);
    }

    public void testIf2() throws Exception {
        System.out.println("------------- testIf1 ----------------------");
        CIntegerObjectTest.run("def fac @argument {|n| if n > 1 then {LS ret = n * (self fac (n-1)) } else { LS ret = 1} ret}; print (fac 10) ");
        //System.out.println(cf);
    }

    public void testIf4() throws Exception {
        System.out.println("------------- testIf4 ----------------------");
        try {
            CIntegerObjectTest.run("def fac @argument {|n| if n > 1 then {ret = n * (self fac (n-1)) } else { ret = m} ret}; print (fac 2) ");
            assertFalse("m is uninitialized",true);
        } catch (ParseException e) {
            System.out.println("test is fine because m is uninitialized \n"+e);
        }
        //System.out.println(cf);
    }

    public void testIf3() throws Exception {
        System.out.println("------------- testIf1 ----------------------");
        CIntegerObjectTest.run("def do @argument unless @argument {|b, cond| if cond then { print 1} else { b ( self ) } }; LS x = 6; do {print x} unless false ");
        //System.out.println(cf);
    }

    public void testDef10() throws Exception {
        System.out.println("------------- testDef10 ----------------------");
        CIntegerObjectTest.run("LS foo = {|a| LS x = 1; LS y = (a + 5); LS }; print 10; assert (((foo ( 111)) y) == 116)");
        //System.out.println(cf);
    }

    public void test1() throws Exception {
        //RuleNode.DEBUG = true;
        System.out.println("------------- test1 ----------------------");
        CIntegerObjectTest.run("LS x = 3 ; LS x = 4 ; print x;");
    }

    public void testSemiColonx() throws Exception {
        //RuleNode.DEBUG = true;
        System.out.println("------------- testSemiColon ----------------------");
        CIntegerObjectTest.run("LS x = 3 ; print x;");
    }

    public void testArray() throws Exception {

        CIntegerObjectTest.run("def CArray @argument @edu.berkeley.cs.builtin.objects.CArray endef ; x = (CArray 10); x[1] = 89; print (x[1]);");
    }

    public void testWhile() throws Exception {
        CIntegerObjectTest.run("i=0; while {i<10}{i = i + 1; print i; }; ");

//        CIntegerObjectTest.eval("i = 2; i = (i + 1); i = (i+1); print i ");
    }

    public void testNull() throws Exception {
        System.out.println("------------- testNull ----------------------");
        CIntegerObjectTest.run("assert (null == null)");
    }

    public void testNull2() throws Exception {
        System.out.println("------------- testNull2 ----------------------");
        try {
            CIntegerObjectTest.run("x = null; assert (x != null)");
            assertFalse("x is null",true);
        } catch (ParseException e) {
            System.out.println("test is fine because x is null \n");
        }

    }
}
