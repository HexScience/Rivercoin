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

import com.riverssen.core.compiler.LexedProgram;
import com.riverssen.core.compiler.LexicalToken;
import com.riverssen.core.rvm.VirtualMachine;
import com.riverssen.core.system.Context;
import com.riverssen.utils.ByteUtil;
import com.riverssen.utils.FileUtils;

import java.io.File;
import java.security.Security;
import java.util.Set;

public class RivercoinCore
{
    public static void main(String args[]) throws Exception
    {
        String root = "";

        if(args != null && args.length > 0)
            root = args[0];
        else throw new RuntimeException("Please specify a rivercoin.config file.");

        new RivercoinCore(root);
    }

    private RivercoinCore(String file)
    {
        Set<LexicalToken> list = new LexedProgram(FileUtils.readUTF(VirtualMachine.class.getResourceAsStream("SampleContract.mpp"))).getTokens();

        for(LexicalToken token : list)
        {
            System.out.println(token);
        }

        System.exit(0);

        /** create a context **/
        Context context = new Context(new File(file));

        /**
         * Add the bouncy castle provider
         */
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
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