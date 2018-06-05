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

package com.riverssen.core.compiler;

import java.util.ArrayList;
import java.util.List;

public class Token
{
    public static enum Type {ROOT, STATIC_ACCESS, PROCEDURAL_ACCESS, INITIALIZATION, EMPTY_DECLARATION, FULL_DECLARATION, METHOD_CALL, METHOD_DECLARATION, METHOD_EMPTY_DECLARATION, CLASS_DECLARATION}
    private Type type;
    private List<LexicalToken> children;

    public Token(Type type)
    {
        this.children = new ArrayList<>();
        this.type     = type;
    }

    public Token add(LexicalToken token)
    {
        this.children.add(token);
        return this;
    }

    public Token clone()
    {
        Token token = new Token(type);
        for(LexicalToken lexicalToken : children)
            token.add(lexicalToken.clone());

        return token;
    }
}
