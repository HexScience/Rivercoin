package com.riverssen.nucleus.net.protocol.message;

import com.riverssen.nucleus.net.ServerManager;
import com.riverssen.nucleus.net.protocol.Message;
import com.riverssen.nucleus.system.Context;

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