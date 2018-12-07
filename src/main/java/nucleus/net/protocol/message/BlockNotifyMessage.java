package nucleus.net.protocol.message;

import nucleus.net.ServerManager;
import nucleus.net.protocol.Message;
import nucleus.net.protocol.NotificationMessage;
import nucleus.protocols.protobufs.Block;
import nucleus.system.Context;

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