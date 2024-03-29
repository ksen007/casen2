package edu.berkeley.cs.lexer;

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
public class SourcePosition {
    private int lineNo;
    private int columnNo;
    private String id;
    private boolean isFile;

    public SourcePosition(String id,int lineNumber, int columnNumber,boolean isFile) {
        this.lineNo = lineNumber;
        this.columnNo = columnNumber;
        this.id = id;
        this.isFile = isFile;
    }

    public int getLineNumber() {
        return lineNo;
    }

    public int getColumnNumber() {
        return columnNo;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (!(object instanceof SourcePosition))
            return false;
        SourcePosition pos = (SourcePosition) object;
        return this.lineNo == pos.lineNo && this.columnNo == pos.columnNo && this.isFile==pos.isFile && this.id.equals(pos.id);
    }

    @Override
    public int hashCode() {
        return lineNo+columnNo+id.hashCode()+(isFile?1:0);
    }

    @Override
    public String toString() {
        return "(source="+ id+",line=" + lineNo + ",column=" + columnNo+")";
    }

    public String getFilename() {
        if (isFile) return id;
        else return null;
    }
}
 