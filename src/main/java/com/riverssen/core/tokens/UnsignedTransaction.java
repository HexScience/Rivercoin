package com.riverssen.core.tokens;

import com.riverssen.core.RiverCoin;
import com.riverssen.core.security.CompressedAddress;
import com.riverssen.core.security.PrivKey;
import com.riverssen.core.security.PublicAddress;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class UnsignedTransaction extends Token
{
//    private String amount;

    public UnsignedTransaction(CompressedAddress sender, PublicAddress receiver, RiverCoin rvcAmt, long timeStamp, String comment)
    {
        setType(TYPE_TRANSACTION);
        setSenderAddress(sender.toString());
        setReceiverAddress(receiver.toString());
        setAmount(rvcAmt);
        setTimeStamp(timeStamp);
        setComment(comment);
        setReward(new RiverCoin("0"));
    }

    public UnsignedTransaction(DataInputStream stream) throws Exception
    {
        setType(TYPE_TRANSACTION);
        setSenderAddress(stream.readUTF());
        setReceiverAddress(stream.readUTF());
        setAmount(RiverCoin.fromStream(stream));
        setReward(RiverCoin.fromStream(stream));
        setTimeStamp(stream.readLong());
        setComment(stream.readUTF());
    }

    @Override
    public synchronized void write(DataOutputStream stream) throws Exception
    {
        super.write(stream);
        stream.writeShort(getType());
        stream.writeUTF(getSenderAddress());
        stream.writeUTF(getReceiverAddress());
        stream.write(getAmount().getBytes());
        stream.write(getReward().getBytes());
        stream.writeLong(getTimeStamp());
        stream.writeUTF(getComment());
    }

    @Override
    public boolean isValid()
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

    public SignedTransaction toSignedTransaction(PrivKey key)
    {
        //TODO: add signature
        return new SignedTransaction(getSenderAddress(), getReceiverAddress(), getAmount(), getTimeStamp(), getComment(), key.sign(toJSON()));
    }
}
