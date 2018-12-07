package nucleus.net.protocol.message;

import nucleus.net.ServerManager;
import nucleus.net.protocol.Message;
import nucleus.system.Context;

public class BlockDoesNotExistMessage extends DataNotFoundMessage
{
    public BlockDoesNotExistMessage(byte[] message)
    {
        super(BLOCK, message);
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
