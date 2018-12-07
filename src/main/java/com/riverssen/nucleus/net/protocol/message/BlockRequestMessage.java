package com.riverssen.nucleus.net.protocol.message;

import com.riverssen.nucleus.net.ServerManager;
import com.riverssen.nucleus.net.protocol.Message;
import com.riverssen.nucleus.net.protocol.RequestMessage;
import com.riverssen.nucleus.protocols.protobufs.Block;
import com.riverssen.nucleus.system.Context;
import com.riverssen.nucleus.util.ByteUtil;

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
     * @param manager
     */
    @Override
    public Message getAnswerMessage(Context context, ServerManager manager)
    {
        try{
            Block block = context.getBlockChain().getBlockFromMain(ByteUtil.decode(getMessageData()));

            if (block != null)
                return new BlockRequestSatisfactionMessage(block.getBytes());
        } catch (Exception e)
        {
            try{
                Block block = context.getBlockChain().getBlockFromMain(ByteUtil.decode(getMessageData()));

                if (block != null)
                    return new BlockRequestSatisfactionMessage(block.getBytes());
            } catch (Exception ee)
            {
                return new FailedToRespondMessage(BLOCK, getCheckSum());
            }
        }

        return new BlockDoesNotExistMessage(getCheckSum());
    }

    @Override
    public String toString()
    {
        return null;
    }
}