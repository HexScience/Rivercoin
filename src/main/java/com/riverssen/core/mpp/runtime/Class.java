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

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class Class extends Container implements Serializable
{
    private String          name;
    private Set<Class>      parents;

    public Class(Token token) throws CompileException
    {
        this.name       = token.getTokens().get(0).toString();
        this.parents    = new LinkedHashSet<>();

        for(Token tok : token.getTokens().get(1).getTokens())
            if(tok.getType().equals(Token.Type.EMPTY_DECLARATION) || tok.getType().equals(Token.Type.FULL_DECLARATION))
                addDeclaration(tok);
            else if(tok.getType().equals(Token.Type.METHOD_DECLARATION))
                addMethod(tok);
    }

    protected Class(String name)
    {
        this.name = name;
        this.parents = new LinkedHashSet<>();
    }

    public Object newInstance(Object...args) throws RuntimeException
    {
        return new Object(this, args);
    }

    @Override
    public String toString()
    {
        String string = "---" + name + "---\n";

        return string;
    }

    public Object newInstanceBlank()
    {
        return new Object(this);
    }
}