package edu.berkeley.cs.lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

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
public class PeekReader {
    private Reader in;
    private CharBuffer peakBuffer;

    public PeekReader(Reader in, int peekLimit) throws IOException {
        if (!in.markSupported()) {
            // Wrap with buffered reader, since it supports marking
            in = new BufferedReader(in);
        }
        this.in = in;
        peakBuffer = CharBuffer.allocate(peekLimit);
        fillPeekBuffer();
    }

    public void close() throws IOException {
        in.close();
    }

    private void fillPeekBuffer() throws IOException {
        peakBuffer.clear();
        in.mark(peakBuffer.capacity());
        in.read(peakBuffer);
        in.reset();
        peakBuffer.flip();
    }

    public int read() throws IOException {
        int c = in.read();
        fillPeekBuffer();
        return c;
    }

    /**
     * Return a character that is further in the stream.
     *
     * @param lookAhead How far to look into the stream.
     * @return Character that is lookAhead characters into the stream.
     */
    public int peek(int lookAhead) {
        if (lookAhead < 1 || lookAhead > peakBuffer.capacity()) {
            throw new IndexOutOfBoundsException("lookAhead must be between 1 and " + peakBuffer.capacity());
        }
        if (lookAhead > peakBuffer.limit()) {
            return -1;
        }
        return peakBuffer.get(lookAhead - 1);
    }
}