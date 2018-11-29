package nucleus.ledger;

import nucleus.protocols.protobufs.Address;
import nucleus.protocols.transaction.DBTransactionOutput;
import nucleus.protocols.transaction.Transaction;
import nucleus.protocols.transaction.TransactionInput;
import nucleus.protocols.transaction.TransactionOutput;
import nucleus.system.Context;
import org.iq80.leveldb.DB;

import java.nio.ByteBuffer;
import java.util.*;

public class AddressBalanceTable
{
    private final   Address                         address;
    private final   Set<DBTransactionOutput>        balances;
    private final   Map<String, TransactionOutput>  outputMap;
    private final   Context                         context;

    public AddressBalanceTable(final Context context, final Address address, final DB db)
    {
        this.address = address;
        this.balances = new LinkedHashSet<>();
        this.outputMap = new LinkedHashMap<>();
        this.context = context;

        byte data[] = db.get(address.getBytes());

        if (data != null && data.length > 0)
        {
            ByteBuffer hlData   = ByteBuffer.wrap(data);

            int numBalances     = hlData.getInt();

            for (int i = 0; i < numBalances; i ++)
            {
                DBTransactionOutput dbTransactionOutput = new DBTransactionOutput();
                dbTransactionOutput.read(hlData);

                balances.add(dbTransactionOutput);
                outputMap.put(dbTransactionOutput.toString(),
                        dbTransactionOutput.getOutput(context.getSerializer()));
            }
        }
    }

    /**
     * @param value The required transaction balance.
     * @param script The unlocking script needed to unlock these Transaction Outputs.
     * @return An array of Transaction Inputs or null if the balance is lower than "value".
     */
    public TransactionInput[] getOutputs(long value, byte script[])
    {
        List<TransactionInput> inputs = new ArrayList<>();
        long val = 0;

        for (DBTransactionOutput output : balances)
        {
            TransactionOutput utxo = outputMap.get(output.toString());
            Transaction transaction = context.getSerializer().loadTransaction(output.getBlockID(), output.getTransactionID());

            inputs.add(new TransactionInput(transaction.getTransactionID(), output.getOutputID(), script));

            val += utxo.getValue();

            if (val >= value)
                return (TransactionInput[]) inputs.toArray();
        }

        if (val >= value)
            return (TransactionInput[]) inputs.toArray();

        else return null;
    }

    /**
     * @param context
     * @return The balance associated with this address table.
     */
    public long collectiveBalance(Context context)
    {
        long b = 0;

        for (TransactionOutput output : outputMap.values())
            b += output.getValue();

        return b;
    }
}
