package com.riverssen.core.tokens;

import com.riverssen.core.RiverCoin;
import com.riverssen.core.chain.Block;
import com.riverssen.core.headers.UTXO;
import com.riverssen.core.security.PubKey;
import com.riverssen.utils.Encodeable;
import com.riverssen.utils.HashUtil;
import com.riverssen.utils.Hashable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;

public class Token implements Comparable<Token>, Hashable, Encodeable
{
    private static short incremental = Short.MIN_VALUE;
    public static final short TYPE_TRANSACTION = Short.MIN_VALUE + 0;
    public static final short TYPE_REWARD = Short.MIN_VALUE + 1;
    public static final short TYPE_MESSGE = Short.MIN_VALUE + 2;
//    public static final short TYPE_MINED_BLOCK = incremental ++;

    private short type;
    private String senderAddress;
    private String receiverAddress;
    private String comment;
    private RiverCoin amount;
    private RiverCoin reward;
    private long timeStamp;

    public Token()
    {
    }

//    public TransactionI(DataInputStream tokenStream)
//    {
//    }

    public byte[] compress()
    {
        return null;
    }

    public synchronized void write(DataOutputStream stream) throws Exception
    {
    }

    public synchronized long size()
    {
        return 64 + 20 + 512 + 13 + 13 + 8 + 256;
    }

    public synchronized void writeAddress(String address, DataOutputStream stream) throws Exception
    {
        byte array[] = PubKey.getBytes(address);

        stream.write(array.length);
        stream.write(array);
    }

    public synchronized String readCompressedPublicAddress(DataInputStream stream) throws Exception
    {
        byte array[] = new byte[Byte.toUnsignedInt(stream.readByte())];

        stream.read(array);

        return new PubKey(array).getPublicAddress().toString();
    }

    public static Token read(DataInputStream stream, String version) throws Exception
    {
        int type = stream.readShort();

        switch (type)
        {
            case TYPE_TRANSACTION:
                return new SignedTransaction(stream);
            case TYPE_REWARD:
                return new RewardToken(stream);
        }

        return null;
    }

    public boolean verifyToken(Block block)
    {
        return false;
    }

    public byte[] getHash()
    {
        return getHash(0);
    }

    public byte[] getHash(long nonce)
    {
        return HashUtil.applySha256(toJSON(nonce).getBytes());
    }

    public byte[] getHash(long nonce, String hash)
    {
        return HashUtil.applyRipeMD160(toJSON(nonce, hash).getBytes());
    }

    public byte[] getHash(String hash)
    {
        return HashUtil.applySha256(toJSON(hash).getBytes());
    }

    public String getHashAsString()
    {
        return getHashAsString(0);
    }

    public String getHashAsString(long nonce)
    {
        return HashUtil.hashToStringBase16(getHash(nonce));
    }

    public String getHashAsString(long nonce, String hash)
    {
        return HashUtil.hashToStringBase16(getHash(nonce, hash));
    }

    public String getHashAsString(String previousHash)
    {
        return HashUtil.hashToStringBase16(getHash(previousHash));
    }

    private String JSONLine(String k, String v)
    {
        return "\t\"" + k + "\":\"" + v + "\"\n";
    }

    public String toJSON(long nonce)
    {
        return "{\n" +
                JSONLine("type", getType() + "") + ", " +
                JSONLine("time", getTimeStamp() + "") + ", " +
                JSONLine("sender", getSenderAddress() + "") + ", " +
                JSONLine("receiver", getReceiverAddress() + "") + ", " +
                JSONLine("amount", getAmount().toRiverCoinString() + "") + ", " +
                JSONLine("reward", getReward().toRiverCoinString() + "") + ", " +
                JSONLine("nonce", nonce + "") + ", " +
                JSONLine("comment", getComment() + "") + "\n}";
    }

    public String toJSON(long nonce, String hash)
    {
        return "{\n" +
                JSONLine("type", getType() + "") + ", " +
                JSONLine("time", getTimeStamp() + "") + ", " +
                JSONLine("sender", getSenderAddress() + "") + ", " +
                JSONLine("receiver", getReceiverAddress() + "") + ", " +
                JSONLine("amount", getAmount().toRiverCoinString() + "") + ", " +
                JSONLine("reward", getReward().toRiverCoinString() + "") + ", " +
                JSONLine("nonce", nonce + "") + ", " +
                JSONLine("hash", hash + "") + ", " +
                JSONLine("comment", getComment() + "") + "\n}";
    }

    public String toJSON(String hash)
    {
        return "{\n" +
                JSONLine("type", getType() + "") + ", " +
                JSONLine("time", getTimeStamp() + "") + ", " +
                JSONLine("sender", getSenderAddress() + "") + ", " +
                JSONLine("receiver", getReceiverAddress() + "") + ", " +
                JSONLine("amount", getAmount().toRiverCoinString() + "") + ", " +
                JSONLine("reward", getReward().toRiverCoinString() + "") + ", " +
                JSONLine("hash", hash + "") + ", " +
                JSONLine("comment", getComment() + "") + "\n}";
    }

    public String toJSON()
    {
        return "{\n" +
                JSONLine("type", getType() + "") + ", " +
                JSONLine("time", getTimeStamp() + "") + ", " +
                JSONLine("sender", getSenderAddress() + "") + ", " +
                JSONLine("receiver", getReceiverAddress() + "") + ", " +
                JSONLine("amount", getAmount().toRiverCoinString() + "") + ", " +
                JSONLine("reward", getReward().toRiverCoinString() + "") + ", " +
                JSONLine("comment", getComment() + "") + "\n}";
    }

    public String toJSONWithHash()
    {
        return "{\n" +
                JSONLine("type", getType() + "") + ", " +
                JSONLine("time", getTimeStamp() + "") + ", " +
                JSONLine("sender", getSenderAddress() + "") + ", " +
                JSONLine("receiver", getReceiverAddress() + "") + ", " +
                JSONLine("amount", getAmount().toRiverCoinString() + "") + ", " +
                JSONLine("reward", getReward().toRiverCoinString() + "") + ", " +
                JSONLine("hash", getHashAsString() + "") + ", " +
                JSONLine("comment", getComment() + "") + "\n}";
    }

    @Override
    public String toString()
    {
        return toJSON();
    }

    public ByteBuffer asByteBuffer()
    {
        return null;
    }

    public short getType()
    {
        return type;
    }

    public void setType(short type)
    {
        this.type = type;
    }

    public String getSenderAddress()
    {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress)
    {
        this.senderAddress = senderAddress;
    }

    public String getReceiverAddress()
    {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress)
    {
        this.receiverAddress = receiverAddress;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public long getTimeStamp()
    {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public RiverCoin getAmount()
    {
        return amount;
    }

    public void setAmount(RiverCoin amount)
    {
        this.amount = amount;
    }

    public void setReward(RiverCoin amount)
    {
        this.reward = amount;
    }

    public boolean isTransaction()
    {
        return getType() == TYPE_TRANSACTION;
    }

    @Override
    public int compareTo(Token o)
    {
        return getTimeStamp() >= o.getTimeStamp() ? 1 : -1;
    }

    @Override
    public int hashCode()
    {
        return getHashAsString().hashCode();
    }

    public String toMsg()
    {
        return toJSON();
    }

    public RiverCoin getReward()
    {
        return reward;
    }

    @Override
    public String keccak()
    {
        return HashUtil.hashToStringBase16(HashUtil.applyKeccak(toJSON().getBytes()));
    }

    @Override
    public String gost3411()
    {
        return HashUtil.hashToStringBase16(HashUtil.applyGost3411(toJSON().getBytes()));
    }

    @Override
    public String sha1()
    {
        return HashUtil.hashToStringBase16(HashUtil.applySha1(toJSON().getBytes()));
    }

    @Override
    public String sha3()
    {
        return HashUtil.hashToStringBase16(HashUtil.applySha3(toJSON().getBytes()));
    }

    @Override
    public String sha256()
    {
        return HashUtil.hashToStringBase16(HashUtil.applySha256(toJSON().getBytes()));
    }

    @Override
    public String sha512()
    {
        return HashUtil.hashToStringBase16(HashUtil.applySha512(toJSON().getBytes()));
    }

    @Override
    public String blake2b()
    {
        return HashUtil.hashToStringBase16(HashUtil.applyBlake2b(toJSON().getBytes()));
    }

    @Override
    public String ripemd128()
    {
        return HashUtil.hashToStringBase16(HashUtil.applyRipeMD128(toJSON().getBytes()));
    }

    @Override
    public String ripemd160()
    {
        return HashUtil.hashToStringBase16(HashUtil.applyRipeMD160(toJSON().getBytes()));
    }

    @Override
    public String ripemd256()
    {
        return HashUtil.hashToStringBase16(HashUtil.applyRipeMD256(toJSON().getBytes()));
    }

    @Override
    public String fs_sha()
    {
        return HashUtil.hashToStringBase16(HashUtil.applySha3(HashUtil.applySha256(HashUtil.applySha512(HashUtil.applySha1((toJSON().getBytes()))))));
    }

    @Override
    public String fs_ripeMD()
    {
        return HashUtil.hashToStringBase16(HashUtil.applyRipeMD256(HashUtil.applyRipeMD160(HashUtil.applyRipeMD128(toJSON().getBytes()))));
    }

    @Override
    public String X11()
    {
        byte    hash[]  = HashUtil.applyRipeMD128(toJSON().getBytes());
                hash    = HashUtil.applyRipeMD160(hash);
                hash    = HashUtil.applyRipeMD256(hash);
                hash    = HashUtil.applyGost3411(hash);
                hash    = HashUtil.applyBlake2b(hash);
                hash    = HashUtil.applyKeccak(hash);
                hash    = HashUtil.applySha512(hash);
                hash    = HashUtil.applySha1(hash);
                hash    = HashUtil.applySha3(hash);
                hash    = HashUtil.applySha256(hash);
        return HashUtil.hashToStringBase16(hash);
    }

    public UTXO getUTXO()
    {
        return null;
    }

    public boolean isValid()
    {
        return false;
    }

    public byte[] getBytes()
    {
        return toJSON().getBytes();
    }

//    public byte[] getBytes()
//    {
//        return new byte[1];
//    }
//
//    public void fromBytes(byte token[])
//    {
//    }
//
//    public void fromBytes(DataInputStream stream)
//    {
//    }
}