package edu.berkeley.cs.builtin.objects;

import edu.berkeley.cs.builtin.functions.GetField;
import edu.berkeley.cs.builtin.functions.Invokable;
import edu.berkeley.cs.builtin.functions.PutField;
import edu.berkeley.cs.builtin.objects.preprocessor.FunctionObject;
import edu.berkeley.cs.builtin.objects.preprocessor.NativeFunctionObject;
import edu.berkeley.cs.builtin.objects.preprocessor.SymbolToken;
import edu.berkeley.cs.lexer.*;
import edu.berkeley.cs.parser.*;

import java.io.*;

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

/* @keywords
    @block @expr @LS @literal @symbol @nl { } true false null number-literals string-literals def assert print ( ) ,
    |

    */

public class CObject {
    private Reference prototype;
    private RuleNode rules;

    private int flags;
    final private static int RETURN_FLAG = 1;
    final private static int EXCEPTION_FLAG = 2;
    final private static int NO_SPACE_FLAG = 4;


    public void setException() {
        flags = flags | EXCEPTION_FLAG;
    }

    public void clearException() {
        flags = flags & (~EXCEPTION_FLAG);
    }

    public boolean isException() {
        return (flags & EXCEPTION_FLAG) > 0;
    }

    public void setReturn() {
        flags = flags | RETURN_FLAG;
    }

    public void clearReturn() {
        flags = flags & (~RETURN_FLAG);
    }

    public boolean isReturn() {
        return (flags & RETURN_FLAG) > 0;
    }

    public void setNoSpace() {
        flags = flags | NO_SPACE_FLAG;
    }

    public void clearNoSpace() {
        flags = flags & (~NO_SPACE_FLAG);
    }

    public boolean isNoSpace() {
        return (flags & NO_SPACE_FLAG) > 0;
    }


    private SourcePosition position;

    public SourcePosition getPosition() {
        return position;
    }

    public void setPosition(SourcePosition position) {
        this.position = position;
    }

    public CObject(SourcePosition position) {
        this.position = position;
    }

    public String locationString() {
        return position==null?" Unknown location ":position.toString();
    }


    public CObject() {    }

    public void assign(SymbolToken var, Reference val) {
        var.clearNoSpace();

        this.addNewRule();
        this.addObject(var);
        this.addAction(new GetField(val),null);

        this.addNewRule();
        this.addObject(var);
        this.addObject(SymbolTable.getInstance().assign);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addAction(new PutField(val),null);
    }

    public void setRule(CObject methods) {
        this.rules = methods.rules;
    }

    public CObject getPrototype() {
        return prototype==null?null:prototype.value;
    }

    public void setPrototype(CObject obj) {
        this.prototype = new Reference(obj);
        if (obj !=null) {
            assign(SymbolTable.getInstance().prototype, prototype);
        }
    }

    public void delPrototype() {
        this.prototype = null;
        rules.removeObject(SymbolTable.getInstance().prototype);
    }

    public RuleNode getRuleNode() {
        return rules;
    }

    public CObject evalString(String s) {
        return eval(new StringReader(s),s,false);
    }

    public CObject evalFile(String s) {
        try {
            return eval(new BufferedReader(new FileReader(s)),s,true);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private CObject eval(Reader in,String s, boolean isFile) {
        try {
            Lexer lexer = new StandardLexer(in,s,isFile);
            Scanner scnr = new BasicScanner(lexer);
            CallFrame cf = new CallFrame(this, CStatementEater.instance,scnr);
            CObject ret = cf.interpret();
            if (ret.isException()) {
                throw new RuntimeException("Eval:\n"+ret);
            }
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public CObject printDeep() {
        CObject current = this;
        RuleNode ret;
        System.out.println("Object:");
        while(current!=null) {
            ret = current.getRuleNode();
            if (ret!=null) {
                System.out.println("--------");
                System.out.println(ret);
            }
            current = current.getPrototype();
        }
        System.out.println("End");
        return this;
    }


    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CObject)) return false;
        return this == o;
    }

    RuleNode curr;
    int argCount;

    public void addNewRule() {
        if(rules == null) {
            rules = new RuleNode(null);
        }
        curr = rules;
        argCount=0;
    }

    public void addMeta(int metaSymbol) {
        addMeta(metaSymbol,false);
    }

    public void addMeta(int metaSymbol,boolean override) {
        RuleNode tmp = curr.addMeta(rules,metaSymbol,override);
        argCount++;
        if (curr == rules && metaSymbol == SymbolTable.getInstance().token) {
            tmp.addPrecedence(OperatorPrecedence.getInstance().getPrecedence(metaSymbol));
        }
        curr = tmp;
    }

    public void addObject(CObject object) {
        RuleNode tmp = curr.addObject(object);
        if (curr==rules) {
            tmp.addPrecedence(OperatorPrecedence.getInstance().getPrecedence(object));
        }
        curr = tmp;
    }

    public void addOther(FunctionObject object) {
        curr = curr.addOther(object);
        argCount++;
        if (curr==null) {
            System.out.println(curr);
        }
    }


    public void addAction(FunctionObject func, boolean reuse) {
        if (curr==null) {
            System.out.println(curr);
        }
        curr = curr.addAction(new Action(argCount,func,reuse));
    }

    public void addAction(Invokable func, CObject scope) {
        if (curr==null) {
            System.out.println(curr);
        }
        curr = curr.addAction(new Action(argCount,new NativeFunctionObject(func,scope,argCount),false));
    }

}
