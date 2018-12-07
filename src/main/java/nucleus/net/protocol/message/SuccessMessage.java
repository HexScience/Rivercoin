package nucleus.net.protocol.message;

import nucleus.net.ServerManager;
import nucleus.net.protocol.Message;
import nucleus.net.protocol.NotificationMessage;
import nucleus.protocols.protobufs.Block;
import nucleus.system.Context;

import java.io.IOException;

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