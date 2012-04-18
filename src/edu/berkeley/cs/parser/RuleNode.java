package edu.berkeley.cs.parser;

import edu.berkeley.cs.builtin.functions.Invokable;
import edu.berkeley.cs.builtin.objects.*;
import edu.berkeley.cs.builtin.objects.preprocessor.Token;
import edu.berkeley.cs.lexer.*;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.LinkedList;
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
public class RuleNode {
    private TIntObjectHashMap<RuleNode> nextSymbolMap;
    private TIntObjectHashMap<RuleNode> nextArgumentMap;
    private Action action;
    private RuleNode nonTerminal;
    private RuleNode newLine;

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
        if (nextSymbolMap!=null) {
            for (TIntObjectIterator<RuleNode> iterator = nextSymbolMap.iterator(); iterator.hasNext(); ) {
                iterator.advance();
                RuleNode next = iterator.value();
                LinkedList<String> tmp = next.print();
                for(String child:tmp) {
                    ret.add(SymbolTable.getInstance().getSymbol(iterator.key())+" "+child);
                }
            }
        }
        if (nextArgumentMap!=null) {
            for (TIntObjectIterator<RuleNode> iterator = nextArgumentMap.iterator(); iterator.hasNext(); ) {
                iterator.advance();
                RuleNode next = iterator.value();
                LinkedList<String> tmp = next.print();
                for(String child:tmp) {
                    ret.add("@"+SymbolTable.getInstance().getSymbol(iterator.key())+" "+child);
                }
            }
        }
        if (nonTerminal!=null) {
            LinkedList<String> tmp = nonTerminal.print();
            for(String child:tmp) {
                ret.add("@argument "+child);
            }
        }
        if (action!=null) {
            ret.add(action.toString());
        }
        return ret;

    }


    public RuleNode addMeta(RuleNode rootRules,int argument) {
        return addMeta(rootRules,argument,false);
    }

    public RuleNode addMeta(RuleNode rootRules, int argument,boolean override) {
        if (this==rootRules && argument==SymbolTable.getInstance().argument && !override) {
            throw new ParseException("First token of a def cannot be @argument.");
        }
//        argCount++;
        RuleNode ret;
        if (argument!=SymbolTable.getInstance().argument) {
            if (nextArgumentMap == null)
                nextArgumentMap = new TIntObjectHashMap<RuleNode>();
            ret = nextArgumentMap.get(argument);
            if (ret==null) {
                nextArgumentMap.put(argument,ret=new RuleNode(this,"@"+SymbolTable.getInstance().getSymbol(argument)));
            }
            return ret;
        } else {
            if (nonTerminal == null) {
                nonTerminal = new RuleNode(this, "@argument");
            }
            return  this.nonTerminal;
        }
    }

    public RuleNode addSymbol(int symbol) {
        RuleNode ret;
        if (nextSymbolMap == null) {
            nextSymbolMap = new TIntObjectHashMap<RuleNode>();
        }
        ret = nextSymbolMap.get(symbol);
        if (ret==null) {
            nextSymbolMap.put(symbol,ret=new RuleNode(this,SymbolTable.getInstance().getSymbol(symbol)));
        }
        return ret;
    }

    public RuleNode addNewLine() {
        newLine = new RuleNode(this,"\\n");
        return newLine;
    }

    public RuleNode addAction(Invokable func, int argCount) {
        action = new Action(argCount,func);
        return null;
    }

    public RuleNode getRuleForSymbol(int symbol) {
        RuleNode ret = null;
        if (nextSymbolMap != null && (ret = nextSymbolMap.get(symbol))!=null){
            return ret;
        }
        return null;
    }

    public RuleNode getRuleForArgument(int argument) {
        RuleNode ret = null;
        if (nextArgumentMap != null && (ret = nextArgumentMap.get(argument))!=null){
            return ret;
        }
        return null;
    }

    public RuleNode getNewLine() {
        return newLine;
    }

    public Action getRuleForAction() {
        return action;
    }

    public RuleNode getRuleForNonTerminal() {
        return nonTerminal;
    }
}
