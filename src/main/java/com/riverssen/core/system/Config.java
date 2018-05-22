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

import com.riverssen.core.Logger;
import com.riverssen.core.RiverCoin;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.core.security.Wallet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class Config
{
    private int         PORT;
    private int         MAX_MINING_THREADS;
    private String      BLOCKCHAIN_DIRECTORY;
    private String      BLOCKCHAIN_TRX_DB;
    private String      BLOCKCHAIN_WLT_DB;
    private String      WALLET_DIRECTORY;
    private String      UNIQUE_PEER_LINK;
    private String      PUBLIC_ADDRESS;
    private boolean     PRUNE;
    private String      BLOCK_CHAIN_DB;
    private BigInteger  CURRENT_TARGET = MINIMUM_TARGET_DIFFICULTY;
    private static Config self;

    public static String getMiningFee()
    {
        return "0.0000015";
    }

    public static String getMinimumTransactionAmount()
    {
        return "100000";
    }

    public int getPort()
    {
        return PORT;
    }

    public int getMaxMiningThreads()
    {
        return MAX_MINING_THREADS;
    }

    public String getBlockChainDirectory()
    {
        return BLOCKCHAIN_DIRECTORY;
    }

    public String getBlockChainTransactionDirectory()
    {
        return BLOCKCHAIN_TRX_DB;
    }

    public String getBlockChainWalletDirectory()
    {
        return BLOCKCHAIN_WLT_DB;
    }

    public String getUniquePeerLink()
    {
        return UNIQUE_PEER_LINK;
    }

    public Config(File config)
    {
        String root = ".//";
        self = this;

        if(config != null && config.length() > 0)
            root = config.toString();

        try{
            BufferedReader reader = new BufferedReader(new FileReader(new File(root + "//rivercoin.config")));

            String line     = "";
            String conf     = "";

            while((line = reader.readLine()) != null)
                conf  += line + "\n";

            String parse[] = conf.split("\n");

            String structure = "";

            for(String string : parse)
            {
                if(string.startsWith("PORT:"))                          this.PORT = Integer.parseInt(string.substring(5));
                else if(string.startsWith("MAX_CPU_THREADS:"))          this.MAX_MINING_THREADS = Integer.parseInt(string.substring("MAX_CPU_THREADS:".length()));
                else if(string.startsWith("STRUCTURE_DIRECTORY"))       structure = string.substring("STRUCTURE_DIRECTORY:".length()).replace(".", root);
                else if(string.startsWith("BLOCKCHAIN_DIRECTORY:"))     this.BLOCKCHAIN_DIRECTORY = string.substring("BLOCKCHAIN_DIRECTORY:".length()).replace(".", root);
                else if(string.startsWith("BLOCKCHAIN_TRX_DB:"))        this.BLOCKCHAIN_TRX_DB = string.substring("BLOCKCHAIN_TRX_DB:".length()).replace(".", root);
                else if(string.startsWith("BLOCKCHAIN_WLT_DB:"))        this.BLOCKCHAIN_WLT_DB = string.substring("BLOCKCHAIN_WLT_DB:".length()).replace(".", root);
                else if(string.startsWith("WALLET_DIRECTORY:"))         this.WALLET_DIRECTORY = string.substring("WALLET_DIRECTORY:".length()).replace(".", root);
                else if(string.startsWith("PEER_FINDER:"))              this.UNIQUE_PEER_LINK = string.substring("PEER_FINDER:".length());
                else if(string.startsWith("PUBLIC_KEY:"))               this.PUBLIC_ADDRESS = string.substring("PUBLIC_KEY:".length());
                else if(string.startsWith("DBKEY:"))                    this.PUBLIC_ADDRESS = string.substring("PUBLIC_KEY:".length());
                else if(string.startsWith("BOOL_PRUNING"))              this.PRUNE = Boolean.parseBoolean(string.substring("BOOL_PRUNING".length()));
            }

            this.BLOCKCHAIN_DIRECTORY += File.separator;
            this.BLOCKCHAIN_TRX_DB += File.separator;
            this.BLOCKCHAIN_WLT_DB += File.separator;
            this.WALLET_DIRECTORY += File.separator;
            this.BLOCK_CHAIN_DB = structure + File.separator + "database";

            Logger.alert("blockchain: "   + BLOCKCHAIN_DIRECTORY);
            Logger.alert("wallet: "       + BLOCKCHAIN_WLT_DB);
            Logger.alert("transaction: "  + BLOCKCHAIN_TRX_DB);

            LatestBlockInfo info = new LatestBlockInfo(this);
            info.read();

            this.CURRENT_TARGET = info.getDifficulty();

            if(this.PUBLIC_ADDRESS == null)
            {
                Logger.err("No address to send mining funds to! please provide your PUBLIC wallet address.");
                System.exit(0);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public long getAverageBlockTime()
    {
        /** two minutes **/
        return 120_000L;
    }

    /** calculate the cost of a contract **/
    public static String getCost(long contractSize)
    {
        return new BigDecimal(getReward()).divide(new BigDecimal(3000), 20, RoundingMode.HALF_UP).multiply(new BigDecimal(contractSize)).toPlainString();
    }

    public static String getReward()
    {
        LatestBlockInfo info = new LatestBlockInfo(self);
        try
        {
            info.read();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        long latestBlock =  info.getLatestBlock();
        long halfEvery   =  20000;

        BigDecimal decimal = new BigDecimal("50").divide(new BigDecimal(latestBlock).divide(new BigDecimal(halfEvery), 20, RoundingMode.HALF_DOWN).round(MathContext.DECIMAL128).multiply(new BigDecimal(2)), 20, RoundingMode.HALF_DOWN);

        return new RiverCoin(decimal).toRiverCoinString();
    }

    public static BigInteger getMinimumDifficulty()
    {
        return MINIMUM_TARGET_DIFFICULTY;
    }

    private static final BigInteger MINIMUM_TARGET_DIFFICULTY = new BigDecimal("225269536353234632640832032722171634457188848844000484574312395358531977087").toBigInteger();

    public PublicAddress getMinerAddress() {
        return null;
    }

    public Wallet getWallet() {
        return null;
    }

    public BigInteger getCurrentDifficulty() {
        return CURRENT_TARGET;
    }
}
