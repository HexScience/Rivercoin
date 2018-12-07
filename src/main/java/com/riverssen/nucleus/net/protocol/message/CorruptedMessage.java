package com.riverssen.nucleus.net.protocol.message;

import com.riverssen.nucleus.net.ServerManager;
import com.riverssen.nucleus.net.protocol.Message;
import com.riverssen.nucleus.system.Context;

public class CorruptedMessage extends Message
{
    public CorruptedMessage(byte[] message)
    {
        super(NOTFY, MSG_CORRUPTED, message);
    }

    @Override
    public Message getAnswerMessage(Context context, ServerManager manager)
    {
        return manager.getQueuedMessage(getMessageData());
    }

    @Override
    public String toString()
    {
        return null;
    }
}
