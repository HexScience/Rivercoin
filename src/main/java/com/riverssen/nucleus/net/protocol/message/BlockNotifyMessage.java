package com.riverssen.nucleus.net.protocol.message;

import com.riverssen.nucleus.net.ServerManager;
import com.riverssen.nucleus.net.protocol.Message;
import com.riverssen.nucleus.net.protocol.NotificationMessage;
import com.riverssen.nucleus.protocols.protobufs.Block;
import com.riverssen.nucleus.system.Context;

import java.io.IOException;

/**
 * This class should be used when notifying peers of newfound blocks and or block solutions.
 */
public class BlockNotifyMessage extends NotificationMessage
{
    public BlockNotifyMessage(Block block) throws IOException
    {
        super(BLOCK, block.getBytes());
    }

    public BlockNotifyMessage(byte[] block)
    {
        super(BLOCK, block);
    }

    /**
     * @return success as this is a notification message.
     * @param context
     * @param manager
     */
    @Override
    public Message getAnswerMessage(Context context, ServerManager manager)
    {
        return new SuccessMessage(getCheckSum());
    }

    @Override
    public String toString()
    {
        return null;
    }
}