package nucleus.protocol.transaction;

public class TransactionInput
{
    /** the UTXO index **/
    private int utxo;
    /** the unlocking script to be used to unlock this utxo in the future **/
    private byte[] unlockingscript;

    public TransactionInput()
    {
    }

    public TransactionInput(int utxo, byte[] unlockingscript)
    {
        this.utxo = utxo;
        this.unlockingscript = unlockingscript;
    }
}
