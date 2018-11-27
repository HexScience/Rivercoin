package nucleus.net.protocol;

public abstract class NotificationMessage extends Message
{
    public NotificationMessage(byte code, byte[] message)
    {
        super(NOTFY, code, message);
    }
}
