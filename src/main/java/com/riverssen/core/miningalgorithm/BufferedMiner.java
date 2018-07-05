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

import com.riverssen.core.algorithms.*;
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
    public static final HashAlgorithm keccak = new Keccak(), keccak2 = new Keccak(), keccak3 = new Keccak(), skein2 = new Skein_256_256(), skein3 = new Skein_512_512(), skein_4 = new Skein_1024_1024(), sha = new Sha1(), sha2 = new Sha256(), sha3 = new Sha3(), sha4 = new Sha4(), blake = new Blake(), gost = new Gost(), ripemd1 = new RipeMD128(), ripemd2 = new RipeMD160(), ripemd3 = new RipeMD256();
    private final byte buffer[];

    public BufferedMiner(ContextI context)
    {
        long bph = (60_000L * 60L) / context.getConfig().getAverageBlockTime();

        int mod = Math.max(1, (int)(context.getBlockChain().currentBlock() / (365 * 24 * bph)));
        //0.5 Gb buffer / 262800 blocks. (every year an incease of 0.5Gb) memory needed.
        buffer = new byte[524_288 * mod];
    }

    public static byte[] custom_hash(byte input[])
    {
        return skein2.encode(keccak.encode(blake.encode(gost.encode(sha3.encode(skein3.encode(sha2.encode(ripemd2.encode(sha4.encode(ripemd3.encode(keccak2.encode(keccak3.encode(skein_4.encode(input)))))))))))));
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
