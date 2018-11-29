package nucleus.crypto;

import nucleus.exceptions.ECLibException;
import nucleus.protocols.protobufs.Address;
import nucleus.protocols.transaction.Signature;
import nucleus.protocols.transaction.Transaction;
import nucleus.protocols.transaction.TransactionData;
import nucleus.protocols.transaction.TransactionInput;
import nucleus.system.Context;
import nucleus.system.Parameters;
import nucleus.util.ByteUtil;

import java.util.LinkedList;
import java.util.List;

public class KeyChain
{
    private byte[]                  seed;
    private LinkedList<KeyPair>     keyPair;

    public KeyChain(byte seed[]) throws ECLibException
    {
        this.seed = seed;
        this.keyPair = new LinkedList<>();

        this.keyPair.add(new KeyPair(seed));
    }

    public KeyChain generate() throws ECLibException
    {
        KeyPair lastKeypair = keyPair.getLast();

        keyPair.add(new KeyPair(new MnemonicPhraseSeeder(ByteUtil.concatenate(seed, lastKeypair.getPrivateKey().toByteArray())).getSeed()));

        return this;
    }

    /**
     * @param data
     * @return A valid ECDSA Signature.
     *
     * The function generates a signature and then generates a new Keypair for improved safety,
     * The old keypair and seed are left in the chain for further usability, however the newly
     * generated keypair should be used to send and or receive any transactions in the future.
     */
    public Signature generateSignature(byte data[]) throws ECLibException
    {
        generate();
        return new Signature();
    }

    public Transaction send(Context context, List<TransactionData> data)
    {
        long totalAmount = 0;

        for (TransactionData transactionData : data)
        {
            if (transactionData.getAmount() >= Parameters.MINIMUM_TRANSACTION)
                totalAmount += transactionData.getAmount();
        }

        Address current = pair().getAddress();
        Signature signature = new Signature();

        TransactionInput inputs[] = context.getLedger().getBalanceTable(current).getOutputs(totalAmount, signature.getSignature());

        return null;
    }

    public void write()
    {
    }

    public void read()
    {
    }

    public KeyPair pair()
    {
        return keyPair.getLast();
    }
}