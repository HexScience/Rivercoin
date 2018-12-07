package com.riverssen.nucleus.net.protocol.message;

import com.riverssen.nucleus.net.ServerManager;
import com.riverssen.nucleus.net.protocol.Message;
import com.riverssen.nucleus.system.Context;

public class FailedToRespondMessage extends Message
{
    public FailedToRespondMessage(byte code, byte[] message)
    {
        super(FAILED, code, message);
    }

    @Override
    public Message getAnswerMessage(Context context, ServerManager manager)
    {
        return null;
    }

    @Override
    public String toString()
    {
        return null;
    }
}
