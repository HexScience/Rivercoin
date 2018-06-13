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

import com.riverssen.core.block.BlockHeader;
import com.riverssen.core.headers.BlockChainI;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.system.LatestBlockInfo;
import com.riverssen.utils.Tuple;

import java.io.File;
import java.util.List;

public class BlockChain implements BlockChainI
{
    private FullBlock       block;
    private ContextI context;
    private long            lastvalidated;

    public BlockChain(ContextI context)
    {
        this.context = context;
    }

    @Override
    public void FetchTransactions()
    {
    }

    @Override
    public void ValidateTransactions()
    {
    }

    @Override
    public void RemoveDoubleSpends()
    {
    }

    @Override
    public void LoadBlockChain()
    {
    }

    @Override
    public void FetchBlockChainFromPeers()
    {
        Logger.alert("attempting to download block from peers");
        List<FullBlock> blocks = context.getBlockPool().Fetch();

        for(FullBlock block : blocks) block.serialize(context);
    }

    @Override
    public void FetchBlockChainFromDisk()
    {
        Logger.alert("attempting to load the blockchain from disk");

        File blockChainDirectory = new File(context.getConfig().getBlockChainDirectory());

        LatestBlockInfo info = new LatestBlockInfo(context.getConfig());
        try
        {
            info.read();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        long latestblock = info.getLatestBlock();

        if(latestblock < 0) return;

        this.block = new BlockHeader(latestblock, context).continueChain();

        Logger.alert("block loaded successfully");
    }

    @Override
    public void Validate()
    {
        /** check (25 minutes) time passed since last validation **/
        if(System.currentTimeMillis() - lastvalidated < 1_500_000L) return;
        Logger.alert("attempting to validate block");

        lastvalidated = System.currentTimeMillis();

        Tuple<String, Long> forkInfo = context.getNetworkManager().getForkInfo();
        long latestFork = forkInfo.getJ();

        /** check that our block is the longest block, if it is, then return **/
        if(latestFork < block.getBlockID()) return;

        /** if our block is short then update it with the longest block **/
        if(block.getBlockID() < forkInfo.getJ())
            FetchBlockChainFromPeers();
    }

    @Override
    public long currentBlock()
    {
        return block.getHeader().getBlockID();
    }

    @Override
    public void run()
    {
        FetchBlockChainFromDisk();
        FetchBlockChainFromPeers();
        if(block == null)
            block = new FullBlock(-1, new BlockHeader());
        Validate();

        while(context.isRunning())
        {
            FetchTransactions();
            ValidateTransactions();
            RemoveDoubleSpends();
            Validate();

            if(block.getBody().mine())
                block.mine(context);
        }
    }
}