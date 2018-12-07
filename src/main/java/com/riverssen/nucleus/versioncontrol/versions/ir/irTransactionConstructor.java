package com.riverssen.nucleus.versioncontrol.versions.ir;

import com.riverssen.nucleus.protocols.transaction.Transaction;
import com.riverssen.nucleus.protocols.transaction.TransactionInput;
import com.riverssen.nucleus.protocols.transaction.TransactionOutput;
import com.riverssen.nucleus.versioncontrol.Constructor;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class irTransactionConstructor implements Constructor<Transaction>
{
    @Override
    public Transaction ConstructFromBytes(ByteBuffer data)
    {
        long flags = data.getLong();
        long maghs = data.getLong();
        long lockt = data.getLong();

        TransactionInput inputs[] = new TransactionInput[data.getInt()];

        for (int i = 0; i < inputs.length; i ++)
            inputs[i] = new irTransactionInputConstructor().ConstructFromBytes(data);

        TransactionOutput outputs[] = new TransactionOutput[data.getInt()];

        for (int i = 0; i < outputs.length; i ++)
            outputs[i] = new irTransactionOutputConstructor().ConstructFromBytes(data);

        byte comment[] = new byte[256]; data.get(comment);
        byte payload[] = new byte[data.getInt()]; data.get(payload);

        return new Transaction(flags, maghs, lockt, inputs, outputs, comment, payload);
    }

    @Override
    public Transaction ConstructFromInput(DataInputStream stream) throws IOException
    {
        long flags = stream.readLong();
        long maghs = stream.readLong();
        long lockt = stream.readLong();

        TransactionInput inputs[] = new TransactionInput[stream.readInt()];

        for (int i = 0; i < inputs.length; i ++)
            inputs[i] = new irTransactionInputConstructor().ConstructFromInput(stream);

        TransactionOutput outputs[] = new TransactionOutput[stream.readInt()];

        for (int i = 0; i < outputs.length; i ++)
            outputs[i] = new irTransactionOutputConstructor().ConstructFromInput(stream);

        byte comment[] = new byte[256]; stream.read(comment);
        byte payload[] = new byte[stream.readInt()]; stream.read(payload);

        return new Transaction(flags, maghs, lockt, inputs, outputs, comment, payload);
    }

    @Override
    public Transaction ConstructFromOther(Transaction other)
    {
        return null;
    }
}