package nucleus.versioncontrol.versions.ir;

import nucleus.protocols.protobufs.Block;
import nucleus.protocols.protobufs.BlockHeader;
import nucleus.protocols.transaction.Transaction;
import nucleus.versioncontrol.Constructor;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class irBlockConstructor implements Constructor<Block>
{
    @Override
    public Block ConstructFromBytes(ByteBuffer data)
    {
        BlockHeader header = new irBlockHeaderConstructor().ConstructFromBytes(data);
        short numTransactions = data.getShort();
        List<Transaction> transactions = new ArrayList<>();

        for (int i = 0; i < numTransactions; i ++)
            transactions.add(new irTransactionConstructor().ConstructFromBytes(data));

        byte codebase[] = new byte[data.getShort()];

        return new Block(header, transactions, codebase);
    }

    @Override
    public Block ConstructFromInput(DataInputStream stream) throws IOException
    {
        BlockHeader header = new irBlockHeaderConstructor().ConstructFromInput(stream);
        short numTransactions = stream.readShort();
        List<Transaction> transactions = new ArrayList<>();

        for (int i = 0; i < numTransactions; i ++)
            transactions.add(new irTransactionConstructor().ConstructFromInput(stream));

        byte codebase[] = new byte[stream.readShort()];

        return new Block(header, transactions, codebase);
    }

    @Override
    public Block ConstructFromOther(Block other)
    {
        return null;
    }
}