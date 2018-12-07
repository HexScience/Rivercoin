package nucleus.net.protocol.message;

import nucleus.net.ServerManager;
import nucleus.net.protocol.Message;
import nucleus.system.Context;

public class DataNotFoundMessage extends ResponseMessage
{
    public DataNotFoundMessage(byte code, byte[] message)
    {
        super(NO_FIND, message);
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
