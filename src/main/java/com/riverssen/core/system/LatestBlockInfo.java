package com.riverssen.core.system;

import com.riverssen.core.Config;
import com.riverssen.core.FullBlock;
import com.riverssen.core.chain.BlockHeader;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;

public class LatestBlockInfo
{
    private static boolean available = true;
    private long        lastBlockCheck;
    private long        lastBlockCheckTimestamp;
    private long        lastBlock;
    private BigDecimal  difficulty;
    private BigInteger  totalHashes;

    public LatestBlockInfo()
    {
    }

    public synchronized void read() throws Exception
    {
        while(!available)
        {
        }

        File file = new File(Config.getConfig().BLOCKCHAIN_DIRECTORY + File.separator + "latestblock");

        if(!file.exists())
        {
            lastBlock = -1;
            lastBlockCheck = 0;
            lastBlockCheckTimestamp = System.currentTimeMillis();
            difficulty = new BigDecimal("225269536353234632640832032722171634457188848844000484574312395358531977087");
            totalHashes = BigInteger.ONE;

            return;
        }

        available = false;

        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));

        lastBlock               = dataInputStream.readLong();
        lastBlockCheck          = dataInputStream.readLong();
        lastBlockCheckTimestamp = dataInputStream.readLong();

        byte difficultyByte[]   = new byte[dataInputStream.readInt()];
        dataInputStream.read(difficultyByte);

        difficulty              = new BigDecimal(new BigInteger(difficultyByte));
        byte totalHashesArrray[] = new byte[dataInputStream.readInt()];

        dataInputStream.read(totalHashesArrray);

        totalHashes             = new BigInteger(totalHashesArrray);

        dataInputStream.close();

        available = true;
    }

    public synchronized void write(long lastBlock, long lastBlockCheck, long lastBlockCheckTimestamp, BigDecimal difficulty, BigInteger totalHashes) throws Exception
    {
        while(!available)
        {
        }

        available = false;

        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(new File(Config.getConfig().BLOCKCHAIN_DIRECTORY + File.separator + "latestblock")));

        dataOutputStream.writeLong(lastBlock);
        dataOutputStream.writeLong(lastBlockCheck);
        dataOutputStream.writeLong(lastBlockCheckTimestamp);
        dataOutputStream.writeInt(difficulty.toBigInteger().toByteArray().length);
        dataOutputStream.write(difficulty.toBigInteger().toByteArray());
        dataOutputStream.writeInt(totalHashes.toByteArray().length);
        dataOutputStream.write(totalHashes.toByteArray());

        dataOutputStream.flush();
        dataOutputStream.close();

        available = true;
    }

    public synchronized long getLatestBlock()
    {
        return lastBlock;
    }

    public synchronized long getLastBlockCheck()
    {
        return lastBlockCheck;
    }

    public synchronized long getLastBlockCheckTimestamp()
    {
        return lastBlockCheckTimestamp;
    }

    public synchronized BigDecimal getDifficulty()
    {
        return difficulty;
    }

    public static boolean exists()
    {
        return new File(Config.getConfig().BLOCKCHAIN_DIRECTORY + File.separator + "latestblock").exists();
    }

    public BigInteger getTotalHashes()
    {
        return totalHashes;
    }

    public BlockHeader getLatestBlockHeader()
    {
        if(lastBlock >= 0)
            return new BlockHeader(lastBlock);
        return null;
    }

    public FullBlock getLatestFullBlock()
    {
        if(lastBlock >= 0)
            return BlockHeader.FullBlock(lastBlock);
        return null;
    }
}