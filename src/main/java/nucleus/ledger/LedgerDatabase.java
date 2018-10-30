package nucleus.ledger;

import nucleus.protocol.protobufs.Address;

import java.util.HashMap;
import java.util.Map;

public class LedgerDatabase
{
    private Map<String, AddressBalanceTable> inMemLedger;

    public LedgerDatabase()
    {
        this.inMemLedger = new HashMap<>();
    }

    public AddressBalanceTable getAddressBalanceTable(Address owner)
    {
        return null;
    }
}
