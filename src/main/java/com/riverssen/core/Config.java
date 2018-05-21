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

import com.riverssen.core.system.LatestBlockInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;

public class Config
{
    private static Config config;
    private boolean PRUNE;

    /** max blocl size in bytes **/
    public final int MAX_BLOCK_SIZE           = 5_000_000;
    /** The mining fee is a constant, any mined solutions taking more than the specified amount should be discarded **/
    public final String MINING_FEE            = "0.00025";
    public int PORT                           = 5110;
    public int MAX_MINING_THREADS             = 2;
    public String REWARD_PER_BLOCK_MINED      = "50.0";

    public int    TOKENS_PER_BLOCK_TOMNE      = 2999;
//    public final int    TOKEN_MINING_DIFFICULTY     = 1;
    public int    BLOCK_MINING_DIFFICULTY     = 1;
    public long   ALERT_TIME                  = 6000;
//    public final long   REWARD_PER_TOKEN_MINED      = (long) (RiverCoin.NANO_COIN_TO_RVC * (REWARD_PER_BLOCK_MINED / (double)TOKENS_PER_BLOCK_TOMNE));
//    public final String BLOCKCHAIN_DIRECTORY        = ".//structure//";
    public String BLOCKCHAIN_DIRECTORY        = ".//structure//chain//";
    public String BLOCKCHAIN_TRX_DB           = ".//structure//db_tx//";
    public String BLOCKCHAIN_WLT_DB           = ".//structure//db_wd//";
    public String BLOCKCHAIN_DIRECTORY_TEMP   = ".//structure//chain//";
    public String BLOCKCHAIN_TRX_DB_TEMP      = ".//structure//db_tx//";
    public String BLOCKCHAIN_WLT_DB_TEMP      = ".//structure//db_wd//";

    public String BLOCK_CHAIN_DB              = "";
    public String WALLET_DIRECTORY            = ".//other//";
    public String WALLET_DIRECTORY_TEMP       = ".//other//";
    public String UNIQUE_PEER_LINK            = "http://";
    public String DEVELOPER_KEY               = "null";
    public String MINING_KEY                  = "null";
    public String PUBLIC_ADDRESS              = null;
    public final BigDecimal MINIMUM_TARGET_DIFFICULTY = new BigDecimal("225269536353234632640832032722171634457188848844000484574312395358531977087");
    public BigDecimal TARGET_DIFFICULTY       = new BigDecimal("225269536353234632640832032722171634457188848844000484574312395358531977087");
    public static final long DIFFICULTY_CHANGE_EVERY = 24;

    private static long difficulty = Long.MAX_VALUE;

    public static void create(String string)
    {
        Config.config = new Config(string);
    }

    public static Config getConfig()
    {
        return config;
    }

    public static void generateDifficulty()
    {
    }

    public static BigInteger miningDifficulty()
    {
        return null;
    }

    public static synchronized void calculateDifficulty() throws Exception
    {
        LatestBlockInfo info = new LatestBlockInfo();
        info.read();

        long latestBlock = info.getLatestBlock();
        long lastBlockCheck = info.getLastBlockCheck();
        long lastBlockCheckTimeStamp = info.getLastBlockCheckTimestamp();
        long numblocks = latestBlock - lastBlockCheck;

//        {
//            byte difficulty[] = new byte[31];
//            Arrays.fill(difficulty, Byte.MAX_VALUE);
//
//            System.out.println(new BigInteger(difficulty));
//        }

        if(numblocks < Config.DIFFICULTY_CHANGE_EVERY) return;

        BigInteger totalHashes = info.getTotalHashes();

        long timeDifference = System.currentTimeMillis() - lastBlockCheckTimeStamp;

        double averageTimePerBlock = ((double)timeDifference / (double)numblocks);

        long blockAmountToCheck = 12;
        long blockAmountMinimum = 8;

        double maxTimePerBlock = 35000;
        double averageTprBlock = 20000;
        double minTimePerBlock = 17000;

        long timeSpan   = 20_000;

        byte difficulty[] = new byte[32];
        Arrays.fill(difficulty, Byte.MAX_VALUE);
//        BigInteger target = new BigInteger(new BigInteger(difficulty).toString(16).substring(Config.getConfig().TARGET_DIFFICULTY.length()), 16);

        BigDecimal secondsPerBlock = (new BigDecimal(timeDifference)
                .divide(new BigDecimal(numblocks), 100, RoundingMode.HALF_UP)).divide(new BigDecimal(1),  100, RoundingMode.HALF_UP);

        System.out.println("seconds per block: " + secondsPerBlock.toPlainString());

        BigDecimal hashesPerBlock  = (new BigDecimal(totalHashes))
                .divide(new BigDecimal(numblocks), 100, RoundingMode.HALF_UP);

        System.out.println("hashes per block: " + hashesPerBlock.toPlainString());

        BigDecimal secondsPerHash  = secondsPerBlock.divide(hashesPerBlock, 100, RoundingMode.HALF_UP);

        System.out.println("seconds per hash: " + secondsPerHash.toPlainString());

        BigDecimal factor          = new BigDecimal(20).divide(secondsPerHash, 100, RoundingMode.HALF_UP);

        BigDecimal oldDifficulty = Config.getConfig().TARGET_DIFFICULTY;

        Config.getConfig().TARGET_DIFFICULTY = Config.getConfig().MINIMUM_TARGET_DIFFICULTY.divide(factor, 100, RoundingMode.HALF_UP);

        System.out.println("factor: " + factor.toPlainString());
        System.out.println("old difficulty: " + oldDifficulty.toPlainString());
        System.out.println("new difficulty: " + Config.getConfig().TARGET_DIFFICULTY.toBigInteger());

        if(Config.getConfig().TARGET_DIFFICULTY.compareTo(Config.getConfig().MINIMUM_TARGET_DIFFICULTY) > 0) Config.getConfig().TARGET_DIFFICULTY = Config.getConfig().MINIMUM_TARGET_DIFFICULTY;

//        BigInteger target = new BigInteger(new BigInteger(difficulty).subtract(Config.getConfig().TARGET_DIFFICULTY.toBigInteger()).toString());

//        Config.getConfig().BLOCK_MINING_DIFFICULTY = Math.max(1, 64 - target.toString(16).length());

//        System.out.println(Config.getConfig().BLOCK_MINING_DIFFICULTY);

//        BigDecimal desiredHashesPerSecond = new BigDecimal(target).divide(secondsPerHash, 100, RoundingMode.HALF_UP);
//
//        System.out.println("desired hashes ps: " + desiredHashesPerSecond.toBigInteger().toString(16).length());

//        BigDecimal desiredHashesPerSecond = ;

//        System.out.println(desiredHashesPerSecond.toPlainString());

//        System.out.println(target.toString(16));
//        System.out.println(target.toString(16).length());

        info.read();
        info.write(info.getLatestBlock(), latestBlock, System.currentTimeMillis(), Config.getConfig().TARGET_DIFFICULTY, BigInteger.ZERO);

        long maxDesired = 12;

//        if(numblocks > blockAmountToCheck && (averageTimePerBlock > maxTimePerBlock || averageTimePerBlock < (double)blockAmountMinimum * minTimePerBlock))//733
//        if(numblocks > maxDesired && timeDifference > timeSpan*1.5)
//        {
//            Config.getConfig().BLOCK_MINING_DIFFICULTY = Math.max(1, --Config.getConfig().BLOCK_MINING_DIFFICULTY);
//
//            Logger.prt("difficulty changed to: " + Config.getConfig().BLOCK_MINING_DIFFICULTY);
//            Logger.prt("average block mine time: " + averageTimePerBlock);
//
//            info.read();
//            info.write(info.getLatestBlock(), latestBlock, System.currentTimeMillis(), getConfig().BLOCK_MINING_DIFFICULTY);
//        } else if(numblocks > maxDesired && timeDifference < timeSpan * 0.85)
//        {
//            Config.getConfig().BLOCK_MINING_DIFFICULTY = Math.min(10, ++Config.getConfig().BLOCK_MINING_DIFFICULTY);
//
//            Logger.prt("difficulty changed to: " + Config.getConfig().BLOCK_MINING_DIFFICULTY);
//            Logger.prt("average block mine time: " + averageTimePerBlock);
//
//            info.read();
//            info.write(info.getLatestBlock(), latestBlock, System.currentTimeMillis(), getConfig().BLOCK_MINING_DIFFICULTY);
//        }

//        {
////            double averageTimePerBlock = (double) (timeDifference / numblocks) / 30000.0;
//            double difficulty = ((double)(getConfig().BLOCK_MINING_DIFFICULTY) / (averageTimePerBlock / averageTprBlock));
//
//            getConfig().BLOCK_MINING_DIFFICULTY = (int) Math.max(1, Math.min(10, Math.round(difficulty)));
//
//            Logger.prt(difficulty > 1 ? Logger.COLOUR_RED : Logger.COLOUR_BLUE, "adjusted difficulty to " + getConfig().BLOCK_MINING_DIFFICULTY + " " + difficulty);
//            Logger.prt("average block mine time: " + averageTimePerBlock);
//
//            info.read();
//            info.write(info.getLatestBlock(), latestBlock, System.currentTimeMillis(), getConfig().BLOCK_MINING_DIFFICULTY);
//        }

//        long timeStamp = Block.getTimeStamp(new File());
    }

    private Config(String config)
    {
        Config.config = this;
        String root = ".//";

        if(config != null && config.length() > 0)
            root = config;

        try{
            BufferedReader reader = new BufferedReader(new FileReader(new File(root + "//rivercoin.config")));

            String line = "";
            config      = "";

            while((line = reader.readLine()) != null)
                config  += line + "\n";

            String parse[] = config.split("\n");

            String structure = "";

            for(String string : parse)
            {
                if(string.startsWith("PORT:")) this.PORT = Integer.parseInt(string.substring(5));
                else if(string.startsWith("MAX_CPU_THREADS:")) this.MAX_MINING_THREADS = Integer.parseInt(string.substring("MAX_CPU_THREADS:".length()));
                else if(string.startsWith("REWARD_PER_BLOCK:")) this.REWARD_PER_BLOCK_MINED = (string.substring("REWARD_PER_BLOCK:".length()));
                else if(string.startsWith("TOKENS_PER_BLOCK:")) this.TOKENS_PER_BLOCK_TOMNE = Integer.parseInt(string.substring("TOKENS_PER_BLOCK:".length()));
                else if(string.startsWith("ALERT_TIME:")) this.ALERT_TIME = Long.parseLong(string.substring("ALERT_TIME:".length()));
                else if(string.startsWith("STRUCTURE_DIRECTORY")) structure = string.substring("STRUCTURE_DIRECTORY:".length()).replace(".", root);
                else if(string.startsWith("BLOCKCHAIN_DIRECTORY:")) this.BLOCKCHAIN_DIRECTORY = string.substring("BLOCKCHAIN_DIRECTORY:".length()).replace(".", root);
                else if(string.startsWith("BLOCKCHAIN_TRX_DB:")) this.BLOCKCHAIN_TRX_DB = string.substring("BLOCKCHAIN_TRX_DB:".length()).replace(".", root);
                else if(string.startsWith("BLOCKCHAIN_WLT_DB:")) this.BLOCKCHAIN_WLT_DB = string.substring("BLOCKCHAIN_WLT_DB:".length()).replace(".", root);
                else if(string.startsWith("WALLET_DIRECTORY:")) this.WALLET_DIRECTORY = string.substring("WALLET_DIRECTORY:".length()).replace(".", root);
                else if(string.startsWith("PEER_FINDER:")) this.UNIQUE_PEER_LINK = string.substring("PEER_FINDER:".length());
                else if(string.startsWith("PUBLIC_KEY:")) this.PUBLIC_ADDRESS = string.substring("PUBLIC_KEY:".length());
                else if(string.startsWith("DBKEY:")) this.PUBLIC_ADDRESS = string.substring("PUBLIC_KEY:".length());
                else if(string.startsWith("BOOL_PRUNING")) this.PRUNE = Boolean.parseBoolean(string.substring("BOOL_PRUNING".length()));
            }

            this.BLOCKCHAIN_DIRECTORY += File.separator;
            this.BLOCKCHAIN_TRX_DB += File.separator;
            this.BLOCKCHAIN_WLT_DB += File.separator;
            this.WALLET_DIRECTORY += File.separator;
            this.BLOCK_CHAIN_DB = structure + File.separator + "database";

            Logger.alert("blockchain: "   + BLOCKCHAIN_DIRECTORY);
            Logger.alert("wallet: "       + BLOCKCHAIN_WLT_DB);
            Logger.alert("transaction: "  + BLOCKCHAIN_TRX_DB);

            LatestBlockInfo info = new LatestBlockInfo();
            info.read();

            this.TARGET_DIFFICULTY = info.getDifficulty();

            if(this.PUBLIC_ADDRESS == null)
            {
                Logger.err("no address to send mining funds to! please provide your PUBLIC wallet address");
                System.exit(0);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /** calculate the cost of a contract **/
    public static String getCost(long contractSize)
    {
        return new BigDecimal(getReward()).divide(new BigDecimal(getConfig().MAX_BLOCK_SIZE), 20, RoundingMode.HALF_UP).multiply(new BigDecimal(contractSize)).toPlainString();
    }

    public static String getReward()
    {
        LatestBlockInfo info = new LatestBlockInfo();
        try
        {
            info.read();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        long latestBlock =  info.getLatestBlock();
        long halfEvery   =  200;

        BigDecimal decimal = new BigDecimal("50").divide(new BigDecimal(latestBlock).divide(new BigDecimal(halfEvery), 20, RoundingMode.HALF_DOWN).round(MathContext.DECIMAL128).multiply(new BigDecimal(2)), 20, RoundingMode.HALF_DOWN);

        return new RiverCoin(decimal).toRiverCoinString();
    }
}
