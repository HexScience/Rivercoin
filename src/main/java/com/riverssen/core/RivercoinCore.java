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
import com.riverssen.core.exceptions.FeatureUnavailableException;
import com.riverssen.core.miningalgorithm.BufferedMiner;
import com.riverssen.core.system.*;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.core.utils.FileUtils;

import java.io.File;
import java.math.BigInteger;
import java.security.Security;

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

        RiverHash     hash  = new RiverHash();

        System.out.println(hash.encode58("2Wgd5szGrbJQePbpa4jR14zdrndbwCDCWAfRtWqKYfXH".getBytes()));

        context.run();
    }
}