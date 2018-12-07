package com.riverssen.nucleus.net.protocol.message;

import com.riverssen.nucleus.net.ServerManager;
import com.riverssen.nucleus.net.protocol.Message;
import com.riverssen.nucleus.net.protocol.NotificationMessage;
import com.riverssen.nucleus.system.Context;

/**
 * This class should be used when notifying peers of message delivery.
 */
public class SuccessMessage extends NotificationMessage
{
    public SuccessMessage(byte checksum[])
    {
        super(MSG_SUCCESS, checksum);
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