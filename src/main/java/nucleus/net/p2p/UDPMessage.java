package nucleus.net.p2p;

import nucleus.net.protocol.Message;

public class UDPMessage extends Message
{
    public UDPMessage(byte code, byte[] message)
    {
        super(REPLY, code, message);
    }

    @Override
    public Class<?> getAnswerMessage()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return null;
    }
}
