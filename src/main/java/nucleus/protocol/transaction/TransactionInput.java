package nucleus.protocol.transaction;

import nucleus.util.ByteUtil;

public class TransactionInput
{
    private byte    previousTransaction[];
    private int     previousTXoutIndex;
    /** the unlocking script to be used to unlock this utxo **/
    private byte    unlockingscript[];

    public TransactionInput()
    {
    }

    public TransactionInput(byte previousTXN[], int previousTXoutIndex, byte[] unlockingscript)
    {
        this.previousTransaction    = previousTXN;
        this.previousTXoutIndex     = previousTXoutIndex;
        this.unlockingscript        = unlockingscript;
    }

    /**
     * @return A unique identifier that can be used in lookup tables.
     */
    public byte[] getUniqueIdentifier()
    {
        return ByteUtil.concatenate(previousTransaction, ByteUtil.encodei(previousTXoutIndex));
    }

    public byte[] getUnlockingScript()
    {
        return unlockingscript;
    }
}
