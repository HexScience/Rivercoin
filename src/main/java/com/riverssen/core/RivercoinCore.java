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

import com.riverssen.core.mpp.compiler.*;
import com.riverssen.core.mpp.contracts.Contracts;
import com.riverssen.core.mpp.runtime.StringObject;
import com.riverssen.core.security.Wallet;
import com.riverssen.core.system.MiningContext;
import com.riverssen.core.headers.ContextI;
import com.riverssen.utils.FileUtils;

import java.io.File;
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
        new RivercoinCore(args[0], args[1]);
        else throw new RuntimeException("Please specify a rivercoin.config file.");
    }

    private RivercoinCore(String type, String file) throws Exception
    {
        Wallet wallet = new Wallet("dawdaw", "dawddawdwa");
        Wallet wallet2 = new Wallet("ddawdaw", "dadwddawdwa");
        /** Test Code For The Mocha++ Compiler **/
        ParsedProgram pp = new ParsedProgram(new LexedProgram(FileUtils.readUTF(Contracts.class.getResourceAsStream("contracts.mpp"))));
        Token list = pp.getTokens();

        Namespace global = new Namespace(pp.getTokens());

        global.get("HelloWorld").setField("msg", new Message(wallet.getPublicKey().getAddress()));

        global.get("HelloWorld").callMethod("HelloWorld");
        System.out.println(global.get("HelloWorld").get("owner"));
        global.get("HelloWorld").setField("msg", new Message(wallet.getPublicKey().getAddress()));
        global.get("HelloWorld").callMethod("setMessage", new StringObject("My name jeff."));
        System.out.println(global.get("HelloWorld").callMethod("getMessage"));

        System.exit(0);

        /** This Code Starts The Rivercoin Client **/

        /** create a context **/
        ContextI context = null;
        switch (type)
        {
            case "node": break;
            case "miner": context = new MiningContext(new File(file));
            case "wallet": break;
        }

        /** Generate directories if they don't exist **/
        FileUtils.createDirectoryIfDoesntExist(context.getConfig().getBlockChainDirectory());
        FileUtils.createDirectoryIfDoesntExist(context.getConfig().getBlockChainTransactionDirectory());
        FileUtils.createDirectoryIfDoesntExist(context.getConfig().getBlockChainWalletDirectory());
        FileUtils.createDirectoryIfDoesntExist(context.getConfig().getBlockChainDirectory() + File.separator + "temp");
        FileUtils.createDirectoryIfDoesntExist(context.getConfig().getBlockChainTransactionDirectory() + File.separator + "temp");
        FileUtils.createDirectoryIfDoesntExist(context.getConfig().getBlockChainWalletDirectory() + File.separator + "temp");
        Logger.alert("usable cpu threads: " + context.getConfig().getMaxMiningThreads());
        context.run();
    }
}