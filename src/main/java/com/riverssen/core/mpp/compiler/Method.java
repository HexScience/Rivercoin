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

import com.riverssen.core.mpp.objects.Float;
import com.riverssen.core.mpp.objects.Integer;
import com.riverssen.core.mpp.objects.Uint;
import com.riverssen.core.mpp.objects.uint256;
import com.riverssen.core.rvm.Opcode;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

import static com.riverssen.core.mpp.compiler.Opcode.*;

public class Method extends Container
{
    private static final Stack<Container> stack = new Stack<>();
    private Set<Field>  arguments;
    private String      returnType;
    private int         offset;
    private Token       body;
    private ByteBuffer  opcodes;

    public Method(Token token)
    {
        this.name       = token.getTokens().get(0).toString();
        this.arguments  = new LinkedHashSet<>();
        for(Token tok : token.getTokens().get(1).getTokens())
            this.arguments.add(new Field(tok));
        this.returnType = token.getTokens().get(1).toString();

        this.body       = token.getTokens().get(3);
    }

    public Method(String name)
    {
        this.name       = name;
    }

    public Method(String name, Opcode opcode[])
    {
        this.name   = name;
    }

    protected   void    setOffset(int offset)
    {
        this.offset = offset;
    }

    public      int     getOffset()
    {
        return this.offset;
    }

    public String getReturnType()
    {
        return returnType;
    }

    @Override
    public void call(Container self, Container... args)
    {
        stack.clear();
        stack.push(self);
        for(Container arg : args)
            stack.push(arg);

        while (opcodes.remaining() > 0)
        {
            switch (opcodes.getShort())
            {
                case PUSH_INT:
                    stack.push(new Integer(opcodes.getLong()));
                    break;
                case PUSH_UINT:
                    stack.push(new Uint(opcodes.getLong()));
                    break;
                case PUSH_UINT256:
                    byte bi[] = new byte[32];
                    opcodes.get(bi);
                    stack.push(new uint256(new BigInteger(bi)));
                    break;
                case PUSH_FLOAT:
                    stack.push(new Float(opcodes.getDouble()));
                    break;

                case ADD:
                    Container b = stack.pop();
                    Container a = stack.pop();
                    stack.push(a.addition(b));
                    break;
            }
        }
    }

    @Override
    public String toString()
    {
        return "method: " + name + " " + returnType + " {}";
    }
}
