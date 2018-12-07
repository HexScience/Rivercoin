package com.riverssen.nucleus.exceptions;

public class ECLibException extends Exception
{
    public ECLibException()
    {
        super("ECLibException");
    }

    public ECLibException(String exception)
    {
        super(exception);
    }
}
