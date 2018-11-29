package nucleus.ledger;

import nucleus.protocols.transaction.TransactionOutput;
import nucleus.protocols.transaction.TransactionOutputID;

import java.util.HashMap;
import java.util.Map;

public class LedgerDatabase
{
    private Map<byte[], byte[]> queried;
    private Map<byte[], byte[]> removed;

    public LedgerDatabase()
    {
        queried = new HashMap<>();
        removed = new HashMap<>();
    }

    public boolean UTXOExists(TransactionOutputID utxoid)
    {
        return false;
    }

    public TransactionOutput getUTXO(byte[] id)
    {
        return null;
    }

    public void removeUTXO(byte[] id)
    {
    }
}
