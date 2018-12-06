package nucleus.net.protocol.message;

import nucleus.net.protocol.Message;
import nucleus.net.protocol.NotificationMessage;
import nucleus.protocols.protobufs.Block;
import nucleus.system.Context;

/**
 * This class should be used when notifying peers of newfound blocks and or block solutions.
 */
public class BlockNotifyMessage extends NotificationMessage
{
    public BlockNotifyMessage(Block block)
    {
        super(BLOCK, block.strip());
    }

    /**
     * @return null as this is a notification message.
     * @param context
     */
    @Override
    public Message getAnswerMessage(Context context)
    {
        return null;
    }

    @Override
    public String toString()
    {
        return null;
    }
}