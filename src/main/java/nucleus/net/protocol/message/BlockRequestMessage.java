package nucleus.net.protocol.message;

import nucleus.net.protocol.Message;
import nucleus.net.protocol.RequestMessage;
import nucleus.system.Context;
import nucleus.util.ByteUtil;

/**
 * This class should be used when requesting blocks from peers.
 */
public class BlockRequestMessage extends RequestMessage
{
    public BlockRequestMessage(long block)
    {
        super(BLOCK, ByteUtil.encode(block));
    }

    /**
     * @return null as this is a request message.
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