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

import com.riverssen.core.algorithms.RiverHash;
import com.riverssen.core.block.BlockHeader;
import com.riverssen.core.block.FullBlock;
import com.riverssen.core.exceptions.FeatureUnavailableException;
import com.riverssen.core.miningalgorithm.BufferedMiner;
import com.riverssen.core.system.*;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.core.utils.FileUtils;
import com.riverssen.core.utils.HashUtil;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Security;
import java.util.Random;

public class RivercoinCore
{
    public static void main(String args[]) throws Exception
    {
        /**
         * Add the bouncy castle provider
         */
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        if(args != null && args.length > 1)
        new RivercoinCore(args[0], args[1], args);
        else throw new RuntimeException("Please specify a rivercoin.config file.");
    }

    public static final long actual_version = ByteUtil.decode(new byte[]{'a', 0, 0, 0, 0, 0, 0, (byte)228});

    private RivercoinCore(String type, String file, String ...args) throws Exception
    {
        /** This Code Starts The Rivercoin Client **/
        /** create a context **/
        ContextI context = null;
        Config   config  = new Config(new File(file + File.separator));

        /** Generate directories if they don't exist **/
        FileUtils.createDirectoryIfDoesntExist(config.getBlockChainDirectory());
        FileUtils.createDirectoryIfDoesntExist(config.getBlockChainTransactionDirectory());
        FileUtils.createDirectoryIfDoesntExist(config.getBlockChainWalletDirectory());
        FileUtils.createDirectoryIfDoesntExist(config.getVSSDirectory());
        Logger.alert("----------------------------------------------------------------");
        Logger.alert("--------------------Welcome To Rivercoin Core-------------------");
        Logger.alert("----------------------------------------------------------------");
        Logger.alert("----------usable cpu threads: " + config.getMaxMiningThreads());

        switch (type)
        {
                //** A Node Will Collect And Relay Information But Won't Get Into Mining **/
            case "node":
                context = new NodeContext(config);
                throw new FeatureUnavailableException("NodeContext");
                //** Miners Act As Nodes But They Attempt To Mine To Get A Reward**/
            case "miner":
                context = new MiningContext(config);
                break;
            case "client":
                context = new ClientContext(config);
                break;
        }

        BufferedMiner miner = new BufferedMiner(context);

        System.out.println(HashUtil.hashToStringBase16(miner.verify_v3("hello world my name jeff 1 ds sd fsd f ds f dsf ds f ds f dsf ds f sd".getBytes(), 120)));

        System.exit(0);

//        BufferedMiner miner = new BufferedMiner(context);

//        RiverHash     hash  = new RiverHash();
//        byte test[]         = new byte[1000000];
//        byte hfds[]         = new byte[1000000];

//        new Random(System.currentTimeMillis()).nextBytes(test);

//        long now = System.currentTimeMillis();

//        for(int i = 0; i < 24_686; i ++)
//            hfds = hash.encode(test);

//        BigInteger target = new BigDecimal(Config.getMinimumDifficulty()).divide(new BigDecimal(24_686), 200, BigDecimal.ROUND_HALF_UP).toBigInteger();
//        String difficultyHash = HashUtil.hashToStringBase16(target.toByteArray());
//        while (difficultyHash.length() < 64) difficultyHash = "0" + difficultyHash;
//        System.out.println(difficultyHash);
//
//        System.out.println(System.currentTimeMillis() - now);
//
//        System.exit(0);

        //Difficulty = At / Th
        //difficulty = 120_000 / 120

        Thread.sleep(1000);

        context.run();
    }
}