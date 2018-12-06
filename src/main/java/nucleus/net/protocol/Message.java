package nucleus.net.protocol;

import nucleus.system.Context;
import nucleus.util.ByteUtil;
import nucleus.util.HashUtil;

public abstract class Message
{
    public static final byte
    NOTFY = 0, /** a notification message type **/
    REPLY = 1, /** a reply message type **/
    OPTIN = 2, /** an optional-to-reply message type **/
    REQUEST = 3, /** a request message **/




    NONE = 4;

    /**
     * A list of message codes
     */
    public static final byte
    PUNCH           = -1,
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
    RECIPROCATE     = 14, /** reciprocate the introduction **/
    JSON            = 15;

    private byte type;
    private byte code;
    private byte checksum[];
    private byte message[];

    public Message(byte type, byte code, byte message[])
    {
        this.code       = code;
        this.message    = message;
        this.checksum   = HashUtil.applySha256(message);
    }

    public abstract Message getAnswerMessage(Context context);
    public byte getType()
    {
        return code;
    }
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
        return ByteUtil.concatenate(new byte[] {type, code}, checksum, message);
    }

    public abstract String toString();

    public byte[] getCheckSum()
    {
        return checksum;
    }
}
