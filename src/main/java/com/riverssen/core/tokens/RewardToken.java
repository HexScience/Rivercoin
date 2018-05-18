package com.riverssen.core.tokens;

import com.riverssen.core.Config;
import com.riverssen.core.RVCCore;
import com.riverssen.core.RiverCoin;
import com.riverssen.core.chain.Block;
import com.riverssen.core.security.PubKey;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.utils.HashUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RewardToken extends SignedTransaction
{
    public RewardToken(PublicAddress address, RiverCoin rewardAmt)
    {
        this(address.toString(), rewardAmt, 500);
    }

    public RewardToken(DataInputStream stream) throws Exception
    {
        setType(TYPE_REWARD);

        setReceiverAddress(readCompressedPublicAddress(stream));
        byte bytes[] = new byte[RiverCoin.MAX_BYTES];

        stream.read(bytes);
        setAmount(new RiverCoin(bytes));
    }

    @Override
    public synchronized long size()
    {
        return 2 + 100 + RiverCoin.MAX_BYTES;
    }

    @Deprecated
    public RewardToken(String address, RiverCoin rewardAmt, long timeStamp)
    {
//        setType(TYPE_MINED_BLOCK);
//        setReceiverAddress(address);
//        setTimeStamp(timeStamp);
//        setAmount(rewardAmt);
//        setComment("");

        super(RVCCore.PaddingWallet.getPublicKey().getPublicAddress().toString(), address, rewardAmt, timeStamp, "", "");
    }

    @Override
    public boolean verifyToken(Block block)
    {
        return false;
    }

    @Override
    public synchronized void write(DataOutputStream stream) throws Exception
    {
        super.write(stream);
        stream.writeShort(getType());

        writeAddress(getReceiverAddress(), stream);
        stream.write(getAmount().getBytes());
    }

    //    @Override
//    public byte[] getBytes()
//    {
//        ByteBuffer bytes = ByteBuffer.allocate(PubKey.SIZE_IN_BYTES + 2 + RiverCoin.MAX_BYTES);
//
//        bytes.putShort(getType());
//        bytes.put(HashUtil.publicKeyExport(getReceiverAddress()).getBytes());
//        bytes.put(getAmount().getBytes());
//
//        bytes.flip();
//
//        return bytes.array();
//    }

    public static void readFromStream(Token self, DataInputStream stream) throws IOException
    {
        byte bytes[] = new byte[PubKey.SIZE_IN_BYTES];

        stream.read(bytes);
        self.setReceiverAddress(HashUtil.publicKeyImport(new String(bytes)));
    }

    @Override
    public boolean isValid()
    {
        return getAmount().toRiverCoinString().equals(Config.getReward());
    }
}