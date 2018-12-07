package com.riverssen.nucleus.versioncontrol;

import com.riverssen.nucleus.protocols.protobufs.Block;
import com.riverssen.nucleus.protocols.protobufs.BlockHeader;
import com.riverssen.nucleus.protocols.transaction.Transaction;
import com.riverssen.nucleus.protocols.transaction.TransactionInput;
import com.riverssen.nucleus.protocols.transaction.TransactionOutput;

public class Constructors// <const_head_constructor, const_block_constructor, const_txn_constructor, const_txin_constructor, const_txout_constructor>
{
    private final /**const_head_constructor **/Constructor      mBlockHeaderConstructor;
    private final /**const_block_constructor**/Constructor      mBlockConstructor;
    private final /**const_txn_constructor  **/Constructor      mTransactionConstructor;
    private final /**const_txin_constructor **/Constructor      mTransactionInput;
    private final /**const_txout_constructor**/Constructor      mTransactionOutput;

    public Constructors(Constructor bhc, Constructor bc, Constructor tc, Constructor tn, Constructor tt)
    {
        this.mBlockHeaderConstructor    = bhc;
        this.mBlockConstructor          = bc;
        this.mTransactionConstructor    = tc;
        this.mTransactionInput          = tn;
        this.mTransactionOutput         = tt;
    }

    public Constructor<BlockHeader> getBlockHeaderConstructor()
    {
        return mBlockHeaderConstructor;
    }

    public Constructor<Block> getBlockConstructor()
    {
        return mBlockConstructor;
    }

    public Constructor<Transaction> getTransactionConstructor()
    {
        return mTransactionConstructor;
    }

    public Constructor<TransactionInput> getTransactionInput()
    {
        return mTransactionInput;
    }

    public Constructor<TransactionOutput> getTransactionOutput()
    {
        return mTransactionOutput;
    }
}