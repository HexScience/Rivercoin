package nucleus.system;

import nucleus.protocols.protobufs.Block;
import nucleus.protocols.protobufs.BlockHeader;
import nucleus.protocols.transaction.Transaction;
import nucleus.protocols.transaction.TransactionOutput;
import nucleus.protocols.transaction.TransactionOutputID;

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
