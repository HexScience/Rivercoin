package com.riverssen.wallet;

import com.riverssen.core.headers.ContextI;
import com.riverssen.protobufs.output.Address;
import com.riverssen.protobufs.output.CompressedKey;
import com.riverssen.protobufs.output.Transaction;

import java.util.ArrayList;
import java.util.List;

public class Wallet
{
    private ContextI context;
    private KeyChain chain;
    public boolean   addressSwitchingEnabled;

    public Wallet()
    {
    }

    public void sendTransaction(long amt, long fee, PublicAddress to, String message, String password) throws Exception {
        CompressedKey from = new CompressedKey(chain.getPublic().getCompressedForm().getBytes());
        PrivKey privateKey = (PrivKey) chain.getPrivate();

        if (message.length() > 256)
            message = message.substring(0, 256);

        if (addressSwitchingEnabled)
            chain.generateNewChain();

        List<Integer> utxos = new ArrayList<>();

        Transaction transaction = new Transaction(from, new Address(to.getBytes()), amt, utxos, fee, message.getBytes());
        Transaction.sign(transaction, privateKey, password);
    }
}