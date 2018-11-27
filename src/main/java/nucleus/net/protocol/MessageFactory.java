package nucleus.net.protocol;

public interface MessageFactory
{
    Message create(byte code, byte message[]);
}
