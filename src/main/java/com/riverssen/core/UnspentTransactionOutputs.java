/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Riverssen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.riverssen.core;

import com.riverssen.core.chain.BlockData;
import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.transactions.UTXO;
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
        this.address = address.toPublicKey().getAddress();
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