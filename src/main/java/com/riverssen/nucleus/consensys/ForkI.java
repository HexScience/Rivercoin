package com.riverssen.nucleus.consensys;

import com.riverssen.nucleus.net.server.IpAddress;
import com.riverssen.nucleus.protocols.protobufs.Block;
import com.riverssen.nucleus.util.SortedLinkedQueue;

import java.util.Queue;

public abstract class ForkI
{
    abstract boolean add(Block block);

    abstract boolean add(DownloadedBlock block);

    protected Queue<Block> blockQueue;

    public long numTransactions()
    {
        long numTransactions = 0;
        for (Block block : blockQueue)
            numTransactions += block.getAcceptedTransactions().size();

        return numTransactions;
    }

    public long averageTime()
    {
        return 0;
    }

    public double transactionRatio()
    {
        long numTransactions = numTransactions();
        long numrTransactions = 0;
        for (Block block : blockQueue)
            numrTransactions += block.getRejectedTransactions().size();

        return (double) (numTransactions / numrTransactions);
    }

    abstract Queue<Block> get();

    Block getAt(long height)
    {
        for (Block block : blockQueue)
            if (block.getHeader().getHeight() == height)
                return block;

        return null;
    }

    public void replace(long height, Block block)
    {
        Queue<Block> temp = new SortedLinkedQueue<>();
        for (Block _block_ : blockQueue)
            if (block.getHeader().getHeight() == height)
                temp.add(block);
            else
                temp.add(_block_);

        blockQueue = temp;
    }

    public abstract boolean hasSender(IpAddress sender);
}