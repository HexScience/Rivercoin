package com.riverssen.core.chain;

import com.riverssen.core.Config;
import com.riverssen.core.Logger;
import com.riverssen.core.RVCCore;
import com.riverssen.core.tokens.Token;
import com.riverssen.core.consensus.Solution;
import com.riverssen.core.system.LatestBlockInfo;
import com.riverssen.utils.LinkedList;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockChain implements Runnable
{
    private Block                                   block;
    private Map<Long, List<Solution>>               solutionPool;
    /**
     * We need a thread that mines.
     * And a thread that checks solutions by peers, that will stop the other thread from mining.
     */
    private ExecutorService                         service;
    private PriorityQueue<Token>                    tokenPool;
    private LinkedList<Token>                       tokenPoolSet;

    public BlockChain()
    {
        this(null);
    }

    public BlockChain(Block block)
    {
        this.block          = block;
        this.solutionPool   = Collections.synchronizedMap(new HashMap<>());
        this.service        = Executors.newFixedThreadPool(3);
        //TODO: check solutions by peers
        this.service.execute(() ->
        {
            getBroadcastedTransactionsFromPeers();
            getBroadcastedSolutionsFromPeers();
        });

        this.tokenPool          = new PriorityQueue<>();//Collections.synchronizedCollection(new PriorityQueue<Transaction>());
        this.tokenPoolSet       = new LinkedList<>();//Collections.synchronizedSet(new LinkedHashSet<Transaction>());
    }

    private Block downloadBlockChain(File blockChainDirectory)
    {
        Logger.alert("downloaded successfully.");

        long startingBlock = 0;

        if(LatestBlockInfo.exists())
        {
            //TODO: download from latest block
        }
            LatestBlockInfo info = new LatestBlockInfo();


        return null;
    }

    public Block loadBlockChainFromDrive()
    {
        Logger.alert("attempting to load the blockchain.");
        File blockChainDirectory = new File(Config.getConfig().BLOCKCHAIN_DIRECTORY);
//        File blocks[]            = blockChainDirectory.listFiles();
        File latestblock         = new File(Config.getConfig().BLOCKCHAIN_DIRECTORY + "latestblock");

        Block block = downloadBlockChain(blockChainDirectory);

        if(block == null && !latestblock.exists())
        {
            Logger.alert("not found, attempting to download longest fork.");

            return new Block(null);
        }

        if(block != null) return block;

        long latest = 0L;

        try
        {
            DataInputStream stream = new DataInputStream(new FileInputStream(latestblock));

            latest = stream.readLong();

            stream.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        this.block = new Block(new Block(new File(blockChainDirectory.toString() + "//block[" + latest + "]"), 0));

        Logger.alert("chain loaded successfully");
        Logger.alert("downloading remnants");

        return this.block;
    }

//    public synchronized void addToken(Transaction token)
//    {
////        pool.add(token);
////        pool.sort((a, b) ->
////        {
////            return (a.getTimeStamp() > b.getTimeStamp()) ? 1 : 0;
////        });
//    }

    @Override
    public void run()
    {
        if(block == null)
//        chain = Collections.synchronizedList(new ArrayList<>());
            this.block          = loadBlockChainFromDrive();
//            this.solution       = new Solution(block.getID(), RVCCore.get().getWallet().getPublicKey().getPublicAddress(), "");
        while (RVCCore.get().run())
        {
            synchronized (tokenPoolSet)
            {
                while (tokenPoolSet.size() > 5 * Config.getConfig().TOKENS_PER_BLOCK_TOMNE)
                    tokenPoolSet.removeEldestEntry();
            }

            synchronized (block)
            {
                if(block.isFull())
                {
                    if(solutionPool.containsKey(block.getID()))
                    {
                        if(checkSolutions()) continue;
                    }
                    else if(block.mined())
                    {
                        block.export();
                        block = new Block(block);
                    } else if(!block.isMining())
                    {
                        block.mineBlock();
                        broadcastSolutionToPeers(null);
                    }
                } else
                {
                    synchronized (tokenPool)
                    {
                        if(tokenPool.size() > 0) block.add(tokenPool.poll());
                    }
                }
            }
        }
    }

//    private synchronized Block block()
//    {
////        if (chain.size() == 0) return null;
////        return chain.get(chain.size() - 1);
//    }

    private synchronized boolean checkSolutions()
    {
        long currentBlock = 0;
        Block testB = null;

        synchronized (block)
        {
            currentBlock = block.getID();
        }

        for(long l = currentBlock - 1; l > -1; l --)
        {
            if(solutionPool.containsKey(l))
            {
                solutionPool.get(l).clear();
                solutionPool.remove(l);
            } else break;
        }

        if(!solutionPool.containsKey(currentBlock)) return false;

        for(Solution solution : solutionPool.get(currentBlock))
        {
            synchronized (block)
            {
                testB        = block.copyEntire();
            }

            if(testB.verifyWork(solution))
            {
                synchronized (block)
                {
                    block.getMiningService().shutdownNow();

                    block.submitSolution(solution);
                    block.export();
                    block = new Block(block);
                }

//                Logger.alert("solution for block[" + currentBlock + "] by: " + solution.getReward().getReceiverAddress() + " accepted!");
                return true;
            }
        }

        return false;
    }

    public synchronized void add(Token token)
    {
        if(!TXIO.transactionSafe(token)) return;

        synchronized (tokenPoolSet)
        {
            if(tokenPoolSet.contains(token)) return;
        }

        synchronized (tokenPool)
        {
            tokenPool.add(token);
        }
        synchronized (tokenPoolSet)
        {
            tokenPoolSet.add(token);
        }
    }

//    private Transaction poll()
//    {
//        Transaction token = pool.get(0);
//        pool.remove(0);
//
//        return token;
//    }

    private synchronized void broadcastSolutionToPeers(Solution solution)
    {
//        String signature = RVCCore.get().getWallet().getPrivateKey().sign(HashUtil.hashToStringBase16(HashUtil.applySha256(block.getHash().getBytes())));
    }

    public synchronized void getBroadcastedTransactionsFromPeers()
    {
    }

    public synchronized void getBroadcastedSolutionsFromPeers()
    {
    }

    public synchronized void addSolution(Solution solution)
    {
//        if(solutionPool.containsKey(solution.getBlockID()))
//            solutionPool.get(solution.getBlockID()).add(solution);
//
//        else
//        {
//            solutionPool.put(solution.getBlockID(), Collections.synchronizedList(new ArrayList<>()));//new PriorityQueue<>());
//            solutionPool.get(solution.getBlockID()).add(solution);
//        }
    }

    public synchronized Block getBlock()
    {
        return block;
    }

    public long currentBlock()
    {
        return 0;
    }
}