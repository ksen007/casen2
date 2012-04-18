package edu.berkeley.cs.parser;

import edu.berkeley.cs.builtin.objects.CObject;
import edu.berkeley.cs.builtin.objects.CStatementEater;
import edu.berkeley.cs.builtin.objects.EnvironmentObject;
import edu.berkeley.cs.builtin.objects.NewLineToken;
import edu.berkeley.cs.builtin.objects.preprocessor.Token;
import edu.berkeley.cs.lexer.Scanner;
import org.junit.Rule;

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

    public CallFrame(CObject LS, Scanner scnr) {
        init(LS,CStatementEater.instance,scnr);
    }

    public CallFrame(CObject LS, CObject base, Scanner scnr) {
        init(LS,base,scnr);
    }

    public void init(CObject LS, CObject base, Scanner scnr) {
        Token t;

        this.LS = LS;
        this.scnr = scnr;
        parseRuleStack = new Stack<RuleNode>();
        computationStack = new Stack<CObject>();
        tokenStack = new Stack<Token>();

        computationStack.push(base);
        parseRuleStack.push(base.getRuleNode());

        t = scnr.nextToken();
        tokenStack.push(t);
        scnr.pushBack(t);

        if (RuleNode.DEBUG) System.out.println("L:");
    }

    public CObject interpret() {
        if (RuleNode.DEBUG) System.out.println("Interpretation start.");
        while(!parseRuleStack.isEmpty()
                && !(parseRuleStack.size()==1 && scnr.isEnd() && computationStack.get(0) instanceof CStatementEater)) {
            interpretAux();
        }
        return computationStack.peek();
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

    public boolean interpretAux() {
        Token t = scnr.nextToken();
        RuleNode currentRule = parseRuleStack.peek();
        Token t2 = t;

        try {
            if (t!=null) {
                Triple ret = (Triple)t.accept(new MatchVisitor(currentRule,null));
                if (ret.next!=null) {
                    if (!ret.skip) computationStack.push(t);
                    parseRuleStack.pop();
                    parseRuleStack.push(ret.next);
                    if (RuleNode.DEBUG) System.out.println("M:"+t);
                    return true;
                }
            }

            if (currentRule.getRuleForNonTerminal() !=null) {
                RuleNode rn = null;
                if ((rn = lookAhead(LS.getRuleNode(),t, LS.getSuperRuleNode()))!=null) {
                    parseRuleStack.pop();
                    parseRuleStack.push(currentRule.getRuleForNonTerminal());

                    computationStack.push(LS);
                    parseRuleStack.push(rn);
                    tokenStack.push(t);

                    scnr.pushBack(t);
                    if (RuleNode.DEBUG) System.out.println("L:");
                    return true;
                }
            }

            if (currentRule.getRuleForAction() != null) {
                scnr.pushBack(t);
                parseRuleStack.pop();
                tokenStack.pop();
                if (RuleNode.DEBUG) System.out.print("A:" + currentRule.getRuleForAction());
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
                    if (RuleNode.DEBUG) System.out.println("S:");

                }
                scnr.pushBack(t);
                return true;
            }
            if (t instanceof NewLineToken) {
                return true;
            }
        } catch (ParseException e) {
            throw new ParseException(t2,this,e);
        } catch (Exception e) {
            throw new ParseException(t2,this,e);
        }
        throw new ParseException(t2,this);
    }

    private RuleNode shift(RuleNode reduce, CObject shiftO, Token reduceOperator, Token shiftOperator) {
        boolean first = reduce!=null && lookAhead(reduce,shiftOperator,null)!=null;
        RuleNode rn = null;
        boolean second = shiftO.getRuleNode()!=null && (rn = lookAhead(shiftO.getRuleNode(),shiftOperator,shiftO.getSuperRuleNode()))!=null;
        if (!second) return null;
        if (!first) return rn;
        if (OperatorPrecedence.getInstance().isShift(reduceOperator,shiftOperator))
            return rn;
        else
            return null;
    }


    private RuleNode lookAhead(RuleNode current, Token t, RuleNode extra) {
        if (t!=null) {
            Triple tmp = (Triple)t.accept(new MatchVisitor(current,extra));
            if (tmp.next != null) return tmp.current;
        }
        if (current.getRuleForNonTerminal() !=null) {
            //RuleNode ret;
            if (lookAhead(LS.getRuleNode(),t,LS.getSuperRuleNode())!=null) {
                return current;
            }
        }
        if(current.getRuleForAction() != null) return current;
        return null;
    }

}
