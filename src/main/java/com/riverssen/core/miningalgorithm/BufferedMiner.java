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

package com.riverssen.core.miningalgorithm;

import com.riverssen.core.headers.ContextI;
import com.riverssen.core.headers.HashAlgorithm;
import com.riverssen.core.system.FileSpec;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.core.utils.Tuple;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;

public class BufferedMiner
{
    private final byte buffer[];

    public BufferedMiner(ContextI context)
    {
        int mod = Math.max(1, (int)(context.getBlockChain().currentBlock() / 262_800L));
        //0.5 Gb buffer / 262800 blocks. (every year an incease of 0.5Gb) memory needed.
        buffer = new byte[524_288 * mod];
    }

    public Tuple<byte[], Long> mine(HashAlgorithm algorithm, BigInteger difficulty, ContextI context) throws IOException {
        BigInteger result = BigInteger.ZERO;

        FileSpec ledger = context.getLedger();
        DataInputStream stream = null;

        if(ledger.length() > 0)
            stream = ledger.next().asInputStream();

        while (ledger.remaining() > 0)
        {
            int cod = stream.read(buffer);

            if(cod == 1)
                stream = ledger.next().asInputStream();

            result = new BigInteger(algorithm.encode(buffer));
        }

        long nonce = 0;

        {
            byte data[] = ByteUtil.encode(nonce);

            for(int i = 0; i < data.length; i ++)
                buffer[i] = data[i];

            result = new BigInteger(algorithm.encode(buffer));
        }

        while (result.compareTo(difficulty) >= 0)
        {
            nonce ++;

            byte data[] = ByteUtil.encode(nonce);

            for(int i = 0; i < data.length; i ++)
                buffer[i] = data[i];

            result = new BigInteger(algorithm.encode(buffer));
        }

        return new Tuple<>(result.toByteArray(), nonce);
    }
}
