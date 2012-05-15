package edu.berkeley.cs.builtin.objects;

import edu.berkeley.cs.builtin.functions.GetField;
import edu.berkeley.cs.builtin.functions.Invokable;
import edu.berkeley.cs.builtin.functions.PutField;
import edu.berkeley.cs.builtin.objects.preprocessor.*;
import edu.berkeley.cs.lexer.*;
import edu.berkeley.cs.parser.*;

import java.io.*;
import java.util.IdentityHashMap;
import java.util.TreeMap;

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


    public CObject(SourcePosition position) {
        this.position = position;
    }

    public String locationString() {
        return position==null?"":position.toString();
    }


    public CObject() {
        //rules = new RuleNode(null);
    }

    public void assign(SymbolToken var, Reference val) {
        var.clearNoSpace();

        this.addNewRule();
        this.addObject(var);
        this.addAction(new GetField(val));

        this.addNewRule();
        this.addObject(var);
        this.addObject(SymbolTable.getInstance().assign);
        this.addMeta(SymbolTable.getInstance().expr);
        this.addAction(new PutField(val));
    }

    public void setRule(CObject methods) {
        this.rules = methods.rules;
    }

    public CObject getParent() {
        return prototype==null?null:prototype.value;
    }

    public void setParent(CObject obj) {
        this.prototype = new Reference(obj);
        if (obj !=null) {
            assign(SymbolTable.getInstance().prototype, prototype);
        }
    }

    public RuleNode getRuleNode() {
        return rules;
    }

    private static CompoundToken parseIt(Reader in,String fname) throws IOException {
        Lexer lexer = new StandardLexer(in);
        Scanner scnr = new BasicScanner(lexer);

        CObject LS = new TokenEater(null,fname);
        CallFrame cf = new CallFrame(LS,LS,null,scnr);
        CompoundToken pgm = (CompoundToken)cf.interpret();
        return pgm;
    }

    public CObject eval(String s) {
        try {
            CompoundToken pgm = parseIt(new StringReader(s),null);
            CObject ret = pgm.execute(this,true);
            if (ret.isException()) {
                throw new RuntimeException("Eval:\n"+ret);
            }
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private static TreeMap<String,CompoundToken> codeCache = new TreeMap<String, CompoundToken>();
    public static String currentFile = null;
    public static IdentityHashMap<CompoundToken,CObject> staticObjects = new IdentityHashMap<CompoundToken, CObject>();

    public static CObject load(String file, boolean reload) {
        Reader in = null;
        CompoundToken pgm;
        try {
            if (reload || !codeCache.containsKey(file)) {
                in = new BufferedReader(new FileReader(file));
                pgm = parseIt(in,file);
                codeCache.put(file,pgm);
            } else {
                pgm = codeCache.get(file);
            }
            return pgm;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            if (in!=null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
        }
    }

    public CObject eq(CObject ret) {
        return this==ret?BooleanToken.TRUE():BooleanToken.FALSE();
    }

    public CObject ne(CObject ret) {
        return this==ret?BooleanToken.FALSE():BooleanToken.TRUE();
    }



    public CObject newDefinitionEater() {
        return new CDefinitionEater(this);
    }


    public CObject loadFile(CObject file) {
        File f;
        if (currentFile != null) {
            f = new File(currentFile);
            f = new File(f.getParent(),((StringToken)file).value);
        } else {
            f = new File(((StringToken)file).value);
        }
        return load(f.getAbsolutePath(),false);
    }

    public CObject reloadFile(CObject file) {
        File f = new File(currentFile);
        f = new File(f.getParent(),((StringToken)file).value);
        return load(f.getAbsolutePath(),true);
    }

    public CObject assignment(CObject sym, CObject value) {
        CObject self = this;
        SymbolToken symbol = (SymbolToken)sym;

        Reference common = new Reference(value);
        assign(symbol,common);
        return value;
    }

    public CObject print(CObject ret) {
        System.out.println(ret);
        return ret;
    }


    public CObject assertEquality(CObject first) {
        if (!((BooleanToken)first).value)
            throw new RuntimeException("assert failed");
        return NullToken.NULL();
    }

    public CObject returnArgument(CObject arg) {
        return arg;
    }

    public CObject returnToken(CObject tok) {
        return tok;
    }


    public CObject whileAction(CObject S1, CObject S2) {
        CompoundToken s1 = (CompoundToken)S1;
        CompoundToken s2 = (CompoundToken)S2;
        CObject ret;
        ret = s1.execute(this,false);
        if (ret.isException()) return ret;

        while(((BooleanToken)ret).value) {
            ret = s2.execute(this,false);
            if (ret.isException()) return ret;

            ret = s1.execute(this,false);
            if (ret.isException()) return ret;
        }
        return NullToken.NULL();
    }



    public CObject onceAction(CObject S) {
        CompoundToken s = (CompoundToken)S;
        CObject val;
        if ((val = CObject.staticObjects.get(s))==null) {
            val = s.execute(this,false);
            if (val.isException()) return val;
            CObject.staticObjects.put(s,val);
        }
        return val;
    }

    public CObject ifAction(CObject c1, CObject S1, CObject S2) {
        BooleanToken cond = (BooleanToken) c1;
        CompoundToken s1 = (CompoundToken)S1;
        CompoundToken s2 = (CompoundToken)S2;
        if (cond.value) {
            CObject ret = s1.execute(this,false);
            if (ret.isException()) return ret;
            return NullToken.NULL();
        } else {
            CObject ret = s2.execute(this,false);
            if (ret.isException()) return ret;
            return NullToken.NULL();
        }
    }

    public CObject unaryNot(CObject operand2) {
        return ((BooleanToken)operand2).value? BooleanToken.FALSE(): BooleanToken.TRUE();
    }

    public CObject unaryMinus(CObject operand2) {
        if (operand2 instanceof LongToken)
            return new LongToken(null,-((LongToken)operand2).value);
        return new DoubleToken(null,-((DoubleToken)operand2).value);

    }

    public CObject newObject() {
        CNonPrimitiveObject ret = new CNonPrimitiveObject();
        //ret.setParent(this,false);
        return ret;
    }

    public CObject cReturn(CObject val) {
        val.setReturn();
        return val;
    }

    public CObject cException(CObject val) {
        val.setException();
        return val;
    }

    public CObject tryCatch(CObject tryAction, CObject var, CObject catchAction) {
        SymbolToken symbol = (SymbolToken) var;
        CompoundToken s1 = (CompoundToken)tryAction;
        CompoundToken s2 = (CompoundToken)catchAction;
        CObject ret = s1.execute(this,false);
        if (ret.isException()) {
            ret.clearException();
            Reference common = new Reference(ret);
            assign(symbol, common);

            ret = s2.execute(this,false);
            if (ret.isException()) return ret;
        }
        return NullToken.NULL();
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
        curr = curr.addMeta(rules,metaSymbol);
        argCount++;
    }

    public void addMeta(int metaSymbol,boolean override) {
        curr = curr.addMeta(rules,metaSymbol,override);
        argCount++;
    }

    public void addObject(CObject object) {
        curr = curr.addObject(object);
    }

    public void addAction(Invokable func) {
        curr = curr.addAction(func,argCount);
    }

    public void addPrecedence(int prec) {
        curr = curr.addPrecedence(prec);
    }

//    public RuleNode getSuperRuleNode() {
//        if (superC == null) return null;
//        return superC.getRuleNode();
//    }
}
