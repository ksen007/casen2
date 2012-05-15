package edu.berkeley.cs.builtin.objects.preprocessor;

import edu.berkeley.cs.builtin.objects.CObject;
import edu.berkeley.cs.builtin.objects.ProtoDoubleToken;
import edu.berkeley.cs.lexer.SourcePosition;

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
public class DoubleToken extends CObject {
    public double value;

    public DoubleToken(SourcePosition position, boolean isSpace, double l) {
        super(position);
        this.value = l;
        if (!isSpace) setNoSpace();
        setParent(ProtoDoubleToken.INSTANCE);
    }

    public DoubleToken(SourcePosition position, double l) {
        super(position);
        this.value = l;
        setParent(ProtoDoubleToken.INSTANCE);
    }

    public CObject add(CObject operand2) {
        return new DoubleToken(null,value+((DoubleToken)operand2).value);
    }

    public CObject subtract(CObject operand2) {
        return new DoubleToken(null,value-((DoubleToken)operand2).value);
    }

    public CObject multiply(CObject operand2) {
        return new DoubleToken(null,value*((DoubleToken)operand2).value);
    }

    public CObject divide(CObject operand2) {
        return new DoubleToken(null,value/((DoubleToken)operand2).value);
    }

    public CObject mod(CObject operand2) {
        return new DoubleToken(null,value%((DoubleToken)operand2).value);
    }

    public CObject lt(CObject operand2) {
        return value<((DoubleToken)operand2).value?BooleanToken.TRUE():BooleanToken.FALSE();
    }

    public CObject gt(CObject operand2) {
        return value>((DoubleToken)operand2).value?BooleanToken.TRUE():BooleanToken.FALSE();
    }

    public CObject le(CObject operand2) {
        return value<=((DoubleToken)operand2).value?BooleanToken.TRUE():BooleanToken.FALSE();
    }

    public CObject ge(CObject operand2) {
        return value>=((DoubleToken)operand2).value?BooleanToken.TRUE():BooleanToken.FALSE();
    }

    public CObject eq(CObject operand2) {
        return value==((DoubleToken)operand2).value?BooleanToken.TRUE():BooleanToken.FALSE();
    }

    public CObject ne(CObject operand2) {
        return value!=((DoubleToken)operand2).value?BooleanToken.TRUE(): BooleanToken.FALSE();
    }

    @Override
    public String toString() {
        return value+"";

    }


    @Override
    public int hashCode() {
        return (int)value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DoubleToken)) return false;
        return value == ((DoubleToken)o).value;
    }
}
