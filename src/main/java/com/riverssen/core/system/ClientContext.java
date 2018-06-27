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

package com.riverssen.core.system;

import com.riverssen.core.BlockChain;
import com.riverssen.core.BlockPool;
import com.riverssen.core.RivercoinCore;
import com.riverssen.core.TransactionPool;
import com.riverssen.core.algorithms.Provider;
import com.riverssen.core.block.BlockHeader;
import com.riverssen.core.networking.messages.GoodByeMessage;
import com.riverssen.riverssen.RiverFlowMap;
import com.riverssen.core.exceptions.AddressInvalidException;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.headers.HashAlgorithm;
import com.riverssen.core.networking.Server;
import com.riverssen.core.networking.NetworkManager;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.core.security.Wallet;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.riverssen.UTXOMap;

import java.math.BigInteger;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientContext implements ContextI
{
    private ExecutorService executorService;
    private NetworkManager networkManager;
    private BlockPool blockPool;
    private TransactionPool transactionPool;
    private UTXOMap utxoManager;
    private PublicAddress miner;
    private Wallet wallet;
    private Config config;
    private BlockChain blockChain;
    private Provider provider;
    private boolean running;
    private final long versionBytes;

    public ClientContext(Config config) throws Exception
    {
        this.config = config;
        this.running = true;
        this.executorService = Executors.newCachedThreadPool();
        this.networkManager = new Server(this);
        this.blockPool = new BlockPool(this);
        this.transactionPool = new TransactionPool(this);
        this.utxoManager = new RiverFlowMap();
        this.miner = this.config.getMinerAddress();
        this.wallet = this.config.getWallet();
        this.provider = new Provider();
        this.blockChain = new BlockChain(this);
        this.versionBytes = RivercoinCore.actual_version;

        if(!PublicAddress.isPublicAddressValid(config.getMinerAddress().toString())) throw new AddressInvalidException(config.getMinerAddress().toString());

        try
        {
//            this.getNetworkManager().establishConnection();
        } catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public long getTime()
    {
        return System.currentTimeMillis();
    }

    public long getVersionBytes()
    {
        return versionBytes;
    }

    public boolean shouldMine()
    {
        return transactionPool.getLastTransactionWas(config.getAverageBlockTime() / 2L);
    }

    public ExecutorService getExecutorService()
    {
        return executorService;
    }

    public NetworkManager getNetworkManager()
    {
        return networkManager;
    }

    public void setNetworkManager(NetworkManager networkManager)
    {
        this.networkManager = networkManager;
    }

    public BlockPool getBlockPool()
    {
        return blockPool;
    }

    public void setBlockPool(BlockPool blockPool)
    {
        this.blockPool = blockPool;
    }

    public TransactionPool getTransactionPool()
    {
        return transactionPool;
    }

    public void setTransactionPool(TransactionPool transactionPool)
    {
        this.transactionPool = transactionPool;
    }

    public UTXOMap getUtxoManager()
    {
        return utxoManager;
    }

    public void setUtxoManager(RiverFlowMap utxoManager)
    {
        this.utxoManager = utxoManager;
    }

    public PublicAddress getMiner()
    {
        return miner;
    }

    public void setMiner(PublicAddress miner)
    {
        this.miner = miner;
    }

    public Wallet getWallet()
    {
        return wallet;
    }

    public void setWallet(Wallet wallet)
    {
        this.wallet = wallet;
    }

    public Config getConfig()
    {
        return config;
    }

    public void setConfig(Config config)
    {
        this.config = config;
    }

    public void run()
    {
        long timer = System.currentTimeMillis();

        Scanner scanner = new Scanner(System.in);

        while (isRunning())
        {
//            CommandLine.Command command = CommandLine.newCommand();
            String text = null;

            while(scanner.hasNextLine())
            {
                text = scanner.nextLine();
//                command = command.parse(text, this);

                String sub[] = text.split(" ");

                if(sub.length > 0)
                {
                    String op   = sub[0];

                    switch (op)
                    {
                        case "wallet":
                            if(sub.length < 2)
                            {
                                Logger.err("incorrect usage:\n\twallet -g <name> <seed> <encryption key> <num addresses>\n\twallet -i <name>\n\twallet -n <keypair> <name>\n\twallet -e");
                                break;
                            }
                            switch (sub[1])
                            {
                                default:
                                    Logger.err("correct usage[1]: wallet -g <name> <seed> <encryption key> <num addresses>");
                                    break;

                                case "-n":
                                    if(sub.length < 3)
                                    {
                                        Logger.err("incorrect usage:\n\twallet -g <name> <seed> <encryption key> <num addresses>\n\twallet -i <name>\n\twallet -n <keypair name>");
                                        break;
                                    }

                                    if(wallet != null)
                                    {
                                        wallet.generateNewKeyPair(sub[2]);
                                    } else
                                        Logger.err("generate a wallet first!");

                                    break;
                                case "-i":
//                                        String wallet_name = sub[1];

//                                        Wallet wallet = new Wallet()
                                    break;
                                case "-g":
                                case "-generate":
                                    String name = sub[2];
                                    String seed = sub[3];
                                    String encr = sub[4];
                                    String numa = sub[5];

                                    int num = 0;

                                    try{
                                        num = Integer.parseInt(numa);
                                    } catch (Exception e)
                                    {
                                        Logger.err("correct usage[2]: wallet -g <name> <seed> <encryption key> <num addresses>");
                                        break;
                                    }

                                    this.wallet = com.riverssen.testing.Wallet.generate(this, name, seed, encr, numa);

                                    Logger.alert("wallet generation successful!");
                                break;
                            }
                            break;
                    }
                }
            }

//            command.parse(null, this);
        }
    }

    public HashAlgorithm getHashAlgorithm(byte blockHash[])
    {
        return provider.getRandomFromHash(blockHash);
    }

    public BigInteger getDifficulty()
    {
        BlockHeader last = getBlockChain().lastBlockHeader();
        if(last == null) return config.getMinimumTargetDifficulty();
        else if(last.getBlockID() == 0) return config.getMinimumTargetDifficulty();
        BlockHeader lastLast = new BlockHeader(last.getBlockID() - 1, this);

        return config.getCurrentDifficulty(last, lastLast);
    }

    public boolean isRunning()
    {
        return running;
    }

    public BlockChain getBlockChain()
    {
        return blockChain;
    }

    @Override
    public boolean actAsRelay()
    {
        return false;
    }

    @Override
    public void shutDown() {
        this.running = false;
        this.networkManager.sendMessage(new GoodByeMessage());
        System.exit(0);
    }
}