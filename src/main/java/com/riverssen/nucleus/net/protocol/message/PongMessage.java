package com.riverssen.nucleus.net.protocol.message;

import com.riverssen.nucleus.net.ServerManager;
import com.riverssen.nucleus.net.protocol.Message;
import com.riverssen.nucleus.net.protocol.NotificationMessage;
import com.riverssen.nucleus.system.Context;
import com.riverssen.nucleus.util.ByteUtil;

/**
 * This class should be used when notifying peers of the servers presence.
 */
public class PongMessage extends NotificationMessage
{
    public PongMessage(long chainHeight)
    {
        super(PING, ByteUtil.encode(chainHeight));
    }

    /**
     * @return null as this is a notification message.
     * @param context
     * @param manager
     */
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