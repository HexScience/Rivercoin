package nucleus.ledger;

import nucleus.protocol.protobufs.Address;
import nucleus.protocol.protobufs.TransactionOutput;
import nucleus.system.Context;

import java.util.List;

public class AddressBalanceTable
{
    private final   Address                 address;
    private         List<TransactionOutput> balances;

    public AddressBalanceTable(final Address address)
    {
        this.address = address;
    }

    public long collectiveBalance(Context context)
    {
        long b = 0;

        for (TransactionOutput output : balances)
            b += output.getValue();

        return b;
    }

    public TransactionOutput getOutput(int output)
    {
        return balances.get(output);
    }
}
