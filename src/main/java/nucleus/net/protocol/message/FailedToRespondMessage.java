package nucleus.net.protocol.message;

import nucleus.net.ServerManager;
import nucleus.net.protocol.Message;
import nucleus.system.Context;

public class FailedToRespondMessage extends Message
{
    public FailedToRespondMessage(byte code, byte[] message)
    {
        super(FAILED, code, message);
    }

    @Override
    public Message getAnswerMessage(Context context, ServerManager manager)
    {
        return null;
    }

    @Override
    public String toString()
    {
        return null;
    }
}
