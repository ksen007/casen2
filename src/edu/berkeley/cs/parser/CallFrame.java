package edu.berkeley.cs.parser;

import edu.berkeley.cs.builtin.objects.CObject;
import edu.berkeley.cs.builtin.objects.preprocessor.ExptToTokenObject;
import edu.berkeley.cs.builtin.objects.preprocessor.StringToken;
import edu.berkeley.cs.builtin.objects.preprocessor.SymbolToken;
import edu.berkeley.cs.lexer.Scanner;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
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
public class CallFrame {
    private Stack<RuleNode> parseRuleStack;
    private Stack<CObject> computationStack;
    private Stack<Integer> precedenceStack;
    CObject LS;
    private Scanner scnr;
    public static final boolean DEBUG = false;


    public CallFrame(CObject LS, CObject base, Scanner scnr) {
        this.LS = LS;
        this.scnr = scnr;
        parseRuleStack = new Stack<RuleNode>();
        computationStack = new Stack<CObject>();
        precedenceStack = new Stack<Integer>();

        if (scnr!=null){
            computationStack.push(base);
            parseRuleStack.push(base.getRuleNode());

            precedenceStack.push(0);
        }

    }

    public static String getStackTrace(Throwable aThrowable) {
        if (aThrowable !=null ){
            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            aThrowable.printStackTrace(printWriter);
            return result.toString();
        }
        return null;
    }

    private static boolean matchesToken(CObject t, SymbolToken sym) {
        if (t instanceof SymbolToken) {
            return ((SymbolToken)t).symbol == sym.symbol;
        }
        return false;
    }

    public CObject interpret() {
        if (RuleNode.DEBUG) System.out.println("Interpretation start.");
        while(true) {
            interpretAux();
            CObject top = computationStack.peek();
            if (top.isReturn()) {
                top.clearReturn();
                return top;
            }
            if (top.isException()) {
                return top;
            }
        }
    }

    public boolean interpretAux() {
        CObject t = scnr.nextToken();
        RuleNode currentRule = parseRuleStack.peek();

        try {
            if (consumeSymbol(currentRule,t)) return true;

            if (matchesToken(t,SymbolTable.getInstance().exprToToken) && !currentRule.isActionOnly()) {
                parseRuleStack.push((new ExptToTokenObject(scnr)).getRuleNode());
                precedenceStack.push(0);
                return true;
            }

            if (!matchesToken(t,SymbolTable.getInstance().newline) && !matchesToken(t,SymbolToken.end)) {
                if (consumeToken(currentRule,t)) return true;
                if (consumeExpr(currentRule,t)) return true;
                if (consumeOther(currentRule,t)) return true;
            }
            if (consumeAction(currentRule,t)) return true;

            if (matchesToken(t,SymbolTable.getInstance().newline)) {
                return true;
            }
        } catch (Exception e) {
            StringToken ret = new StringToken(null,"Failed to consume "+t+" at "+t.locationString()
                    +" with "+this+ " because "+ getStackTrace(e));
            ret.setException();
            computationStack.push(ret);
            return true;
        }
        StringToken ret = new StringToken(null,"Failed to consume "+t+" at "+t.locationString()+" with "+this);
        ret.setException();
        computationStack.push(ret);
        return true;
    }

    private boolean consumeSymbol(RuleNode currentRule, CObject t) {
        RuleNode ret = currentRule.getRuleForObject(t);
        if (ret!=null) {

            if (DEBUG) {
                if (!t.isNoSpace()) System.out.print(' ');
                //System.out.print("s:");
                System.out.print(t);
            }
            LS.setPosition(t.getPosition());
            parseRuleStack.pop();
            parseRuleStack.push(ret);
            if (matchesToken(t,SymbolTable.getInstance().assign)) {
                precedenceStack.pop();
                precedenceStack.push(OperatorPrecedence.getInstance().getPrecedence(t));
            }
            return true;
        }
        return false;
    }

    private boolean consumeToken(RuleNode currentRule, CObject t) {
        RuleNode toBePushed;
        if ((toBePushed = currentRule.getRuleForToken()) !=null) {
            if (DEBUG) {
                if (!t.isNoSpace()) System.out.print(' ');
                //System.out.print("t:");
                System.out.print(t);
            }
            parseRuleStack.pop();
            parseRuleStack.push(toBePushed);
            computationStack.push(t);
            return true;
        }
        return false;
    }

    private boolean consumeExpr(RuleNode currentRule, CObject t) {
        Pair rn;
        RuleNode toBePushed;

        if ((toBePushed = currentRule.getRuleForExpr()) !=null) {
            if ((rn = contextLookAhead(LS, t))!=null) {
                if (DEBUG) {
                    System.out.print('(');
                }
                parseRuleStack.pop();
                parseRuleStack.push(toBePushed);

                computationStack.push(LS);
                parseRuleStack.push(rn.fst);
                precedenceStack.push(rn.snd);
                scnr.pushBack(t);
                return true;
            }
        }
        return false;
    }

    private boolean consumeOther(RuleNode currentRule, CObject t) {
        Pair rn;
        OtherPair toBePushed;
        CObject ctxt;

        if ((toBePushed = currentRule.getRuleForOther()) !=null) {
            ctxt = toBePushed.fst.execute(LS);
            if ((rn = contextLookAhead(ctxt, t))!=null) {
                if (DEBUG) {
                    System.out.print('(');
                }
                parseRuleStack.pop();
                parseRuleStack.push(toBePushed.next);

                computationStack.push(ctxt);
                parseRuleStack.push(rn.fst);
                precedenceStack.push(rn.snd);
                scnr.pushBack(t);
                return true;
            }
        }
        return false;
    }

    private boolean consumeAction(RuleNode currentRule, CObject t) {
        if (currentRule.getRuleForAction() != null) {
            doAction(currentRule,t);

            if (computationStack.peek().isException()) return true;

            tryShifting(currentRule);
            return true;
        }
        return false;
    }

    private void doAction(RuleNode currentRule, CObject t) {
        scnr.pushBack(t);
        parseRuleStack.pop();
        precedenceStack.pop();
        currentRule.getRuleForAction().apply(computationStack,this);
    }

    private void tryShifting(RuleNode currentRule) {
        CObject nt = computationStack.peek();
        CObject t = scnr.nextToken();
        RuleNode reduce;
        int tmp;
        if (parseRuleStack.isEmpty()) {
            reduce = null;
            tmp = 0;
        } else {
            reduce = parseRuleStack.peek();
            tmp = precedenceStack.peek();
        }

        Pair rn;
        if ((rn=shift(reduce,nt,tmp,t))!=null) {
            if (DEBUG) {
                System.out.print('_');
            }
            parseRuleStack.push(rn.fst);
            precedenceStack.push(rn.snd);
        } else {
            if (DEBUG) {
                System.out.print(')');
            }
        }
        scnr.pushBack(t);
    }


    private Pair shift(RuleNode reduce, CObject shift, int exprPrecedence, CObject shiftOperator) {
        boolean first = isProgressPossible(reduce,shiftOperator);
        Pair ret;
        boolean second = (ret = contextLookAhead(shift,shiftOperator))!=null;
        if (!second) return null;
        if (!first) return ret;
        if (OperatorPrecedence.getInstance().isShift(exprPrecedence,ret.snd))
            return ret;
        else {
            return null;
        }
    }

    private static boolean isProgressPossible(RuleNode rn, CObject t) {
        if (rn ==null) return false;
        if (rn.getRuleForObject(t)!=null) return true;
        if (matchesToken(t,SymbolTable.getInstance().exprToToken) && !rn.isActionOnly()) return true;
        if (!matchesToken(t,SymbolToken.end) && !matchesToken(t,SymbolTable.getInstance().newline)) {
            if (rn.getRuleForToken()!=null) return true;
            if (rn.getRuleForExpr()!=null) return true;
            if (rn.getRuleForOther()!=null) return true;
        }
        if (rn.getRuleForAction()!=null) return true;
        return false;
    }


    private static Pair contextLookAhead(CObject LS, CObject t) {
        CObject current;
        RuleNode ret, ret2;


        current = LS;
        while(current!=null) {
            ret = current.getRuleNode();
            if (ret!=null && (ret2 = ret.getRuleForObject(t))!=null) {
                return new Pair(ret,ret2.getOptionalPrecedence());
            }
            current = current.getPrototype();
        }

        current = LS;
        if (current!=null) {
            ret = current.getRuleNode();
            if (matchesToken(t,SymbolTable.getInstance().exprToToken) && !ret.isActionOnly()) {
                return new Pair(ret,0);
            }
        }

        if (!matchesToken(t,SymbolTable.getInstance().newline) && !matchesToken(t,SymbolToken.end)) {

            current = LS;
            while(current!=null) {
                ret = current.getRuleNode();
                if (ret!=null && (ret2=ret.getRuleForToken())!=null) {
                    return new Pair(ret,ret2.getOptionalPrecedence());
                }
                current = current.getPrototype();
            }

            current = LS;
            while(current!=null) {
                ret = current.getRuleNode();
                if (ret!=null && (ret2=ret.getRuleForExpr())!=null) {
                    return new Pair(ret,ret2.getOptionalPrecedence());
                }
                current = current.getPrototype();
            }

            OtherPair oret2;
            current = LS;
            while(current!=null) {
                ret = current.getRuleNode();
                if (ret!=null && (oret2=ret.getRuleForOther())!=null) {
                    return new Pair(ret,oret2.next.getOptionalPrecedence());
                }
                current = current.getPrototype();
            }

        }
        current = LS;
        while(current!=null) {
            ret = current.getRuleNode();
            if (ret!=null && ret.getRuleForAction()!=null) {
                return new Pair(ret,0);
            }
            current = current.getPrototype();
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LS:\n");
        CObject current = LS;
        while(current != null) {
            sb.append(current.getRuleNode());
            sb.append("---\n");
            current = current.getPrototype();
        }
        sb.append(LS.getRuleNode());
        sb.append("Computation Stack:\n");
        for (CObject next : computationStack) {
            sb.append("*** ");
            sb.append(next);
            sb.append("\n");
        }

        sb.append("Rule Stack:\n");
        for (RuleNode next : parseRuleStack) {
            sb.append("*** ");
            sb.append(next);
        }

        return sb.toString();
    }


}

class Pair {
    RuleNode fst;
    int snd;

    Pair(RuleNode fst, int snd) {
        this.fst = fst;
        this.snd = snd;
    }
}