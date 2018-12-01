package nucleus.ledger;

import nucleus.protocols.protobufs.Address;
import nucleus.protocols.transaction.TransactionInput;

public class Ledger
{
    public AddressBalanceTable getBalanceTable(Address address)
    {
        return new AddressBalanceTable();
    }
}
