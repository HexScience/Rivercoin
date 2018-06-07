package com.riverssen.core.compiler;

public class ParseException extends Exception
{
    public ParseException(String msg, Token token)
    {
        super(msg + " at line: " + token.getLine() + " offset: " + token.getOffset());
    }
}
