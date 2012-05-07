package edu.berkeley.cs.parser;

import edu.berkeley.cs.builtin.objects.CObject;
import edu.berkeley.cs.builtin.objects.CStatementEater;
import edu.berkeley.cs.builtin.objects.EnvironmentObject;
import edu.berkeley.cs.builtin.objects.preprocessor.CompoundToken;
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
    private Stack<Token> tokenStack;
    private CObject LS;
    private Scanner scnr;
    private CObject environment;


    public CallFrame(CObject LS, CObject base, CObject environment, Scanner scnr) {
        init(LS,base,environment,scnr);
    }

    public void init(CObject LS, CObject base, CObject environment, Scanner scnr) {
        Token t;

        this.environment = environment;
        this.LS = LS;
        this.scnr = scnr;
        parseRuleStack = new Stack<RuleNode>();
        computationStack = new Stack<CObject>();
        tokenStack = new Stack<Token>();

        if (scnr!=null){
            computationStack.push(base);
            parseRuleStack.push(base.getRuleNode());

            t = scnr.nextToken();
            tokenStack.push(t);
            scnr.pushBack(t);
        }

    }

    private static boolean isNewLine(Token t) {
        if (t instanceof SymbolToken) {
            return ((SymbolToken)t).symbol == SymbolTable.getInstance().getId("\n");
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

    public boolean interpretAux() {
        Token t = scnr.nextToken();
        RuleNode currentRule = parseRuleStack.peek();
        Token t2 = t;

        try {
            RuleNode ret = (RuleNode)t.accept(new MatchVisitor(currentRule));
            if (ret!=null) {
                parseRuleStack.pop();
                parseRuleStack.push(ret);
                return true;
            }
            if (currentRule.getRuleForToken() !=null && !isNewLine(t)) {
                parseRuleStack.pop();
                parseRuleStack.push(currentRule.getRuleForToken());
                if (t instanceof CompoundToken) {
                    t = new CompoundToken((CompoundToken)t,LS);
                }
                computationStack.push(t);
                return true;
            }

            if (currentRule.getRuleForNonTerminal() !=null && !isNewLine(t)) {
                RuleNode rn;
                if ((rn = contextLookAhead(LS,EnvironmentObject.instance,t,false))!=null) {
                    parseRuleStack.pop();
                    parseRuleStack.push(currentRule.getRuleForNonTerminal());
                    computationStack.push(LS);
                    parseRuleStack.push(rn);
                    tokenStack.push(t);
                    scnr.pushBack(t);
                    return true;
                }
            }

            if (currentRule.getRuleForAction() != null) {
                scnr.pushBack(t);
                parseRuleStack.pop();
                tokenStack.pop();
                currentRule.getRuleForAction().apply(computationStack);

                // now greedily apply the computation stack top object to the rest fo the stream if possible
                CObject nt = computationStack.peek();
                t = scnr.nextToken();
                RuleNode reduce;
                Token tmp;
                if (parseRuleStack.isEmpty()) {
                    reduce = null;
                    tmp = null;
                } else {
                    reduce = parseRuleStack.peek();
                    tmp = tokenStack.peek();
                }

                RuleNode rn;
                if ((rn=shift(reduce,nt,tmp,t))!=null) {
                    parseRuleStack.push(rn);
                    tokenStack.push(t);

                }
                scnr.pushBack(t);
                return true;
            }
            if (isNewLine(t)) {
                return true;
            }
        } catch (ParseException e) {
            throw new ParseException(t2,this,e);
        } catch (Exception e) {
            throw new ParseException(t2,this,e);
        }
        System.out.println(currentRule);
        throw new ParseException(t2,this);
    }

    private static boolean isProgressPossible(RuleNode rn, Token t) {
        return rn !=null
                && (t.accept(new MatchVisitor(rn))!=null
                || rn.getRuleForNonTerminal()!=null
                || rn.getRuleForToken()!=null
                || rn.getRuleForAction()!=null);
    }

    private RuleNode shift(RuleNode reduce, CObject shift, Token reduceOperator, Token shiftOperator) {
        boolean first = isProgressPossible(reduce,shiftOperator);
        RuleNode ret;
        boolean second = (ret = contextLookAhead(shift,null,shiftOperator,true))!=null;
        if (!second) return null;
        if (!first) return ret;
        if (OperatorPrecedence.getInstance().isShift(reduceOperator,shiftOperator))
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
            if (t.accept(new MatchVisitor(ret))!=null) {
                return ret;
            }
            current = current.getParent(isProto);
        }
        if (extra != null ) {
            ret = extra.getRuleNode();
            if (t.accept(new MatchVisitor(ret))!=null) {
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
