package com.riverssen.nucleus.protocols.transaction;

public class TransactionInput
{
    public static final int SIZE = 32 + 8 + 4 + 4;

    private byte    previousTransaction[];
//    private long    txnBlock; private int txnIndex;
//
    private int     previousTransactionOutputIndex;
//    private DBTransactionOutput output;
    /** the unlocking script to be used to unlock this utxo **/
    private byte    unlockingscript[];

    public TransactionInput()
    {
    }

    public TransactionInput(byte previousTXN[], int previousTransactionOutputIndex, byte[] unlockingscript)
    {
        this.previousTransaction            = previousTXN;
        this.previousTransactionOutputIndex = previousTransactionOutputIndex;
        this.unlockingscript                = unlockingscript;
    }

    /**
     * @return A unique identifier that can be used in lookup tables.
     */
    public byte[] getUniqueIdentifier()
    {
//        return ByteUtil.concatenate(previousTransaction, ByteUtil.encodei(previousTXoutIndex));
        return null;
    }

    public byte[] getUnlockingScript()
    {
        return unlockingscript;
    }

    public int size()
    {
        return SIZE + unlockingscript.length;
    }
}
