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
import com.riverssen.core.utils.Handler;
import com.riverssen.riverssen.Constant;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class BlockChain implements BlockChainI
{
    private volatile Set<FullBlock>                  orphanedBlocks;
//    private Map<Client, Set<FullBlock>>     downloadedBlocks;
    private volatile Set<FullBlock>                  downloadedBlocks;
    private volatile Handler<FullBlock>              block;
    private volatile ContextI                        context;
    private volatile long                            lastvalidated;
    private volatile ReentrantLock                   lock;
    private volatile boolean                          mlock;

    public BlockChain(ContextI context)
    {
        this.context        = context;
        this.orphanedBlocks = Collections.synchronizedSet(new LinkedHashSet<>());
        this.downloadedBlocks= Collections.synchronizedSet(new LinkedHashSet<>());
        this.lock           = new ReentrantLock();
        this.block          = new Handler<>(null);
    }

    @Override
    public synchronized void FetchTransactions()
    {
    }

    @Override
    public synchronized void ValidateTransactions()
    {
    }

    @Override
    public synchronized void RemoveDoubleSpends()
    {
    }

    @Override
    public synchronized void LoadBlockChain()
    {
    }

    public synchronized BlockHeader lastBlockHeader()
    {
        lock.lock();
        try{
            if(currentBlock() == 0) return null;
            return new BlockHeader(currentBlock() - 1, context);
        } finally {
            lock.unlock();
        }
    }

    public synchronized void download(FullBlock block)
    {
        lock.lock();
        try{
            this.downloadedBlocks.add(block);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public synchronized void FetchBlockChainFromPeers()
    {
        Logger.alert("attempting to download block(s) from peers");
        @Constant Set<Client> communicators = new LinkedHashSet<>(context.getNetworkManager().getCommunicators());

//        context.getNetworkManager().downloadLongestChain();
        Logger.alert("listing...");

        List<Client> nodes = new ArrayList<>(communicators);
        Logger.alert("arranging...");

        //Descending Ordered List
        nodes.sort((a, b)->{
            if(a.getChainSize() == b.getChainSize()) return 0;
            else if(a.getChainSize() > b.getChainSize()) return -1;
            else return 1;
        });

        Logger.alert("rearranging...");

        if(nodes.size() == 0) {
            Logger.alert("no nodes found.");
            return;
        }

        long startingPoint = currentBlock();

        Logger.alert("client '" + nodes.get(0).getChainSize() + "' block(s) behind");

        client_iterator :
            for(Client node : nodes) {
                Logger.alert("attempting: " + node);

                if(node.isBlocked()) continue;
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
                                if(block.getI().validate(context) != 0)
                                {
                                    node.block();

                                    for(long i = startingPoint; i < currentBlock(); i ++)
                                        FileUtils.deleteblock(i, context);

                                    break client_iterator;
                                }

                                this.block.setI(next);
                                downloadedBlocks.remove(next);
                            }
                    }
                }

                node.unlock(lock);
            }
    }

    @Override
    public synchronized void FetchBlockChainFromDisk()
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

        this.block.setI(BlockHeader.FullBlock(latestblock, context));

        Logger.alert("block loaded successfully");
    }

    @Override
    public synchronized void Validate()
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
        lock.lock();
        try{
            if(block == null) return -1;

            return block.getI().getHeader().getBlockID();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void queueBlock(FullBlock block)
    {
        lock.unlock();
        try{
            orphanedBlocks.add(block);
        } finally {
            lock.unlock();
        }
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

        if(block.getI() == null)
            block.setI(new FullBlock(-1, null, context));
        else block.setI(block.getI().getHeader().continueChain(context));

//        System.exit(0);

        Logger.alert("finished processes.");

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

//                    blockList.sort((a, b)->{
//                        if          (a.getBlockID() == b.getBlockID()) return 0;
//                        else if     (a.getBlockID() > b.getBlockID()) return 1;
//
//                        return -1;
//                    });

                    /** This function should choose the biggest block in queue at the current level **/
                    /** first block downloaded **/
                    this.block.setI(blockList.get(0));

                    this.block.getI().serialize(context);
                    this.block.setI(this.block.getI().getHeader().continueChain(context));
                    lastBlockWas = System.currentTimeMillis();

                    blockList.clear();
                }
            }

            delete.clear();
            blockList.clear();

            if(mlock)
            {
            } else {
                while (context.getTransactionPool().available()) {
                    block.getI().add(context.getTransactionPool().next(), context);
                    if (block.getI().getBody().mine(context))
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
                            this.block.getI().free(context);

                            this.block.setI(orphaned);
                            this.block.getI().serialize(context);
                            this.block.setI(this.block.getI().getHeader().continueChain(context));
                        }
                    }
                }

                if(block.getI().getBody().mine(context))
                {
                    block.getI().mine(context);

                    boolean continueBlock = true;

                    while (orphanedBlocks.size() > 0)
                    {
                        FullBlock orphaned = orphanedBlocks.iterator().next();

                        orphanedBlocks.remove(orphaned);

                        if(orphaned.getBlockID() == currentBlock() && orphaned.validate(context) == 0 && orphaned.getHeader().getTimeStampAsLong() <= block.getI().getHeader().getTimeStampAsLong())
                        {
                            this.block.getI().free(context);

                            this.block.setI(orphaned);
                            this.block.getI().serialize(context);
                            this.block.setI(this.block.getI().getHeader().continueChain(context));
                            continueBlock = false;
                        }
                    }

                    if(continueBlock)
                    {
                        /** Send Solution To Nodes **/

                        context.getNetworkManager().sendBlock(block.getI());

                        block.getI().serialize(context);

                        block.setI(block.getI().getHeader().continueChain(context));
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
                this.block.getI().serialize(context);
                this.block.setI(block);
        }
    }
}