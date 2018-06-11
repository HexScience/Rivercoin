/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Riverssen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.riverssen.core.mpp.runtime;

import com.riverssen.core.mpp.compiler.Token;
import com.riverssen.core.mpp.exceptions.CompileException;

import java.util.Map;

public class Container
{
    private String                  identifier;
    private Map<String, Namespace>  namespaceMap;
    private Map<String, Class>      classMap;
    private Map<String, Method>     methodMap;
    private Map<String, Field>      fieldMap;

    protected void addNameSpace(Token token) throws CompileException
    {
        Namespace ns = new Namespace(token);
        if(this.namespaceMap.containsKey(ns.getName())) throw new CompileException("namespace '" + ns.getName() + "' already defined.", token);
        this.namespaceMap.put(ns.getName(), ns);
    }

    protected void addClass(Token token) throws CompileException
    {
        Class ns = new Class(token);
        if(this.classMap.containsKey(ns.getName())) throw new CompileException("class '" + ns.getName() + "' already defined.", token);
        this.classMap.put(ns.getName(), ns);
    }

    protected void addMethod(Token token) throws CompileException
    {
        Method ns = new Method(token);
        if(this.methodMap.containsKey(ns.getName())) throw new CompileException("method '" + ns.getName() + "' already defined.", token);
        this.methodMap.put(ns.getName(), ns);
    }

    protected void addDeclaration(Token token) throws CompileException
    {
        Field ns = new Field(token);
        if(this.fieldMap.containsKey(ns.getName())) throw new CompileException("field '" + ns.getName() + "' already defined.", token);
        this.fieldMap.put(ns.getName(), ns);
    }

    public String getName()
    {
        return identifier;
    }
}
