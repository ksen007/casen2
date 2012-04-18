package edu.berkeley.cs.builtin.functions;

import edu.berkeley.cs.builtin.objects.CObject;
import edu.berkeley.cs.parser.ParseException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
public class NativeFunction implements Invokable {
    private String methodName;

    public NativeFunction(String methodName) {
        this.methodName = methodName;
    }

    public CObject apply(LinkedList<CObject> args) {
        CObject self = args.removeFirst();
        Class[] types = new Class[args.size()];
        for(int i=0; i<types.length;i++) {
            types[i] = CObject.class;

        }
        Method method;
//        try {
        try {
            method = self.getClass().getMethod(methodName, types);
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return (CObject)method.invoke(self,args.toArray());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e.getCause());
//            throw new RuntimeException(e.getMessage());
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof ParseException)
                throw (ParseException)e.getCause();
            else
                throw new RuntimeException(e.getCause());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getCause());
        }
    }

        @Override
        public String toString() {
            return "@"+methodName;
        }
    }
