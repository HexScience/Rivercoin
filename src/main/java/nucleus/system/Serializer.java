package nucleus.system;

import nucleus.protocol.protobufs.Block;
import nucleus.protocol.protobufs.BlockHeader;
import nucleus.protocol.transaction.Transaction;

public class Serializer
{
    public Block loadBlock(long block)
    {
        return null;
    }

    public BlockHeader loadHeader(long block)
    {
        return null;
    }

    public Transaction loadTransaction(long block, short transactionID)
    {
        return loadBlock(block).getTransactions().get(transactionID);
    }
}
