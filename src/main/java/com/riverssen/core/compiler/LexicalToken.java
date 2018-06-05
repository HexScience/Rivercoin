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

public class LexicalToken
{
    private StringBuilder       value;
    private int                 line;
    private int                 offset;
    private int                 whitespace;
    private Type                type;
    public static enum          Type {KEYWORD, IDENTIFIER, SYMBOL, STRING, NUMBER, PARENTHESIS_OPEN, PARENTHESIS_CLOSED, BRACES_OPEN, BRACES_CLOSED, BRACKETS_OPEN, BRACKETS_CLOSED, EQUALS, END};

    public LexicalToken(String value, int line, int offset, int whitespace)
    {
        this.value = new StringBuilder(value);
        this.line = line;
        this.offset = offset;
        this.whitespace = whitespace;
    }

    public LexicalToken(char value, int line, int offset, int whitespace)
    {
        this.value = new StringBuilder(value);
        this.line = line;
        this.offset = offset;
        this.whitespace = whitespace;
    }

    public LexicalToken append(char chr)
    {
        value.append(chr);
        return this;
    }

    public LexicalToken clone()
    {
        return new LexicalToken(value.toString(), line, offset, whitespace);
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
        if(type == null)
        {
            final char separators[] = {'.','=','+','-','\'','"',',','<','>','?',';',':','!','\\','/',
                    '[',']','{','}','(',')','*','&','^','%','$','#','@'};

            boolean separator = false;

            if(toString().length() == 1)
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
                }

                for (char s : separators) if (toString().charAt(0) == s) separator = true;
            }

            if(separator) type = Type.SYMBOL;
            else {
                if(toString().startsWith("\"") || toString().startsWith("'")) type = Type.STRING;
                else if(toString().matches("([_]*[A-z]+\\d*)+")) type = Type.IDENTIFIER;
                else if(toString().matches("(\\d[_]*\\d*)+")) type = Type.NUMBER;
            }
        }
        return this.type;
    }

    @Override
    public String toString()
    {
        return value.toString();
    }
}