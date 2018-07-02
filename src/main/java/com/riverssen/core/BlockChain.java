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
import com.riverssen.core.block.FullBlock;
import com.riverssen.core.headers.BlockChainI;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.networking.Client;
import com.riverssen.core.networking.messages.RequestBlockMessage;
import com.riverssen.core.security.Wallet;
import com.riverssen.core.system.LatestBlockInfo;
import com.riverssen.core.system.Logger;
import com.riverssen.core.transactions.TXIList;
import com.riverssen.core.transactions.Transaction;
import com.riverssen.core.utils.Base58;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.core.utils.FileUtils;
import com.riverssen.riverssen.Constant;

import java.util.*;

public class BlockChain implements BlockChainI
{
    private Set<FullBlock>                  orphanedBlocks;
//    private Map<Client, Set<FullBlock>>     downloadedBlocks;
    private Set<FullBlock>                  downloadedBlocks;
    private FullBlock                       block;
    private ContextI                        context;
    private long                            lastvalidated;
    private boolean                         lock;

    public BlockChain(ContextI context)
    {
        this.context        = context;
        this.orphanedBlocks = new LinkedHashSet<>();
        this.downloadedBlocks= new LinkedHashSet<>();
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

    public BlockHeader lastBlockHeader()
    {
        if(currentBlock() == 0) return null;
        return new BlockHeader(currentBlock() - 1, context);
    }

    public void download(FullBlock block)
    {
        this.downloadedBlocks.add(block);
    }

    @Override
    public void FetchBlockChainFromPeers()
    {
        Logger.alert("attempting to download block(s) from peers");
        final long totalNodes = context.getNetworkManager().amountNodesConnected();
        @Constant Set<Client> communicators = context.getNetworkManager().getCommunicators();

        context.getNetworkManager().downloadLongestChain();
        List<Client> nodes = new ArrayList<>();

        for(Client communicator : communicators)
            if(communicator.isRelay()) nodes.add(communicator);

        //Descending Ordered List
        nodes.sort((a, b)->{
            if(a.getChainSize() == b.getChainSize()) return 0;
            else if(a.getChainSize() > b.getChainSize()) return -1;
            else return 1;
        });

        if(nodes.size() == 0) return;

        long startingPoint = currentBlock();

        Logger.alert("client '" + nodes.get(0).getChainSize() + "' block(s) behind");

        client_iterator :
            for(Client node : nodes) {
                String lock = ByteUtil.defaultEncoder().encode58((System.currentTimeMillis() + " BlockChainLock: " + node).getBytes());

                //Wait for node to unlock.
                while (!node.lock(lock)) {}

                for (long i = context.getBlockChain().currentBlock(); i < node.getChainSize(); i++)
                    if (i >= 0) node.sendMessage(new RequestBlockMessage(i));

                long required = node.getChainSize() - Math.max(currentBlock(), 0);

                long lastChange = System.currentTimeMillis();
                long lastChainS = startingPoint;

                while (currentBlock() < required)
                {
                    if(lastChainS != currentBlock())
                    {
                        lastChange = System.currentTimeMillis();
                        lastChainS = currentBlock();
                    }

                    if (System.currentTimeMillis() - lastChange > ((3.75 * 60_000L) * required))
                        Logger.err("a network error might have occurred, no updates to the network.");

                    if (System.currentTimeMillis() - lastChange > ((7.5 * 60_000L) * required))
                    {
                        Logger.err("a network error might have occurred, '" + downloadedBlocks.size() + "' downloaded out of '" + required + "'.");
                        Logger.alert("forking...");

                        node.block();
                        downloadedBlocks.clear();
                        break client_iterator;
                    }

                    if(downloadedBlocks.size() > 0)
                    {
                        FullBlock next  = null;

                        for(FullBlock fullBlock : downloadedBlocks)
                            if(fullBlock.getBlockID() == (currentBlock() + 1))
                            {
                                next = fullBlock;
                                break;
                            }

                            if(next != null)
                            {
                                if(block.validate(context) != 0)
                                {
                                    node.block();

                                    for(long i = startingPoint; i < currentBlock(); i ++)
                                        FileUtils.deleteblock(i, context);

                                    break client_iterator;
                                }

                                this.block = next;
                                downloadedBlocks.remove(next);
                            }
                    }
                }

                node.unlock(lock);
            }
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
//        Wallet wallet = new Wallet("test", "test");
//        context.getTransactionPool().addInternal(new Transaction(wallet.getPublicKey().getCompressed(), context.getMiner(), new TXIList(), new RiverCoin("12.0"), "bro").sign(wallet.getPrivateKey()));

        FetchBlockChainFromDisk();
//        FetchBlockChainFromPeers();
//        if(block == null)
//            block = new FullBlock(-1, new BlockHeader());

        FetchBlockChainFromPeers();

//        Validate();

//        System.out.println(block.toJSON());

        if(block == null)
            block = new FullBlock(-1, null, context);
        else block = block.getHeader().continueChain(context);

//        System.exit(0);

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
                    this.block = this.block.getHeader().continueChain(context);
                    lastBlockWas = System.currentTimeMillis();

                    blockList.clear();
                }
            }

            delete.clear();
            blockList.clear();

            if(lock)
            {
            } else {
                while (context.getTransactionPool().available()) {
                    block.add(context.getTransactionPool().next(), context);
                    if (block.getBody().mine(context))
                        break;
                }

                if(System.currentTimeMillis() - lastBlockWas >= context.getConfig().getAverageBlockTime())
                {
                    while (orphanedBlocks.size() > 0)
                    {
                        FullBlock orphaned = orphanedBlocks.iterator().next();

                        orphanedBlocks.remove(orphaned);

                        if(orphaned.getBlockID() == currentBlock() && orphaned.validate(context) == 0)
                        {
                            this.block.free(context);

                            this.block = orphaned;
                            this.block.serialize(context);
                            this.block = this.block.getHeader().continueChain(context);
                        }
                    }
                }

                if(block.getBody().mine(context))
                {
                    block.mine(context);

                    boolean continueBlock = true;

                    while (orphanedBlocks.size() > 0)
                    {
                        FullBlock orphaned = orphanedBlocks.iterator().next();

                        orphanedBlocks.remove(orphaned);

                        if(orphaned.getBlockID() == currentBlock() && orphaned.validate(context) == 0 && orphaned.getHeader().getTimeStampAsLong() <= block.getHeader().getTimeStampAsLong())
                        {
                            this.block.free(context);

                            this.block = orphaned;
                            this.block.serialize(context);
                            this.block = this.block.getHeader().continueChain(context);
                            continueBlock = false;
                        }
                    }

                    if(continueBlock)
                    {
                        /** Send Solution To Nodes **/

                        context.getNetworkManager().sendBlock(block);

                        block.serialize(context);

                        block = block.getHeader().continueChain(context);
                        lastBlockWas = System.currentTimeMillis();
                    }
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