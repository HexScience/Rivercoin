package nucleus.net.protocol;


import nucleus.util.ByteUtil;

/**
 * This class handles message responses,
 * Any messages that IS a response must use
 * this class as it's template.
 */
public abstract class MessageResponse extends Message
{
    public MessageResponse(byte code, MessageID id, byte[] message)
    {
        super(REPLY, code, ByteUtil.concatenate(id.get(), message));
    }
}
