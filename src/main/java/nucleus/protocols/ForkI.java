package nucleus.protocols;

import nucleus.protocols.protobufs.Block;

import java.util.Queue;

public abstract class ForkI
{
    abstract boolean add(Block block);
    abstract boolean add(DownloadedBlock block);

    protected Queue<Block>    blockQueue;

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
}