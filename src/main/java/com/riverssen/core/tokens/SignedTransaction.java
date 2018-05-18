package com.riverssen.core.tokens;

import com.riverssen.core.RiverCoin;
import com.riverssen.core.chain.Block;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;

public class SignedTransaction extends Token
{
    private String signature;

    public SignedTransaction(String sender, String receiver, RiverCoin rvcAmt, long timeStamp, String comment, String signature)
    {
        setType(TYPE_TRANSACTION);
        setSenderAddress(sender);
        setReceiverAddress(receiver);
        setAmount(rvcAmt);
        setReward(new RiverCoin("0"));
        setTimeStamp(timeStamp);
        setComment(comment);
        setSignature(signature);
    }

    public SignedTransaction(DataInputStream stream) throws Exception
    {
        setType(TYPE_TRANSACTION);
        setSenderAddress(readCompressedPublicAddress(stream));
        setReceiverAddress(readCompressedPublicAddress(stream));
        setAmount(RiverCoin.fromStream(stream));
        setReward(RiverCoin.fromStream(stream));
        setTimeStamp(stream.readLong());
        setComment(stream.readUTF());
        setSignature(stream.readUTF());
    }

    public SignedTransaction()
    {
    }

    @Override
    public void write(DataOutputStream stream) throws Exception
    {
//        ByteBuffer bytes = ByteBuffer.allocate(PubKey.SIZE_IN_BYTES * 2 + 2 + RiverCoin.MAX_BYTES + 4 + getComment().length() + 4 + signature.length());

        stream.writeShort(getType());

        writeAddress(getSenderAddress(), stream);
        writeAddress(getReceiverAddress(), stream);

        stream.write(getAmount().getBytes());
        stream.write(getReward().getBytes());

        stream.writeLong(getTimeStamp());

        stream.writeUTF(getComment());
        stream.writeUTF(getSignature());
    }

    @Override
    public synchronized long size()
    {
        return 2 + 200 + RiverCoin.MAX_BYTES + 8 + getComment().getBytes().length + getSignature().getBytes().length;
    }

    @Override
    public byte[] compress()
    {
        ByteBuffer buffer = ByteBuffer.allocate(2 + 100 + 100 + RiverCoin.MAX_BYTES + 256 + 150);

        return super.compress();
    }

    @Override
    public boolean verifyToken(Block block)
    {
        return false;
    }

//    public String getAmount()
//    {
//        return amount;
//    }
//
//    public void setAmount(String amount)
//    {
//        this.amount = amount;
//    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }
}
