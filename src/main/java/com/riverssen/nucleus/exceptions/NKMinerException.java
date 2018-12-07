package com.riverssen.nucleus.exceptions;

public class NKMinerException extends Exception
{
    public NKMinerException(String ex)
    {
        super("NKMiner: " + ex);
    }
}
