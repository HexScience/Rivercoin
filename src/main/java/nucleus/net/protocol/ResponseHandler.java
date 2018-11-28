package nucleus.net.protocol;

import java.nio.ByteBuffer;

public abstract class ResponseHandler
{
    private ByteBuffer buffer;

    public ResponseHandler(byte[] data)
    {
    }

    public static ResponseHandler construct(byte[] data)
    {
        return null;
    }
}