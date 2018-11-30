package nucleus.protocols.transaction;

public class TransactionInput
{
    private byte    previousTransaction[];

    private long    txnBlock; private int txnIndex;

    private int     previousTXoutIndex;
    /** the unlocking script to be used to unlock this utxo **/
    private byte    unlockingscript[];

    public TransactionInput()
    {
    }

    public TransactionInput(byte previousTXN[], long block, int index, int previousTXoutIndex, byte[] unlockingscript)
    {
        this.previousTransaction    = previousTXN;
        this.txnBlock               = block;
        this.txnIndex               = index;
        this.previousTXoutIndex     = previousTXoutIndex;
        this.unlockingscript        = unlockingscript;
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
}
