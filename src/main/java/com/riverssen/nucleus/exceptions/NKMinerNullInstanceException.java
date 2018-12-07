package com.riverssen.nucleus.exceptions;

public class NKMinerNullInstanceException extends NKMinerException
{
    public NKMinerNullInstanceException()
    {
        super("no NKMiner instance present, please init first.");
    }
}
