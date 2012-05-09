package edu.berkeley.cs.parser;

import edu.berkeley.cs.builtin.objects.CObject;
import edu.berkeley.cs.builtin.objects.preprocessor.CompoundToken;
import edu.berkeley.cs.builtin.objects.preprocessor.StringToken;
import edu.berkeley.cs.builtin.objects.preprocessor.SymbolToken;
import edu.berkeley.cs.builtin.objects.preprocessor.Token;
import edu.berkeley.cs.lexer.Scanner;

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
    private Stack<Integer> tokenStack;
    private CObject LS;
    private Scanner scnr;
    private CObject environment;


    public CallFrame(CObject LS, CObject base, CObject environment, Scanner scnr) {
        Token t;

        this.environment = environment;
        this.LS = LS;
        this.scnr = scnr;
        parseRuleStack = new Stack<RuleNode>();
        computationStack = new Stack<CObject>();
        tokenStack = new Stack<Integer>();

        if (scnr!=null){
            computationStack.push(base);
            parseRuleStack.push(base.getRuleNode());

            t = scnr.nextToken();
            tokenStack.push(OperatorPrecedence.getInstance().getPrecedence(t));
            scnr.pushBack(t);
        }

    }

    private static boolean matchesToken(Token t, String sym) {
        if (t instanceof SymbolToken) {
            return ((SymbolToken)t).symbol == SymbolTable.getInstance().getId(sym);
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
//        return computationStack.peek();
    }

    private boolean consumeSymbol(RuleNode currentRule, Token t) {
        RuleNode ret = currentRule.matchSymbol(t);
        if (ret!=null) {
            parseRuleStack.pop();
            parseRuleStack.push(ret);
            if (matchesToken(t,"=")) {
                tokenStack.pop();
                tokenStack.push(OperatorPrecedence.getInstance().getPrecedence(t));
            }
            return true;
        }
        return false;
    }

    private boolean consumeToken(RuleNode currentRule, Token t) {
        RuleNode toBePushed;
        if ((toBePushed = currentRule.getRuleForToken()) !=null) {

            if (t instanceof CompoundToken) {
                t = new CompoundToken((CompoundToken)t,LS);
            }
            if (matchesToken(t,"exprToToken")) {
                t = scnr.nextToken();
                if (consumeExpr(toBePushed,t)) return true;
                scnr.pushBack(t);
            } else {
                parseRuleStack.pop();
                parseRuleStack.push(toBePushed);
                computationStack.push(t);
                return true;
            }
        }
        return false;
    }

    private boolean consumeExpr(RuleNode toBePushed, Token t) {
        RuleNode rn;

        if ((rn = contextLookAhead(LS, environment, t, false))!=null) {
            parseRuleStack.pop();
            parseRuleStack.push(toBePushed);

            Integer prec = toBePushed.getOptionalPrecedence();
            if (prec !=null) {
                tokenStack.pop();
                tokenStack.push(prec);
            }

            computationStack.push(LS);
            parseRuleStack.push(rn);
            tokenStack.push(OperatorPrecedence.getInstance().getPrecedence(t));
            scnr.pushBack(t);
            return true;
        }
        return false;
    }

    private boolean consumeAction(RuleNode currentRule, Token t) {
        if (currentRule.getRuleForAction() != null) {
            doAction(currentRule,t);

            if (computationStack.peek().isException()) return true;

            tryShifting();
            return true;
        }
        return false;
    }

    private void doAction(RuleNode currentRule, Token t) {
        scnr.pushBack(t);
        parseRuleStack.pop();
        tokenStack.pop();
        currentRule.getRuleForAction().apply(computationStack);
    }

    private void tryShifting() {
        CObject nt = computationStack.peek();
        Token t = scnr.nextToken();
        RuleNode reduce;
        int tmp;
        if (parseRuleStack.isEmpty()) {
            reduce = null;
            tmp = 0;
        } else {
            reduce = parseRuleStack.peek();
            tmp = tokenStack.peek();
        }

        RuleNode rn;
        if ((rn=shift(reduce,nt,tmp,t))!=null) {
            parseRuleStack.push(rn);
            tokenStack.push(OperatorPrecedence.getInstance().getPrecedence(t));
        }
        scnr.pushBack(t);
    }

    public boolean interpretAux() {
        Token t = scnr.nextToken();
        RuleNode currentRule = parseRuleStack.peek();

        try {
        if (consumeSymbol(currentRule,t)) return true;
        if (!matchesToken(t,"\n")) {
            if (consumeToken(currentRule,t)) return true;
            RuleNode toBePushed;
            if ((toBePushed = currentRule.getRuleForNonTerminal()) !=null) {
                if (consumeExpr(toBePushed,t)) return true;
            }
        }
        if (consumeAction(currentRule,t)) return true;

        if (matchesToken(t,"\n")) {
            return true;
        }
        } catch (Exception e) {
            StringToken ret = new StringToken(null,"Failed to consume "+t+" with "+this+ " because "+e);
            ret.setException();
            computationStack.push(ret);
            return true;
        }
//        System.out.println(currentRule);
        StringToken ret = new StringToken(null,"Failed to consume "+t+" with "+this);
        ret.setException();
        computationStack.push(ret);
        return true;
    }

    private static boolean isProgressPossible(RuleNode rn, Token t) {
        return rn !=null
                && (rn.matchSymbol(t)!=null
                || rn.getRuleForNonTerminal()!=null
                || rn.getRuleForToken()!=null
                || rn.getRuleForAction()!=null);
    }

    private RuleNode shift(RuleNode reduce, CObject shift, int exprPrecedence, Token shiftOperator) {
        boolean first = isProgressPossible(reduce,shiftOperator);
        RuleNode ret;
        boolean second = (ret = contextLookAhead(shift,null,shiftOperator,true))!=null;
        if (!second) return null;
        if (!first) return ret;
        if (OperatorPrecedence.getInstance().isShift(exprPrecedence,shiftOperator))
            return ret;
        else
            return null;
    }


    private static RuleNode contextLookAhead(CObject LS, CObject extra, Token t, boolean isProto) {
        CObject current;
        RuleNode ret;

        current = LS;
        while(current!=null) {
            ret = current.getRuleNode();
            if (ret!=null && ret.matchSymbol(t)!=null) {
                return ret;
            }
            current = current.getParent(isProto);
        }
        if (extra != null ) {
            ret = extra.getRuleNode();
            if (ret!=null && ret.matchSymbol(t)!=null) {
                return ret;
            }
        }

        current = LS;
        while(current!=null) {
            ret = current.getRuleNode();
            if (ret.getRuleForToken()!=null) {
                return ret;
            }
            current = current.getParent(isProto);
        }
        if (extra != null ) {
            ret = extra.getRuleNode();
            if (ret.getRuleForToken()!=null) {
                return ret;
            }
        }

        current = LS;
        while(current!=null) {
            ret = current.getRuleNode();
            if (ret.getRuleForNonTerminal()!=null) {
                return ret;
            }
            current = current.getParent(isProto);
        }
        if (extra != null ) {
            ret = extra.getRuleNode();
            if (ret.getRuleForNonTerminal()!=null) {
                return ret;
            }
        }

        current = LS;
        while(current!=null) {
            ret = current.getRuleNode();
            if (ret.getRuleForAction()!=null) {
                return ret;
            }
            current = current.getParent(isProto);
        }
        if (extra != null ) {
            ret = extra.getRuleNode();
            if (ret.getRuleForAction()!=null) {
                return ret;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LS:\n");
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
