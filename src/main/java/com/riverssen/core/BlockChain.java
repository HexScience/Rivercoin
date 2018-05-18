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

import com.riverssen.core.chain.BlockHeader;
import com.riverssen.core.consensus.ConsensusAlgorithm;
import com.riverssen.core.headers.BlockChainI;
import com.riverssen.core.networking.PeerNetwork;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.core.system.LatestBlockInfo;
import com.riverssen.utils.Tuple;

import java.io.File;
import java.util.List;

public class BlockChain implements BlockChainI
{
    private FullBlock       block;
    private TransactionPool transactionPool;
    private BlockPool       blockPool;
    private SolutionPool    solutionPool;
    private PeerNetwork     network;
    private long            lastvalidated;
    private PublicAddress   miner;

    public BlockChain(TransactionPool tPool, BlockPool bPool, SolutionPool solutionPool, PeerNetwork network, PublicAddress miner)
    {
        this.transactionPool = tPool;
        this.blockPool       = bPool;
        this.solutionPool    = solutionPool;
        this.network         = network;
        this.miner           = miner;
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
        Logger.alert("attempting to download chain from peers");
        List<FullBlock> blocks = blockPool.Fetch();

        for(FullBlock block : blocks) block.serialize();
    }

    @Override
    public void FetchBlockChainFromDisk()
    {
        Logger.alert("attempting to load the blockchain from disk");

        File blockChainDirectory = new File(Config.getConfig().BLOCKCHAIN_DIRECTORY);

        LatestBlockInfo info = new LatestBlockInfo();
        try
        {
            info.read();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        long latestblock = info.getLatestBlock();

        if(latestblock < 0) return;

        this.block = new BlockHeader(latestblock).continueChain();

        Logger.alert("chain loaded successfully");
    }

    @Override
    public void Validate()
    {
        /** check (25 minutes) time passed since last validation **/
        if(System.currentTimeMillis() - lastvalidated < 1_500_000L) return;
        Logger.alert("attempting to validate chain");

        lastvalidated = System.currentTimeMillis();

        Tuple<String, Long> forkInfo = network.getForkInfo();
        long latestFork = forkInfo.getJ();

        /** check that our chain is the longest chain, if it is, then return **/
        if(latestFork < block.getBlockID()) return;

        /** if our chain is short then update it with the longest chain **/
        if(block.getBlockID() < forkInfo.getJ())
            FetchBlockChainFromPeers();

//        if(block.getBlockID() == forkInfo.getJ() && forkInfo.getI().equals(block.getHeader().getParentHashAsString()))
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

        while(RVCCore.get().run())
        {
            FetchTransactions();
            ValidateTransactions();
            RemoveDoubleSpends();
            Validate();

            if(block.getBody().mine())
                block.mine(ConsensusAlgorithm.getLatestInstance(block.getHeader().getParentHash()), Config.getConfig().TARGET_DIFFICULTY.toBigInteger(), miner, solutionPool);
        }
    }
}