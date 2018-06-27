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

import java.io.DataInputStream;
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

    public byte[] mine(HashAlgorithm algorithm, ContextI context)
    {
        BigInteger result = BigInteger.ZERO;

//        DataInputStream stream = context.getLedger().asInputStream();

        return result.toByteArray();
    }
}
