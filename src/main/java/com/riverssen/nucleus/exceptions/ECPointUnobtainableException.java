package com.riverssen.nucleus.exceptions;

public class ECPointUnobtainableException extends ECLibException
{
    public ECPointUnobtainableException(String info)
    {
        super("ECPointUnobtainableException: " + info + ".");
    }
}
