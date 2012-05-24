package edu.berkeley.cs.builtin.objects.preprocessor;

import edu.berkeley.cs.builtin.objects.CObject;
import edu.berkeley.cs.builtin.objects.ProtoBooleanToken;
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
public class BooleanToken extends CObject {
    public static BooleanToken TRUE() {
        if (TRUE==null)
            TRUE = new BooleanToken(null,true,true);
        return TRUE;
    }

    public static BooleanToken FALSE() {
        if (FALSE==null)
            FALSE = new BooleanToken(null,true,false);
        return FALSE;
    }

    private static BooleanToken TRUE;
    private static BooleanToken FALSE;


    public boolean value;

    public BooleanToken(SourcePosition position, boolean isSpace, boolean value) {
        super(position);
        this.value = value;
        if (!isSpace) setNoSpace();
        setParent(ProtoBooleanToken.INSTANCE);
    }

    @Override
    public String toString() {
        return value+"";

    }

    @Override
    public int hashCode() {
        return value?1:0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BooleanToken)) return false;
        return value == ((BooleanToken)o).value;
    }

}
