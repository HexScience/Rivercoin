package com.riverssen.nucleus.net.protocol.message;

import com.riverssen.nucleus.net.ServerManager;
import com.riverssen.nucleus.net.protocol.Message;
import com.riverssen.nucleus.system.Context;

public class BlockDoesNotExistMessage extends DataNotFoundMessage
{
    public BlockDoesNotExistMessage(byte[] message)
    {
        super(BLOCK, message);
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
