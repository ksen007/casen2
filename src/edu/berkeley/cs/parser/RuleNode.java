package edu.berkeley.cs.parser;

import edu.berkeley.cs.builtin.objects.CObject;

import java.util.HashMap;
import java.util.LinkedList;

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

class OtherPair {
    CObject fst;
    RuleNode next;

    OtherPair(CObject fst, RuleNode next) {
        this.fst = fst;
        this.next = next;
    }
}

public class RuleNode {
    private HashMap<CObject,RuleNode> next;
    private Action action;
    private RuleNode expr;
    private OtherPair other;
    private RuleNode token;

    boolean isNoSpace = false;

    private int optionalPrecedence = 0;

    public static boolean DEBUG = false;

    private RuleNode parent;
    private String prefix;


    public RuleNode(RuleNode parent) {
        this.parent = parent;
    }

    public RuleNode(RuleNode parent, String prefix) {
        this.parent = parent;
        this.prefix = prefix;
    }


    @Override
    public String toString() {
        String prefix = " @. ";
        RuleNode tmp2 = this;
        while(tmp2!=null) {
            if (tmp2.prefix != null)
                prefix = " "+tmp2.prefix + prefix;
            tmp2 = tmp2.parent;
        }

        StringBuilder sb = new StringBuilder();
        LinkedList<String> tmp = print();
        for(String child:tmp) {
            sb.append(prefix);
            sb.append(child);
        }
        return sb.toString();
    }

    private LinkedList<String> print() {
        LinkedList<String> ret = new LinkedList<String>();
        if (next!=null) {
            for (CObject cObject : next.keySet()) {
                RuleNode rn = next.get(cObject);
                LinkedList<String> tmp = rn.print();
                for (String child : tmp) {
                    ret.add(cObject + " " + child);
                }
            }
        }
        if (expr !=null) {
            LinkedList<String> tmp = expr.print();
            for(String child:tmp) {
                ret.add("@expr "+child);
            }
        }
        if (token!=null) {
            LinkedList<String> tmp = token.print();
            for(String child:tmp) {
                ret.add("@token "+child);
            }
        }
        if (action!=null) {
            ret.add(action.toString());
        }
        return ret;

    }


    public RuleNode addMeta(RuleNode rootRules,int metaSymbol) {
        return addMeta(rootRules,metaSymbol,false);
    }

    public RuleNode addMeta(RuleNode rootRules, int metaSymbol,boolean override) {
        if (this==rootRules && metaSymbol==SymbolTable.getInstance().expr && !override) {
            throw new ParseException("First token of a def cannot be @expr.");
        }
        if (metaSymbol==SymbolTable.getInstance().token) {
            if (expr != null || action !=null || other!=null) {
                throw new ParseException("Cannot add @token when @expr or other or action exists");
            }
            if (token == null) {
                token = new RuleNode(this, "@token");
            }
            return  this.token;
        } else {
            if (token != null || action !=null || other!=null) {
                throw new ParseException("Cannot add @expr when @token or other or action exists");
            }
            if (expr == null) {
                expr = new RuleNode(this, "@expr");
            }
            return expr;
        }
    }

    public RuleNode addOther(CObject other) {
        if (expr != null || token !=null || other!=null || action!=null ) {
            throw new ParseException("Cannot add other when @expr or @token or other or action exists");
        }
        this.other = new OtherPair(other,new RuleNode(this,other.toString()));
        return this.other.next;
    }

    public RuleNode addObject(CObject val) {
        RuleNode ret;
        if (next == null) {
            next = new HashMap<CObject, RuleNode>();
        }
        ret = next.get(val);
        if (ret==null) {
            next.put(val,ret=new RuleNode(this,val.toString()));
        }
        ret.isNoSpace = val.isNoSpace();
        return ret;
    }

    public RuleNode addPrecedence(int prec) {
        optionalPrecedence = prec;
        return this;
    }

    public RuleNode addAction(Action a) {
        if (expr != null || token !=null || other!=null) {
            throw new ParseException("Cannot add action when @expr or @token or other exists");
        }
        action = a;
        return null;
    }

    public RuleNode getRuleForObject(CObject object) {
        RuleNode ret = null;
        if (next != null && (ret = next.get(object))!=null){
            if (ret.isNoSpace && !object.isNoSpace()) {
                System.out.println("Boom: "+this);
                System.out.println(object);
                return null;
            }
            return ret;
        }
        return null;
    }

    public Action getRuleForAction() {
        return action;
    }

    public RuleNode getRuleForExpr() {
        return expr;
    }

    public RuleNode getRuleForToken() {
        return token;
    }

    public int getOptionalPrecedence() {
        return optionalPrecedence;
    }

    public boolean isActionOnly() {
        return (next==null && expr ==null && token == null && action != null);
    }

    public OtherPair getRuleForOther() {
        return other;
    }
}
