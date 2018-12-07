//package com.riverssen.nucleus.net.p2p;
//
//import com.riverssen.nucleus.net.protocols.MessageFactory;
//import com.riverssen.nucleus.protocols.transaction.Transaction;
//import com.riverssen.nucleus.util.ByteUtil;
//
//import java.io.IOException;
//
//import com.riverssen.nucleus.net.protocols.Message;
//
//import static com.riverssen.nucleus.net.protocols.Message.*;
//
//public class UDPMessageFactory implements MessageFactory
//{
//    @Override
//    public Message create(byte code, byte[] message)
//    {
//        return new UDPMessage(code, message);
//    }
//
//    public Message holepunch() { return create(PUNCH, new byte[1]); }
//
//    public Message createPingMessage()
//    {
//        return create(PING, ByteUtil.encode(System.currentTimeMillis()));
//    }
//
//    public Message createPongMessage()
//    {
//        return create(PONG, ByteUtil.encode(System.currentTimeMillis()));
//    }
//
//    public Message createTransactionMessage(Transaction transaction) throws IOException
//    {
//        return create(TRANSACTION, transaction.getBytes());
//    }
//
//    public Message createIntroductionMessage(long clientVersion, long chainInfo, byte latestHash[])
//    {
//        return create(INTRODUCE_SELF, null);
//    }
//
//    public Message createReciprocationMessage(long clientVersion, long chainInfo, boolean hashMismatch)
//    {
//        return create(RECIPROCATE, null);
//    }
//
//    public Message createBlockHeaderRequestMessage(long blockID)
//    {
//        return create(GETBLOCKHEADER, ByteUtil.encode(blockID));
//    }
//
//    public Message createBlockRequestMessage(long blockID)
//    {
//        return create(GETBLOCK, ByteUtil.encode(blockID));
//    }
//
//    public Message createChainHeaderRequestMessage(long startPoint)
//    {
//        return create(GETCHAINHEADERS, ByteUtil.encode(startPoint));
//    }
//
//    public Message createChainRequestMessage(long startPoint)
//    {
//        return create(GETCHAIN, ByteUtil.encode(startPoint));
//    }
//
//    public Message createChainInfoRequestMessage(long myChainInfo)
//    {
//        return create(GETCHAINSIZE, ByteUtil.encode(myChainInfo));
//    }
//}
