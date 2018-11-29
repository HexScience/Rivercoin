package nucleus.system;

import nucleus.protocols.protobufs.Block;
import nucleus.protocols.protobufs.BlockHeader;
import nucleus.protocols.transaction.Transaction;
import nucleus.protocols.transaction.TransactionOutput;

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

    public TransactionOutput loadTransactionOutput(long block, int transaction, int id)
    {
        return loadTransaction(block, transaction).getOutput(id);
    }
}
