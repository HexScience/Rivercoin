package nucleus.versioncontrol.versions.ir;

import nucleus.protocols.transaction.TransactionInput;
import nucleus.versioncontrol.Constructor;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class irTransactionInputConstructor implements Constructor<TransactionInput>
{
    @Override
    public TransactionInput ConstructFromBytes(ByteBuffer data)
    {
        byte prvtxn[] = new byte[32]; data.get(prvtxn);
        long block    = data.getLong();
        int txn       = data.getInt();
        int prvtxni   = data.getInt();
        byte unksct[] = new byte[data.getShort()]; data.get(unksct);

        return new TransactionInput(prvtxn, block, txn, prvtxni, unksct);
    }

    @Override
    public TransactionInput ConstructFromInput(DataInputStream stream) throws IOException
    {
        byte prvtxn[] = new byte[32]; stream.read(prvtxn);
        long block    = stream.readLong();
        int txn       = stream.readInt();
        int prvtxni   = stream.readInt();
        byte unksct[] = new byte[stream.readShort()]; stream.read(unksct);

        return new TransactionInput(prvtxn, block, txn, prvtxni, unksct);
    }

    @Override
    public TransactionInput ConstructFromOther(TransactionInput other)
    {
        return null;
    }
}