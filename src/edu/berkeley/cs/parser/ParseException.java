package edu.berkeley.cs.parser;

import edu.berkeley.cs.builtin.objects.mutable.CObject;

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
public class ParseException extends RuntimeException {
    private CObject token;
    private CallFrame frame;
    private Exception child;
    private String message;

    public ParseException(CObject token, CallFrame frame, Exception child) {
        this.token = token;
        this.frame = frame;
        this.child = child;
    }

    public ParseException(CObject token, CallFrame frame) {
        this.token = token;
        this.frame = frame;
        this.child = null;
    }

    public ParseException(String s) {
        message = s;
    }

    public String getMessage() {
        return message+":"+token;
    }

    //    public ParseException(Throwable throwable) {
//        otherCause = throwable;
//    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
        if (message != null) {
            sb.append("Cause:"+message);
//        } else if (otherCause !=null) {
//            sb.append(otherCause.toString());
        } else if (frame!=null) {
            if (token==null)
                sb.append("Token: "+token+"\n");
            else
                sb.append("Token: "+token+" at "+token.locationString()+"\n");
            sb.append("Frame:\n"+frame);
            if (child != null) {
                sb.append("Child:\n");
//                child.printStackTrace();
                if (child instanceof ParseException)
                    sb.append(child.toString());
                else {
                    sb.append(child.toString()+"\n");
                    StackTraceElement[] ste = child.getStackTrace();
                    for(StackTraceElement st:ste)
                        sb.append(st+"\n");
                }
            }
        }
        sb.append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
        return sb.toString();
    }
}
