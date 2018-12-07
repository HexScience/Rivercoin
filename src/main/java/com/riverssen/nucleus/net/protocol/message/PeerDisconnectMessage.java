package com.riverssen.nucleus.net.protocol.message;

import com.riverssen.nucleus.net.ServerManager;
import com.riverssen.nucleus.net.protocol.Message;
import com.riverssen.nucleus.net.protocol.NotificationMessage;
import com.riverssen.nucleus.system.Context;

public class PeerDisconnectMessage extends NotificationMessage
{
    public PeerDisconnectMessage(byte code, byte[] message)
    {
        super(DISCONNECT, message);
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
