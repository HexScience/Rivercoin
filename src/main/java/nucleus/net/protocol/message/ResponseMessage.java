package nucleus.net.protocol.message;


import nucleus.net.protocol.Message;

/**
 * This class handles message responses,
 * Any messages that IS a response to a
 * request must use this class as its
 * template.
 */
public abstract class ResponseMessage extends Message
{
    public ResponseMessage(byte code, byte[] message)
    {
        super(REPLY, code, message);
    }
}
