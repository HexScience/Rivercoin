package nucleus.system;

import nucleus.exceptions.FileServiceException;
import nucleus.protocols.protobufs.Block;
import nucleus.protocols.protobufs.BlockHeader;
import nucleus.protocols.transaction.Transaction;
import nucleus.protocols.transaction.TransactionOutput;
import nucleus.util.FileService;

import java.io.IOException;
import java.util.ArrayList;

public class Serializer
{
    private Context     context;
    private FileService entryPoint;

    public Serializer(Context context, FileService entryPoint)
    {
        this.context    = context;
        this.entryPoint = entryPoint;
    }

    public Block loadBlock(long block) throws IOException, FileServiceException
    {
        return new Block(entryPoint.newFile("height_" + block + ".blk"));
    }

    public BlockHeader loadHeader(long block)
    {
        return null;
    }

    public Transaction loadTransaction(long block, int transactionID) throws IOException, FileServiceException
    {
        return new ArrayList<Transaction>(loadBlock(block).getAcceptedTransactions()).get(transactionID);
    }

    public TransactionOutput loadTransactionOutput(long block, int transaction, int id) throws IOException, FileServiceException
    {
        return loadTransaction(block, transaction).getOutput(id);
    }
}
