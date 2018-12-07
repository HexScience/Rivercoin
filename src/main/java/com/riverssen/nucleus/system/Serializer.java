package com.riverssen.nucleus.system;

import com.riverssen.nucleus.exceptions.FileServiceException;
import com.riverssen.nucleus.protocols.protobufs.Block;
import com.riverssen.nucleus.protocols.protobufs.BlockHeader;
import com.riverssen.nucleus.protocols.transaction.Transaction;
import com.riverssen.nucleus.protocols.transaction.TransactionOutput;
import com.riverssen.nucleus.util.FileService;

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
