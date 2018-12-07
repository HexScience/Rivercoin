package com.riverssen.nucleus.net.protocol;


/**
 * This class handles message requests,
 * Any messages that IS a request must use
 * this class as it's template.
 */
public abstract class RequestMessage extends Message
{
    public RequestMessage(byte code, byte[] message)
    {
        super(REQUEST, code, message);
    }
}
