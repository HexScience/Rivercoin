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

package com.riverssen.core.mpp.compiler;

import com.riverssen.core.mpp.exceptions.CompileException;

import java.util.HashMap;
import java.util.Map;

public class Container
{
    protected static final Container VOID = new Container() {
        @Override
        public String toString()
        {
            return "void";
        }
    };
    protected String                name;
    private Map<String, Container>  globalMap;

    public Container()
    {
        this.globalMap = new HashMap<>();
    }

    protected void addNameSpace(Token token) throws CompileException
    {
        Namespace ns = new Namespace(token);
        if(this.globalMap.containsKey(ns.getName())) throw new CompileException("namespace '" + ns.getName() + "' already defined.", token);
        this.globalMap.put(ns.getName(), ns);
    }

    protected void addClass(Token token) throws CompileException
    {
        Class ns = new Class(token);
        if(this.globalMap.containsKey(ns.getName())) throw new CompileException("class '" + ns.getName() + "' already defined.", token);
        this.globalMap.put(ns.getName(), ns);
    }

    protected void addMethod(Token token) throws CompileException
    {
        Method ns = new Method(token);
        if(this.globalMap.containsKey(ns.getName())) throw new CompileException("method '" + ns.getName() + "' already defined.", token);
        this.globalMap.put(ns.getName(), ns);
    }

    protected void addDeclaration(Token token) throws CompileException
    {
        Field ns = new Field(token);
        if(this.globalMap.containsKey(ns.getName())) throw new CompileException("field '" + ns.getName() + "' already defined.", token);
        this.globalMap.put(ns.getName(), ns);
    }

    public void setField(String name, Container value)
    {
        this.globalMap.put(name, value);
    }

    public String getName()
    {
        return name;
    }

    public Map<String, Container> getGlobalMap()
    {
        return globalMap;
    }

    public Container get(String name)
    {
        return globalMap.get(name);
    }

    public Container callMethod(String method, Container ...args)
    {
        return get(method).call(this, args);
    }

    public Container call(Container self, Container...args)
    {
        return VOID;
    }

    public java.lang.Object asJavaObject()
    {
        return this;
    }

    public Container addition(Container b)
    {
        return new Container();
    }

    public Container submission(Container b)
    {
        return new Container();
    }

    public Container multiplication(Container b)
    {
        return new Container();
    }

    public Container subdivision(Container b)
    {
        return new Container();
    }

    public Container modulo(Container b)
    {
        return new Container();
    }

    public Container sin(Container b)
    {
        return new Container();
    }

    public Container cos(Container b)
    {
        return new Container();
    }

    public Container power(Container b)
    {
        return new Container();
    }

    @Override
    public boolean equals(java.lang.Object obj)
    {
        return super.equals(obj);
    }
}
