package com.riverssen.nucleus.versioncontrol.versions.ir;

import com.riverssen.nucleus.protocols.protobufs.Block;
import com.riverssen.nucleus.protocols.protobufs.BlockHeader;
import com.riverssen.nucleus.protocols.transaction.Transaction;
import com.riverssen.nucleus.versioncontrol.Constructor;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedHashSet;
import java.util.Set;

public class irBlockConstructor implements Constructor<Block>
{
    @Override
    public Block ConstructFromBytes(ByteBuffer data)
    {
        BlockHeader header = new irBlockHeaderConstructor().ConstructFromBytes(data);
        short numTransactions = data.getShort();
        Set<Transaction> transactions = new LinkedHashSet<>();

        for (int i = 0; i < numTransactions; i ++)
            transactions.add(new irTransactionConstructor().ConstructFromBytes(data));

        byte codebase[] = new byte[data.getShort()];

        return new Block(header, transactions, null, codebase);
    }

    @Override
    public Block ConstructFromInput(DataInputStream stream) throws IOException
    {
        BlockHeader header = new irBlockHeaderConstructor().ConstructFromInput(stream);
        short numTransactions = stream.readShort();
        Set<Transaction> transactions = new LinkedHashSet<>();

        for (int i = 0; i < numTransactions; i ++)
            transactions.add(new irTransactionConstructor().ConstructFromInput(stream));

        byte codebase[] = new byte[stream.readShort()];

        return new Block(header, transactions, null, codebase);
    }

    @Override
    public Block ConstructFromOther(Block other)
    {
        return null;
    }
}