package nucleus.net.p2p;

import nucleus.net.message.Message;

public class UDPMessage extends Message
{
    public UDPMessage(byte code, byte[] message)
    {
        super(code, message);
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
