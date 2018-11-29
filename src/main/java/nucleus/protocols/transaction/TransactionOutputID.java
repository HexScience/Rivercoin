package nucleus.protocols.transaction;

import nucleus.util.ByteUtil;

public class TransactionOutputID
{
    private long    block;
    /**
     * 3 byte index allows for a maximum of (2^24) transactions in a block or simply (16.777.216) transactions per block;
     * (1398101,333333333333333) transactions a second.
     */
    private byte[]   txid = new byte[3];
    /**
     * 2 byte index allows for a maximum of (2^16) or 65.536 outputs a block; or (5461,333333333333333) outputs a second.
     * In reality this allows up to (1.099.511.627.776) outputs per block or up to (91.625.968.981,333333333333333) outputs per second.
     */
    private short   txoid;

    public TransactionOutputID(long block, int txid, short txoid)
    {
        this.block = block;
        this.txid  = ByteUtil.trim(ByteUtil.encodei(txid), 1, 4);
        this.txoid = txoid;
    }

    public long getBlock()
    {
        return block;
    }

    public int getTransaction()
    {
        return ByteUtil.decodei(ByteUtil.concatenate(new byte[] {0}, txid));
    }

    public int getOutput()
    {
        return txoid;
    }
}
