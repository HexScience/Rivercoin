package com.riverssen.nucleus.versioncontrol.versions.ir;

import com.riverssen.nucleus.protocols.transaction.TransactionOutput;
import com.riverssen.nucleus.versioncontrol.Constructor;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class irTransactionOutputConstructor implements Constructor<TransactionOutput>
{
    @Override
    public TransactionOutput ConstructFromBytes(ByteBuffer data)
    {
        long value = data.getLong();
        byte spendscript[] = new byte[data.getShort()]; data.get(spendscript);
        return new TransactionOutput(value, spendscript);
    }

    @Override
    public TransactionOutput ConstructFromInput(DataInputStream stream) throws IOException
    {
        long value = stream.readLong();
        byte spendscript[] = new byte[stream.readShort()]; stream.read(spendscript);
        return new TransactionOutput(value, spendscript);
    }

    @Override
    public TransactionOutput ConstructFromOther(TransactionOutput other)
    {
        return null;
    }
}
