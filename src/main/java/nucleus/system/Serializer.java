package nucleus.system;

import nucleus.protocol.protobufs.Block;
import nucleus.protocol.protobufs.BlockHeader;
import nucleus.protocol.transaction.Transaction;
import nucleus.protocol.transaction.TransactionOutput;
import nucleus.protocol.transaction.TransactionOutputID;

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

    public Transaction loadTransaction(long block, int transactionID)
    {
        return loadBlock(block).getTransactions().get(transactionID);
    }

    public TransactionOutput loadTransactionOutput(TransactionOutputID id)
    {
        return loadTransaction(id.getBlock(), id.getTransaction()).getOutput(id.getOutput());
    }
}
