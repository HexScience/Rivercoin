package nucleus.net.message;

import com.riverssen.core.utils.ByteUtil;

public abstract class Message
{
    /**
     * A list of message codes
     */

    public static byte
    PING            = 0,
    PONG            = 1,

    GETBLOCK        = 2,
    GETBLOCKHEADER  = 3,
    GETCHAIN        = 4,
    GETCHAINHEADERS = 5,
    GETCHAINSIZE    = 6,

    BLOCK           = 7,
    BLOCKHEADER     = 8,
    CHAIN           = 9,
    CHAINHEADERS    = 10,
    CHAINSIZE       = 11,
    TRANSACTION     = 12,
    INTRODUCE_SELF  = 13, /** introduction message **/
    RECIPROCATE     = 14; /** reciprocate the introduction **/

    private byte code;
    private byte message[];

    public Message(byte code, byte message[])
    {
        this.code       = code;
        this.message    = message;
    }

    public abstract Class<?> getAnswerMessage();
    public byte getCode()
    {
        return code;
    }

    public byte[] getMessageData()
    {
        return message;
    }

    public byte[] getFullMessage()
    {
        return ByteUtil.concatenate(new byte[] {code}, message);
    }

    public abstract String toString();
}
