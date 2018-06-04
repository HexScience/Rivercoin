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

package com.riverssen.core.rvm;

import java.util.HashMap;
import java.util.Stack;

public class VirtualMemory
{
    private Stack<MemObject>            stack;
    private HashMap<Long, MemObject>    memPool;
    private MemObject                   NULL;

    public VirtualMemory()
    {
        this.stack      = new Stack<>();
        this.memPool    = new HashMap<>();
        this.NULL = new MemObject()
        {
            @Override
            public <T extends MathContext> T add(T b)
            {
                return null;
            }

            @Override
            public <T extends MathContext> T sub(T b)
            {
                return null;
            }

            @Override
            public <T extends MathContext> T mul(T b)
            {
                return null;
            }

            @Override
            public <T extends MathContext> T div(T b)
            {
                return null;
            }

            @Override
            public <T extends MathContext> T mod(T b)
            {
                return null;
            }

            @Override
            public void call(VirtualMachine virtualMachine)
            {

            }

            @Override
            public int getType()
            {
                return -1;
            }

            @Override
            public long getPointer()
            {
                return 0;
            }

            @Override
            public void fromBytes(byte[] data)
            {

            }

            @Override
            public MemObject get(long address)
            {
                return null;
            }

            @Override
            public void store(VirtualMachine virtualMachine)
            {

            }

            @Override
            public String toString()
            {
                return "NULL";
            }
        };
    }

    public void addObject(MemObject object, long ptr)
    {
        memPool.put(ptr, object);
    }

    public MemObject getObject(long ptr)
    {
        if(!memPool.containsKey(ptr))
            return NULL;
        return memPool.get(ptr);
    }

    public MemObject peek()
    {
        return stack.peek();
    }

    public MemObject pop()
    {
        return stack.pop();
    }

    public void      push(MemObject object)
    {
        stack.push(object);
    }

    public MemObject getFromStack(long pointer)
    {
        return stack.get((int) pointer);
    }
}
