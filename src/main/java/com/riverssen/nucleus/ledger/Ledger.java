package com.riverssen.nucleus.ledger;

import com.riverssen.nucleus.protocols.protobufs.Address;

public class Ledger
{
    public AddressBalanceTable getBalanceTable(Address address)
    {
        return new AddressBalanceTable();
    }
}
