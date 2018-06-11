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

import com.riverssen.core.mpp.compiler.OpcodeWriter;
import com.riverssen.core.mpp.compiler.Token;
import com.riverssen.core.rvm.Opcode;
import com.riverssen.core.rvm.opcodes.FunctionOpcode;

import java.util.LinkedHashSet;
import java.util.Set;

public class Method
{
    private String      name;
    private Set<Field>  arguments;
    private String      returnType;
    private Opcode      opcode[];
    private Opcode      method;
    private int         offset;

    public Method(Token token)
    {
        this.name       = token.getTokens().get(0).toString();
        this.arguments  = new LinkedHashSet<>();
        for(Token tok : token.getTokens().get(1).getTokens())
            this.arguments.add(new Field(tok));

        this.method     = new FunctionOpcode(token.getTokens().get(2));
    }

    public Method

    public Method(String name, Opcode opcode[])
    {
        this.name   = name;
        this.opcode = opcode;
    }

    protected   void    setOffset(int offset)
    {
        this.offset = offset;
    }

    public      int     getOffset()
    {
        return this.offset;
    }

    public String getName()
    {
        return name;
    }

    public String getReturnType()
    {
        return returnType;
    }

    protected void call(Object self, Object ...args) throws RuntimeException
    {
        /** pass this as parameter **/
    }
}
