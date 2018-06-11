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

import com.riverssen.core.mpp.runtime.StringObject;

import java.io.IOException;

public class Object
{
    private int myAddress = 0;
    private Class type;
    private Object fields[];
    private boolean onStack;

    public Object(Class type, Object ...args) throws RuntimeException
    {
        this.myAddress = 0;
        this.type = type;
        this.fields = new Object[this.type.getFields().size()];
        this.type.getMethoddByName(type.getName()).call(this, args);
    }

    protected Object(Class type)
    {
        this.myAddress = 0;
        this.type = type;
        this.fields = new Object[this.type.getFields().size()];
    }

    protected Object()
    {
    }

    public static Object fromInput(Token value)
    {
        switch (value.getTokens().get(0).getType())
        {
            case STRING:
                return new StringObject(value.getTokens().get(0).toString().substring(0, value.getTokens().get(0).toString().length() - 1));
        }

        return null;
    }

    protected void setField(String name, Object object)
    {
        fields[type.getFieldByName(name)] = object;
    }

    public java.lang.Object asJavaObject()
    {
        return null;
    }

    public Object getFieldByName(String name)
    {
        return fields[type.getFieldByName(name)];
    }

    public Method getMethodByName(String name)
    {
        return null;
    }

    public Object callMethod(String name, Object ...args)
    {
        return type.getMethoddByName(name).call(this, args);
    }

    public void push_to_stack(OpcodeWriter writer) throws IOException
    {
        writer.write(com.riverssen.core.mpp.Opcode.LOAD);
        writer.getStream().writeLong(myAddress);
    }
}
