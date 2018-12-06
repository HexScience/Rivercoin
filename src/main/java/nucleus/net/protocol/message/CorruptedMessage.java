package nucleus.net.protocol.message;

import nucleus.net.ServerManager;
import nucleus.net.protocol.Message;
import nucleus.system.Context;

public class CorruptedMessage extends Message
{
    public CorruptedMessage(byte[] message)
    {
        super(MSG_CORRUPTED, MSG_CORRUPTED, message);
    }

    @Override
    public Message getAnswerMessage(Context context, ServerManager manager)
    {
        return manager.getQueuedMessage(getMessageData());
    }

    @Override
    public String toString()
    {
        return null;
    }
}
