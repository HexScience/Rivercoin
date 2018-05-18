package com.riverssen.core;

import com.riverssen.core.chain.BlockData;
import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.headers.UTXO;
import com.riverssen.core.headers.UTXOTraverser;
import com.riverssen.core.security.CompressedAddress;
import com.riverssen.core.security.PublicAddress;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * The UTXO manager class that will hold UTXO information for a certain public address
 */
public class UnspentTransactionOutputs
{
    private PublicAddress address;
    private List<UTXO>    tokens;
    private long          lastTransactionID;

    public UnspentTransactionOutputs(PublicAddress address, long currentBlock)
    {
        this.address = address;

        Executors.newFixedThreadPool(1).execute(()->{
            long mblock = currentBlock;

            while(mblock > -1)
                new BlockData(mblock ++).FetchUTXOs(address, tokens);
        });
    }

    public UnspentTransactionOutputs(CompressedAddress address, long currentBlock)
    {
        this.address = address.toPublicKey().getPublicWalletAddress();
    }

    public void addInput(TransactionI token)
    {
        tokens.add(null);
    }

    public RiverCoin balance()
    {
        return new RiverCoin("0");
    }

    public void traverse(UTXOTraverser traverser)
    {
    }
}