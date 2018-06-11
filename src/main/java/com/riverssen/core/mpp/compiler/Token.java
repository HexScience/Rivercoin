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

import java.io.Serializable;
import java.util.*;

public class Token implements Serializable
{
    private StringBuilder       value;
    private int                 line;
    private int                 offset;
    private int                 whitespace;
    private Type                type;
    private List<Token>         children;
    private Set<Modifier>       modifiers = new LinkedHashSet<>();

    public boolean isMathOp()
    {
        return getType() == Type.MATH_OP;
    }

    public Collection<Modifier> getModifiers()
    {
        return modifiers;
    }

    public List<Token> getChildByType(Type type)
    {
        List<Token> tokens = new ArrayList<>();

        for(Token token : children) if(token.getType().equals(type)) ((ArrayList<Token>) tokens).add(token);

        return tokens;
    }

    public void setModifiers(Modifier modifier)
    {
        this.modifiers.add(modifier);
    }

    public boolean isModifier(Modifier modifier)
    {
        return modifiers.contains(modifier);
    }

    public static enum          Type implements Serializable {
        NAMESPACE,
        KEYWORD,
        IDENTIFIER,
        SYMBOL,
        STRING,
        NUMBER,
        PARENTHESIS_OPEN,
        PARENTHESIS_CLOSED,
        BRACES_OPEN,
        BRACES_CLOSED,
        BRACKETS_OPEN,
        BRACKETS_CLOSED,
        MATH_OP,
        EQUALS,
        END,
        ROOT,
        STATIC_ACCESS,
        PROCEDURAL_ACCESS,
        POINTER_ACCESS,
        INITIALIZATION,
        EMPTY_DECLARATION,
        FULL_DECLARATION,
        METHOD_CALL,
        METHOD_DECLARATION,
        METHOD_EMPTY_DECLARATION,
        CLASS_DECLARATION,
        EMPTY_CLASS_DECLARATION,
        PARENTHESIS,
        BRACES,
        INPUT,
        ADDITION,
        SUBTRACTION,
        MULTIPLICATION,
        SUBDIVISION,
        POW,
        MOD,
        BRACKETS,
        VALUE,
        NEW,
        EXTEND,
        IF,
        FOR,
        FOREACH,
        BOOL_OP,
        LESS_THAN,
        MORE_THAN,
        LESSTHAN_EQUAL,
        MORETHAN_EQUAL,
        ASSERT,
        AND,
        OR,
        RIGHT_SHIFT,
        LEFT_SHIFT,
        UNARY,
        PREUNARY,
        PLUSPLUS,
        MINUSMINUS,
        LIST,
        ARRAY,
        WHILE
    };

    public Token(String value, int line, int offset, int whitespace)
    {
        this.value = new StringBuilder(value);
        this.line = line;
        this.offset = offset;
        this.whitespace = whitespace;
        this.children   = new ArrayList<>();
    }

    public Token(char value, int line, int offset, int whitespace)
    {
        this.value = new StringBuilder(value);
        this.line = line;
        this.offset = offset;
        this.whitespace = whitespace;
        this.children   = new ArrayList<>();
    }

    public Token(Type type)
    {
        this.type       = type;
        this.value      = new StringBuilder(type.toString());
        this.children   = new ArrayList<>();
    }

    private Token()
    {
    }

    public Token append(char chr)
    {
        value.append(chr);
        return this;
    }

    public Token append(String chr)
    {
        value.append(chr);
        return this;
    }

    public Token add(Token token)
    {
        this.children.add(token);
        return this;
    }

    public Token clone()
    {
        Token token = new Token();
        token.type  = type;
        token.line  = line;
        token.offset= offset;
        token.value = new StringBuilder(value);

        for (Token child : children)
            token.add(child.clone());
        return token;
    }

    public int getLine()
    {
        return line;
    }

    public int getOffset()
    {
        return offset;
    }

    public int getWhitespace()
    {
        return whitespace;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public Type getType()
    {
        if (type == null)
        {
            final char separators[] = { '.', '=', '+', '-', '\'', '"', ',', '<', '>', '?', ';', ':', '!', '\\', '/', '[', ']', '{', '}', '(', ')', '*', '&', '^', '%', '$', '#', '@', '~' };

            boolean separator = false;

            if (toString().length() == 1)
            {
                if (toString().charAt(0) == ')')
                {
                    type = Type.PARENTHESIS_CLOSED;
                    return type;
                } else if (toString().charAt(0) == '(')
                {
                    type = Type.PARENTHESIS_OPEN;
                    return type;
                } else if (toString().charAt(0) == '}')
                {
                    type = Type.BRACES_CLOSED;
                    return type;
                } else if (toString().charAt(0) == '{')
                {
                    type = Type.BRACES_OPEN;
                    return type;
                } else if (toString().charAt(0) == ']')
                {
                    type = Type.BRACKETS_CLOSED;
                    return type;
                } else if (toString().charAt(0) == '[')
                {
                    type = Type.BRACKETS_OPEN;
                    return type;
                } else if (toString().charAt(0) == ';')
                {
                    type = Type.END;
                    return type;
                } else if (toString().charAt(0) == '=')
                {
                    type = Type.EQUALS;
                    return type;
                } else if (toString().charAt(0) == '+' || toString().charAt(0) == '-' || toString().charAt(0) == '*'
                        || toString().charAt(0) == '/' || toString().charAt(0) == '%')
                {
                    type = Type.MATH_OP;
                    return type;
                } else if (toString().charAt(0) == '&')
                {
                    type = Type.AND;
                    return type;
                } else if (toString().charAt(0) == '|')
                {
                    type = Type.OR;
                    return type;
                } else if (toString().charAt(0) == '<')
                {
                    type = Type.LESS_THAN;
                    return type;
                } else if (toString().charAt(0) == '>')
                {
                    type = Type.MORE_THAN;
                    return type;
                }

                for (char s : separators) if (toString().charAt(0) == s) separator = true;
            }

            if (separator) {
                if(toString().charAt(0) == '.') {
                    type = Type.PROCEDURAL_ACCESS;
                    return type;
                }
                type = Type.SYMBOL;
            }
            else
            {
                if (toString().startsWith("\"") || toString().startsWith("'")) type = Type.STRING;
                else if (toString().equals("extends"))
                    type = Type.EXTEND;
                else if (toString().matches("([_]*[A-z]+\\d*)+")) {
                    if(isKeyword()) type = Type.KEYWORD;
                    else
                    type = Type.IDENTIFIER;
                }
                else if (toString().matches("(\\d[_]*\\d*)+")) type = Type.NUMBER;
            }
        }
        return this.type;
    }

    private boolean isKeyword()
    {
        final String keywords[] = {"function", "fun", "new", "class", "static", "public", "private", "protected", "const", "final", "extend", "header", "if", "for", "while", "foreach", "then", "namespace"};
        for(String string : keywords) if(toString().equals(string)) return true;
        return false;
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
        String s = (whitespace(i) + type + " " + value) + "\n";
        for(Token token : children)
            s += token.humanReadable(i + 1) + "\n";
        return s;
    }

    public boolean isInteger()
    {
        return toString().contains(".");
    }

    @Override
    public String toString()
    {
        return value.toString();//humanReadable(0);
    }
}