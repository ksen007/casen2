package edu.berkeley.cs.builtin.functions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

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
public class NativeFunction {// implements Invokable {
//    private String methodName;
//
//    public NativeFunction(String methodName) {
//        this.methodName = methodName;
//    }
//
    public static String getStackTrace(Throwable aThrowable) {
        if (aThrowable !=null ){
            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            aThrowable.printStackTrace(printWriter);
            return result.toString();
        }
        return null;
    }
//
//    public CObject apply(LinkedList<CObject> args, CObject DS) {
//        CObject self = args.removeFirst();
//        Class[] types = new Class[args.size()];
//        for(int i=0; i<types.length;i++) {
//            types[i] = CObject.class;
//
//        }
//        Method method;
//        try {
//            method = self.getClass().getMethod(methodName, types);
//            return (CObject)method.invoke(self,args.toArray());
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException("No such native method "+self+" "+methodName+" because "+getStackTrace(e.getCause()));
//        } catch (InvocationTargetException e) {
//            throw new RuntimeException("Exception in native method "+self+"."+methodName+"("+args+") because "+getStackTrace(e.getCause()));
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException("Illegal access in native method "+self+" "+methodName+" because "+getStackTrace(e));
//        }
//    }
//
//        @Override
//        public String toString() {
//            return "@"+methodName;
//        }
    }
