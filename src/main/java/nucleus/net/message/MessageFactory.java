package nucleus.net.message;

public interface MessageFactory
{
    Message create(byte code, byte message[]);
}
