package com.riverssen.core;

import com.riverssen.core.algorithms.Sha3;
import com.riverssen.core.chain.*;
import com.riverssen.core.chain.BlockChain;
import com.riverssen.core.headers.BlockChainI;
import com.riverssen.core.networking.PeerNetwork;
import com.riverssen.core.security.PubKey;
import com.riverssen.core.security.Wallet;
import com.riverssen.core.tokens.UnsignedTransaction;
import com.riverssen.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Security;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RVCCore
{
    public static short versionBytes = 1;
    private final PeerNetwork network;
    private Wallet wallet;
    public static final String version = "0.0.2a";
    private static boolean GPUMining;

    public static Wallet PaddingWallet;
    private TransactionPool transactionPool;
    private BlockPool blockPool;
    private SolutionPool solutionPool;

    public static void main(String args[]) throws Exception
    {
        String root = "";

        if(args != null && args.length > 0)
            root = args[0];

        Config.create(root);
        new RVCCore();
    }

    private boolean         run;
    private static RVCCore  self;
    private ExecutorService service;
    private BlockChainI     blockChain;

    RVCCore() throws Exception
    {
        self = this;
        run = true;
        service = Executors.newFixedThreadPool(8);

        /**
         * Add the bouncy castle provider
         */
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        /** Generate directories if they don't exist **/
        FileUtils.createDirectoryIfDoesntExist(Config.getConfig().BLOCKCHAIN_DIRECTORY);
        FileUtils.createDirectoryIfDoesntExist(Config.getConfig().WALLET_DIRECTORY);
        FileUtils.createDirectoryIfDoesntExist(Config.getConfig().BLOCKCHAIN_TRX_DB);
        FileUtils.createDirectoryIfDoesntExist(Config.getConfig().BLOCKCHAIN_WLT_DB);
        FileUtils.createDirectoryIfDoesntExist(Config.getConfig().BLOCKCHAIN_DIRECTORY + File.separator + "temp");
        FileUtils.createDirectoryIfDoesntExist(Config.getConfig().WALLET_DIRECTORY + File.separator + "temp");
        FileUtils.createDirectoryIfDoesntExist(Config.getConfig().BLOCKCHAIN_TRX_DB + File.separator + "temp");
        FileUtils.createDirectoryIfDoesntExist(Config.getConfig().BLOCKCHAIN_WLT_DB + File.separator + "temp");
        Logger.alert("usable cpu threads: " + Config.getConfig().MAX_MINING_THREADS);

        /** Generate a wallet from the Public Address of the miner found in the config **/
        wallet = new Wallet(PubKey.fromPublicWalletAddress(Config.getConfig().PUBLIC_ADDRESS));

        network            = new PeerNetwork();
        blockPool          = new BlockPool(network);
        solutionPool       = new SolutionPool(network);
        TransactionPool transactionPool = new TransactionPool(network);
        blockChain         = new com.riverssen.core.BlockChain(transactionPool, blockPool, solutionPool, network, wallet.getPublicKey().getPublicWalletAddress());

        service.execute(blockChain);
        service.execute(()->{
            while (run)
            {
                network.Update();

                try
                {
                    Thread.sleep(20000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });

        while(run)
        {
        }

        FullBlock block                 = new FullBlock(0, new BlockHeader());

        BigInteger difficultyi = (Config.getConfig().TARGET_DIFFICULTY.toBigInteger()).divide(new BigInteger("1000000"));

        block.mine(new Sha3(), difficultyi, wallet.getPublicKey().getPublicWalletAddress(), solutionPool);

        System.exit(0);

        try
        {
            Config.calculateDifficulty();
        } catch (Exception e)
        {
            e.printStackTrace();
        };

        try
        {
            network.connect(service);
        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void setGPUMining(boolean GPUMining)
    {
        RVCCore.GPUMining = GPUMining;
    }

    private synchronized void alert()
    {
        long now = System.currentTimeMillis();
        BigInteger balance = Wallet.readBalance(wallet.getPublicKey().getPublicWalletAddress().toString());
        Logger.prt(Logger.COLOUR_BLUE, "wallet[" + wallet.getPublicKey().getPublicWalletAddress() + "] balance: " + new RiverCoin(balance).toRiverCoinString() + " lookup:" + (System.currentTimeMillis() - now) + "ms");
    }

    public synchronized static RVCCore get()
    {
        return self;
    }

    public synchronized boolean run()
    {
        return run;
    }

    public synchronized Wallet getWallet()
    {
        return wallet;
    }

    public synchronized void setWallet(Wallet wallet)
    {
        this.wallet = wallet;
    }

    public synchronized BlockChainI getChain()
    {
        return blockChain;
    }

    public TransactionPool getTransactionPool()
    {
        return transactionPool;
    }

    public BlockPool getBlockPool()
    {
        return blockPool;
    }

    public SolutionPool getSolutionPool()
    {
        return solutionPool;
    }
}