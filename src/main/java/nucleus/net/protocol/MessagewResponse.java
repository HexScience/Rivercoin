package nucleus.net.protocol;

import nucleus.util.ByteUtil;

/**
 * This class handles messages with responses,
 * Any messages that REQUIRES a response must use
 * this class as it's template.
 */
public abstract class MessagewResponse extends Message
{
    /**
     * @param code Message code
     * @param id Unique Message 160-bit id
     * @param message The message
     */
    public MessagewResponse(byte code, MessageID id, byte[] message)
    {
        super(REPLY, code, ByteUtil.concatenate(id.get(), message));
    }
}
