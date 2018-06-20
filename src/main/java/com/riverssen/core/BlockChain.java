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
import com.riverssen.core.networking.Client;
import com.riverssen.core.networking.messages.RequestBlockMessage;
import com.riverssen.core.system.LatestBlockInfo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BlockChain implements BlockChainI
{
    private Set<FullBlock>  orphanedBlocks;
    private FullBlock       block;
    private ContextI        context;
    private long            lastvalidated;
    private boolean         lock;

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
        final long totalNodes = context.getNetworkManager().amountNodesConnected();
        Set<Client> communicators = context.getNetworkManager().getCommunicators();

        context.getNetworkManager().downloadLongestChain();
        List<Client> nodes = new ArrayList<>();

        for(Client communicator : communicators)
            if(communicator.isRelay()) nodes.add(communicator);

        //Descending Ordered List
        nodes.sort((a, b)->{ if(a.getChainSize() == b.getChainSize()) return 0;
        else if(a.getChainSize() > b.getChainSize()) return -1;
        else return 1;
        });

        for(Client node : nodes)
            for(long i = context.getBlockChain().currentBlock() - 1; i < node.getChainSize(); i ++)
                node.sendMessage(new RequestBlockMessage(i));
    }

    @Override
    public void FetchBlockChainFromDisk()
    {
        Logger.alert("attempting to load the blockchain from disk");

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

        this.block = BlockHeader.FullBlock(latestblock, context);

        Logger.alert("block loaded successfully");
    }

    @Override
    public void Validate()
    {
        /** check (25 minutes) time passed since last validation **/
        if(System.currentTimeMillis() - lastvalidated < 1_500_000L) return;
        Logger.alert("attempting to validate block");

        lastvalidated = System.currentTimeMillis();

//        Tuple<String, Long> forkInfo = context.getNetworkManager().getForkInfo();
//        long latestFork = forkInfo.getJ();

        /** check that our block is the longest block, if it is, then return **/
//        if(latestFork < block.getBlockID()) return;

        /** if our block is short then update it with the longest block **/
//        if(block.getBlockID() < forkInfo.getJ())
//            FetchBlockChainFromPeers();
    }

    @Override
    public long currentBlock()
    {
        if(block == null) return -1;

        return block.getHeader().getBlockID();
    }

    @Override
    public void queueBlock(FullBlock block)
    {
        orphanedBlocks.add(block);
    }

    @Override
    public void run()
    {
        FetchBlockChainFromDisk();
//        FetchBlockChainFromPeers();
//        if(block == null)
//            block = new FullBlock(-1, new BlockHeader());

        FetchBlockChainFromPeers();

//        Validate();

        System.out.println(block.toJSON());

        if(block == null)
            block = new FullBlock(-1, null);
        else block = block.getHeader().continueChain();


        System.exit(0);

        Set<FullBlock> delete = new LinkedHashSet<>();
        List<FullBlock> blockList = new ArrayList<>();

        long lastBlockWas = 1L;

        while(context.isRunning())
        {
            for (FullBlock block : orphanedBlocks)
                if(block.getBlockID() < currentBlock() - 1)
                    delete.add(block);

            orphanedBlocks.removeAll(delete);

            delete.clear();

            if(System.currentTimeMillis() - lastBlockWas >= context.getConfig().getAverageBlockTime())
            {
                while (orphanedBlocks.size() > 0)
                {
                    long current = currentBlock() - 1;

                    for(FullBlock block : orphanedBlocks)
                        if(block.getBlockID() == current)
                            blockList.add(block);

                    orphanedBlocks.removeAll(blockList);

                    blockList.sort((a, b)->{
                        if          (a.getBlockID() == b.getBlockID()) return 0;
                        else if     (a.getBlockID() > b.getBlockID()) return 1;

                        return -1;
                    });

                    /** This function should choose the biggest block in queue at the current level **/
                    this.block = blockList.get(blockList.size() - 1);

                    this.block.serialize(context);
                    this.block = this.block.getHeader().continueChain();
                    lastBlockWas = System.currentTimeMillis();

                    blockList.clear();
                }
            }

            delete.clear();
            blockList.clear();

            if(lock)
            {

            } else {
                if(block.getBody().mine(context))
                {
                    block.mine(context);
                    block.serialize(context);

                    System.exit(0);

                    block = block.getHeader().continueChain();
                    lastBlockWas = System.currentTimeMillis();
                }
            }
        }
    }

    public void insertBlock(FullBlock block)
    {
        if(currentBlock() + 1 == block.getBlockID())
        {
            if(this.block != null)
                this.block.serialize(context);
                this.block = block;
        }
    }
}