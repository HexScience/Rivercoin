package com.riverssen.nucleus.protocols.transaction;

import com.riverssen.nucleus.exceptions.FileServiceException;
import com.riverssen.nucleus.system.Serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * This class will be used as a handler for transaction output storage
 * in the db, it is smaller in size (15 bytes instead of 40) therefore
 * it makes sense to use this class in the DB as a pointer to the actu
 * -al transaction output.
 */
public class DBTransactionOutput
{
    /**
     * The block that generated this transaction output.
     */
    private long    blockID;
    /**
     * The transaction index of the block.
     */
    private int     transactionID;
    /**
     * The output index in the transaction.
     */
    private int     outputID;

    public DBTransactionOutput()
    {
    }

    public DBTransactionOutput(long blockID, int transactionID, int outputID)
    {
        this.blockID = blockID;
        this.transactionID = transactionID;
        this.outputID = outputID;
    }

    public void write(DataOutputStream stream) throws IOException
    {
        stream.writeLong(blockID);
        ByteBuffer int_24 = ByteBuffer.allocate(4);
        int_24.putInt(transactionID);
        int_24.flip();
        int_24.get();

        byte int_24bit[] = new byte[3];
        int_24.get(int_24bit);

        stream.write(int_24bit);
        stream.writeInt(outputID);
    }

    public void read(DataInputStream stream) throws IOException
    {
        blockID = stream.readLong();
        byte int_24bit[] = new byte[3];

        ByteBuffer int_24 = ByteBuffer.allocate(4);
        stream.read(int_24bit);
        int_24.put((byte) 0);
        int_24.put(int_24bit);

        int_24.flip();
        transactionID = int_24.getInt();

        outputID = stream.readInt();
    }

    public void read(ByteBuffer stream)
    {
        blockID = stream.getLong();
        byte int_24bit[] = new byte[3];

        ByteBuffer int_24 = ByteBuffer.allocate(4);
        stream.get(int_24bit);
        int_24.put((byte) 0);
        int_24.put(int_24bit);

        int_24.flip();
        transactionID = int_24.getInt();

        outputID = stream.getInt();
    }

    public long getBlockID()
    {
        return blockID;
    }

    public int getTransactionID()
    {
        return transactionID;
    }

    public int getOutputID()
    {
        return outputID;
    }

    public TransactionOutput getOutput(Serializer serializer) throws IOException, FileServiceException
    {
        return serializer.loadTransactionOutput(blockID, transactionID, outputID);
    }

    @Override
    public String toString()
    {
        return getBlockID() + "." + getTransactionID() + "." + getOutputID();
    }
}