package com.riverssen.nucleus.exceptions;

public class NKMinerInstanceAlreadyExistsException extends NKMinerException
{
    public NKMinerInstanceAlreadyExistsException()
    {
        super("instance already exists, please fetch it.");
    }
}