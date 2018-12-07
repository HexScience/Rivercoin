package nucleus.net.protocol.message;

import nucleus.net.ServerManager;
import nucleus.net.protocol.Message;
import nucleus.net.protocol.RequestMessage;
import nucleus.protocols.protobufs.Block;
import nucleus.system.Context;
import nucleus.util.ByteUtil;

/**
 * This class should be used when satisfying block requests from peers.
 */
public class BlockRequestSatisfactionMessage extends ResponseMessage
{
    public BlockRequestSatisfactionMessage(final byte[] block)
    {
        super(BLOCK, block);
    }

    /**
     * @return null as this is a request message.
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