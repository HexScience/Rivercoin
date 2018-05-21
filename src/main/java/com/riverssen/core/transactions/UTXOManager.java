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

package com.riverssen.core.transactions;

import com.riverssen.core.Config;
import com.riverssen.core.FullBlock;
import com.riverssen.core.chain.BlockHeader;
import com.riverssen.core.compression.Base58Hash;
import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.system.LatestBlockInfo;
import com.riverssen.utils.Base58;
import com.riverssen.utils.MerkleTree;

import java.util.HashMap;
import java.util.Map;

public class UTXOManager
{
    private static Map<byte[], TransactionOutput> map = new HashMap<>();

    public static TransactionOutput get(@Base58Hash byte hash[])
    {
        return map.get(hash);
    }

    public static TransactionOutput get(@Base58Hash String hash)
    {
        return get(Base58.decode(hash));
    }

    public static void loadUTXOs()
    {
        LatestBlockInfo blockInfo = new LatestBlockInfo();
        long l = blockInfo.getLatestBlock();

        for(long lng = 0; lng < l; l ++)
            add(BlockHeader.FullBlock(lng));
    }

    public static void add(FullBlock block)
    {
        MerkleTree mt = block.getBody().getMerkleTree();

        for(TransactionI transaction : mt.flatten())
            for(TransactionOutput transactionOutput : transaction.getOutputs())
                map.put(transactionOutput.getHash(), transactionOutput);
    }

    static class ChainedMap
    {
    }
}
