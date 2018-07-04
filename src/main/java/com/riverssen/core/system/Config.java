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

import com.riverssen.core.RiverCoin;
import com.riverssen.core.block.BlockHeader;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.core.security.Wallet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.BigInteger;
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
    private long        VSSSIZE;
    private String      VSSFILE;
    private static Config self;

    public static String getMiningFee()
    {
        return "0.0000015";
    }
    public static String getByteFee()
    {
        return "0.000000375";
    }
    public static String getFirstReward() { return "50"; };

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

        if(config != null && config.toString().length() > 0)
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
                else if(string.startsWith("STRUCTURE_DIRECTORY"))       structure = string.substring("STRUCTURE_DIRECTORY:".length()).replace(".", root + File.separator);
                else if(string.startsWith("BLOCKCHAIN_DIRECTORY:"))     this.BLOCKCHAIN_DIRECTORY = string.substring("BLOCKCHAIN_DIRECTORY:".length()).replace(".", root + File.separator);
                else if(string.startsWith("BLOCKCHAIN_TRX_DB:"))        this.BLOCKCHAIN_TRX_DB = string.substring("BLOCKCHAIN_TRX_DB:".length()).replace(".", root + File.separator);
                else if(string.startsWith("BLOCKCHAIN_WLT_DB:"))        this.BLOCKCHAIN_WLT_DB = string.substring("BLOCKCHAIN_WLT_DB:".length()).replace(".", root + File.separator);
                else if(string.startsWith("WALLET_DIRECTORY:"))         this.WALLET_DIRECTORY = string.substring("WALLET_DIRECTORY:".length()).replace(".", root + File.separator);
                else if(string.startsWith("PEER_FINDER:"))              this.UNIQUE_PEER_LINK = string.substring("PEER_FINDER:".length());
                else if(string.startsWith("PUBLIC_KEY:"))               this.PUBLIC_ADDRESS = string.substring("PUBLIC_KEY:".length());
                else if(string.startsWith("DBKEY:"))                    this.PUBLIC_ADDRESS = string.substring("PUBLIC_KEY:".length());
                else if(string.startsWith("BOOL_PRUNING:"))              this.PRUNE = Boolean.parseBoolean(string.substring("BOOL_PRUNING".length()));
                else if(string.startsWith("VIRTUAL_SPACE_SIZE:"))        this.VSSSIZE = Long.parseLong(string.substring("VIRTUAL_SPACE_SIZE:".length()));
                else if(string.startsWith("VIRTUAL_SPACE_FILE:"))        this.VSSFILE = (string.substring("VIRTUAL_SPACE_FILE:".length()).replace(".", root + File.separator));
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
        return new BigDecimal(contractSize).multiply(new BigDecimal(getByteFee())).toPlainString();
    }

    public static RiverCoin totalCoinsLeft()
    {
        LatestBlockInfo info = new LatestBlockInfo(self);
        try {
            info.read();

            long current = info.getLatestBlock() + 1;
            long halfEvery = getHalfEvery();

            RiverCoin max = new RiverCoin(RiverCoin.MAX_RIVERCOINS);
            RiverCoin cur = new RiverCoin(getFirstReward());

            while (current > halfEvery)
            {
                current -= halfEvery;
                max = max.sub(cur.mul(new RiverCoin(halfEvery + "")));
                cur = cur.div(new RiverCoin("2.0"));
            }

            if(current > 0 && current < halfEvery)
                max = max.sub(cur.mul(new RiverCoin(halfEvery + "")));

            return max;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new RiverCoin(RiverCoin.MAX_RIVERCOINS);
    }

    public static long getHalfEvery()
    {
        return 500_000L;
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

        /** blocks start at 0 **/
        long latestBlock =  info.getLatestBlock() + 1L;
        long halfEvery   =  getHalfEvery();

        if(latestBlock <= 0) return new RiverCoin("50.0").toRiverCoinString();

        BigDecimal decimal = new BigDecimal(getFirstReward());

        long numDivisions = latestBlock / halfEvery;

        for(int i = 0; i < numDivisions; i ++)
            decimal = decimal.divide(new BigDecimal(2), 200, RoundingMode.HALF_UP);

        return new RiverCoin(decimal).toRiverCoinString();
    }

    public static BigInteger getMinimumDifficulty()
    {
        return MINIMUM_TARGET_DIFFICULTY;
    }

//    private static final BigInteger MINIMUM_TARGET_DIFFICULTY = new BigDecimal("225269536353234632640832032722171634457188848844000484574312395358531977087").toBigInteger();
    private static final BigInteger MINIMUM_TARGET_DIFFICULTY = new BigDecimal("225269536353234632640832032722171634457188848844000484574312395358531977087").toBigInteger();
    private static final BigInteger MAXIMUM_TARGET_DIFFICULTY = new BigDecimal("2252695363532346326408320327221716344571888488").toBigInteger();

    public PublicAddress getMinerAddress() {
        return new PublicAddress(PUBLIC_ADDRESS);
    }

    public Wallet getWallet() {
        return null;
    }

    public BigInteger getMinimumTargetDifficulty()
    {
        return MINIMUM_TARGET_DIFFICULTY;
    }

    public BigInteger getCurrentDifficulty(BlockHeader lastBlock, BlockHeader lastOneHundred) {
        if(lastBlock == null) return MINIMUM_TARGET_DIFFICULTY;
        BigInteger min = MINIMUM_TARGET_DIFFICULTY;
        BigInteger cur = lastBlock.getDifficultyAsInt();

        BigDecimal tdf = new BigDecimal((lastBlock.getTimeStampAsLong() - lastOneHundred.getTimeStampAsLong()));
        if(tdf.compareTo(BigDecimal.ZERO) == 0) tdf = BigDecimal.ONE;
        BigDecimal tph = new BigDecimal(Math.max(lastBlock.getNonce(), 1)).divide(tdf, 200, RoundingMode.HALF_DOWN);

        BigDecimal correctTimePerBlock = new BigDecimal(getAverageBlockTime()).multiply(new BigDecimal(0.25));

        BigDecimal adjustedTimePerBlock= tdf.divide(correctTimePerBlock, 200, RoundingMode.HALF_DOWN);

        BigDecimal adjustedHashPerBlock= tph.multiply(adjustedTimePerBlock).multiply(new BigDecimal(10.0));

        BigInteger result = cur;

//        System.out.println(adjustedHashPerBlock);

        try{
            result = new BigDecimal(cur).divide(new BigDecimal("7.5").multiply(adjustedHashPerBlock), 200, RoundingMode.HALF_UP).toBigInteger();
        } catch (Exception e)
        {
        }

        if(result.compareTo(MINIMUM_TARGET_DIFFICULTY) > 0)
            return MINIMUM_TARGET_DIFFICULTY;

        if(result.compareTo(MAXIMUM_TARGET_DIFFICULTY) < 0)
            return MAXIMUM_TARGET_DIFFICULTY;


        return result;//MINIMUM_TARGET_DIFFICULTY;//new BigDecimal(cur).multiply(adjustedTimePerBlock.multiply(new BigDecimal(100.0))).toBigInteger();
    }

    public long getVSSSize()
    {
        return VSSSIZE;
    }

    public String getVSSDirectory()
    {
        return VSSFILE;
    }
}
