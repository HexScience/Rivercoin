package com.riverssen.nucleus.exceptions;

public class EncryptionPasswordInvalidException extends Throwable
{
    public EncryptionPasswordInvalidException(String password)
    {
        super("encryption password '" + password + "' is invalid.");
    }
}