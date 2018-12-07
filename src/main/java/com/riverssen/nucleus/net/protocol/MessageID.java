package com.riverssen.nucleus.net.protocol;

import com.riverssen.nucleus.exceptions.MessageIDException;

public class MessageID
{
    private static final int messageid_size = 20;
    private final byte[] id;

    public MessageID(byte[] id) throws MessageIDException
    {
        if (id.length < messageid_size || id.length > messageid_size)
            throw new MessageIDException("message id is 20 bytes long, provided '" + id.length + "'");

        this.id = id;
    }

    public byte[] get()
    {
        return id;
    }
}
