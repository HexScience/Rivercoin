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

package com.riverssen.testing;

import com.riverssen.core.RiverCoin;
import com.riverssen.core.mpp.compiler.*;
import com.riverssen.core.mpp.objects.RSAPK;
import com.riverssen.core.mpp.runtime.StringObject;
import com.riverssen.core.security.Wallet;
import com.riverssen.core.utils.Base58;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.core.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.security.KeyPair;

public class Contracts
{
    public static void test() throws Exception
    {
        Wallet wallet       = new Wallet("dawdaw", "dawddawdwa");
        Wallet wallet2      = new Wallet("ddawdaw", "dadwddawdwa");
        /** Test Code For The Mocha++ Compiler **/
        ParsedProgram pp    = new ParsedProgram(new LexedProgram(FileUtils.readUTF(com.riverssen.core.mpp.contracts.Contracts.class.getResourceAsStream("contracts.mpp"))));
        Token list = pp.getTokens();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Namespace global = new Namespace(pp.getTokens());
        global.setGlobal();

        System.out.println(new RiverCoin(list.calculateCost()).toRiverCoinString());

        String firstStateHash = Base58.encode(global.getStateChange(ByteUtil.defaultEncoder()));
        ByteUtil.writeObject(stream, global);

        System.out.println(global.callMethod("set"));

        global.get("HelloWorld").setField("msg", new Message(wallet.getPublicKey().getAddress()));
        global.get("Messenger") .setField("msg", new Message(wallet.getPublicKey().getAddress()));

        global.callMethod("HelloWorld");
        global.get("HelloWorld").setField("msg", new Message(wallet.getPublicKey().getAddress()));
        global.get("HelloWorld").callMethod("setMessage", new StringObject("My name jeff."));

        KeyPair keyPair = com.riverssen.core.mpp.objects.RSA.buildKeyPair();

        System.out.println(global.get("HelloWorld").callMethod("getMessage"));
        global.get("Messenger").callMethod("Messenger");
        RSAPK k = new RSAPK(keyPair.getPublic());
        global.get("Messenger").callMethod("sendMessage", new StringObject("hello world"), k);
        global.get("Messenger").callMethod("sendMessage", new StringObject("helldo world"), k);
        System.out.println(global.get("Messenger").get("owner"));
        System.out.println(global.get("Messenger").get("messages"));

        String secondStateHash = Base58.encode(global.getStateChange(ByteUtil.defaultEncoder()));

        System.out.println(firstStateHash);
        System.out.println(secondStateHash);

        System.exit(0);
    }
}
