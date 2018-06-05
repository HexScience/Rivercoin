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
    public static enum Type {
        ROOT,
        STATIC_ACCESS,
        PROCEDURAL_ACCESS,
        INITIALIZATION,
        EMPTY_DECLARATION,
        FULL_DECLARATION,
        METHOD_CALL,
        METHOD_DECLARATION,
        METHOD_EMPTY_DECLARATION,
        CLASS_DECLARATION,
        PARENTHESIS,
        BRACES,
        INPUT,
        BRACKETS
    }
    private Type                type;
    private List<Token>         children;
    private List<LexicalToken>  tokens;

    public Token(Type type)
    {
        this.children = new ArrayList<>();
        this.tokens   = new ArrayList<>();
        this.type     = type;
    }

    public Token add(LexicalToken token)
    {
        this.tokens.add(token);
        return this;
    }

    public Token add(Token token)
    {
        this.children.add(token);
        return this;
    }

    public Token clone()
    {
        Token token = new Token(type);
        for(LexicalToken lexicalToken : tokens)
            token.add(lexicalToken.clone());
        for(Token child : children)
            token.add(child.clone());

        return token;
    }

    public List<Token> getTokens()
    {
        return this.children;
    }

    private String whitespace(int length)
    {
        length*=4;
        String string = "";

        for(int i = 0; i < length; i ++)
            string += ' ';

        return string;
    }

    public String humanReadable(int i)
    {
        String s = (whitespace(i) + type) + "\n";
        for(LexicalToken token : tokens)
            s += (whitespace(i) + token) + "\n";
        for(Token token : children)
            s += token.humanReadable(i + 1) + "\n";

        return s;
    }

    @Override
    public String toString()
    {
        return humanReadable(0);
    }
}
