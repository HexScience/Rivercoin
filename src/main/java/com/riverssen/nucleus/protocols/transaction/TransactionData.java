package com.riverssen.nucleus.protocols.transaction;

import com.riverssen.nucleus.protocols.protobufs.Address;
import com.riverssen.nucleus.protocols.transactionapi.TransactionPayload;

public class TransactionData
{
    private long    amount;
    private Address receiver;
    private byte    lockingscript[];

    public TransactionData(long amount, Address receiver)
    {
        this(amount, receiver, TransactionPayload.P2PKH_lock(receiver));
    }

    public TransactionData(long amount, Address receiver, byte lockingscript[])
    {
        this.amount = amount;
        this.receiver = receiver;
        this.lockingscript = lockingscript;
    }

    public long getAmount()
    {
        return amount;
    }

    public Address getReceiver()
    {
        return receiver;
    }

    public byte[] getLockingscript()
    {
        return lockingscript;
    }
}