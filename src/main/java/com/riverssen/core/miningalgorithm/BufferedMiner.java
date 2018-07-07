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
import com.riverssen.core.security.AdvancedEncryptionStandard;
import com.riverssen.core.system.FileSpec;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.core.utils.HashUtil;
import com.riverssen.core.utils.Tuple;

import java.io.*;
import java.math.BigInteger;

public class BufferedMiner
{
    private static final HashAlgorithm   hash = new RiverHash();
    private final long                   buffer;
    private volatile long                nonce;
    private volatile byte[]              hash_;

    public BufferedMiner(ContextI context)
    {
        long bph = (60_000L * 60L) / context.getConfig().getAverageBlockTime();

        int mod = Math.max(1, (int)(context.getBlockChain().currentBlock() / (182.5 * 24 * bph)));

        //0.5 Gb buffer / 262800 blocks. (every half a year an incease of 0.5Gb) memory is needed.
        buffer = 524_288 * mod;

        nonce = -1;
    }

    public static byte[] custom_hash(byte input[])
    {
        return hash.encode(input);
    }

    private static byte[] pad(byte input[], int length)
    {
        if(input.length < length)
        {
            byte newinput[] = new byte[length];

            int i = 0;

            for(int j = 0; j < input.length; j ++)
                newinput[j] = input[i ++];

            while (i < length)
                newinput[i ++] = 0;
        }

        return ByteUtil.trim(input, 0, length);
    }

    private void mine(byte input[], ContextI context) throws Exception
    {
        nonce ++;

        verify(input, nonce, context);
    }

    public byte[] verify(byte input[], long nonce, ContextI context) throws Exception
    {
        int nonce_ = 0;

        byte input_hash[]               = custom_hash(ByteUtil.concatenate(input, custom_hash(ByteUtil.encode(nonce))));
        AdvancedEncryptionStandard aes  = new AdvancedEncryptionStandard(input_hash);

        byte encrypted_input[]          = aes.encrypt(input);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(encrypted_input);

        while(byteArrayOutputStream.size() < buffer)
        {
            input = ByteUtil.concatenate(
                    getInput(new BigInteger(custom_hash(ByteUtil.concatenate(input_hash, custom_hash(ByteUtil.encode(nonce_ ++ * nonce))))).abs().mod(new BigInteger(Math.max(1, context.getBlockChain().currentBlock()) + "").abs()).abs().longValue(), context), byteArrayOutputStream.toByteArray());

            input_hash                        = custom_hash(ByteUtil.concatenate(input_hash, input, custom_hash(ByteUtil.encode(nonce))));

            encrypted_input                   = aes.encrypt(input_hash, input);

            byteArrayOutputStream.write(encrypted_input);
        }

        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(aes.encrypt(byteArrayOutputStream.toByteArray()));

        hash_ = custom_hash(byteArrayOutputStream.toByteArray());
        byte prng[] = new byte[32];

        while (inputStream.available() > 0)
        {
            inputStream.read(prng);
            hash_ = custom_hash(ByteUtil.xor(hash_, prng));
        }

        inputStream.close();


//        hash_ = custom_hash(byteArrayOutputStream.toByteArray());
        String hash = HashUtil.hashToStringBase16(hash_);

        while (hash.length() < 64) hash = '0' + hash;

        hash_ = hexStringToByteArray(hash);

        return hash_;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

//
//    int nonce_ = 0;
//    nonce ++;
//
//    byte input_hash[]               = custom_hash(ByteUtil.concatenate(input, custom_hash(ByteUtil.encode(nonce))));
//    AdvancedEncryptionStandard aes  = new AdvancedEncryptionStandard(input_hash);
//
//    byte encrypted_input[]          = aes.encrypt(input);
//
//    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        byteArrayOutputStream.write(input);
//        byteArrayOutputStream.write(encrypted_input);
//
//        while(byteArrayOutputStream.size() < buffer)
//    {
////            input = ByteUtil.concatenate(
//////                    getInput(new BigInteger(custom_hash(ByteUtil.concatenate(input_hash, custom_hash(ByteUtil.encode(nonce_ ++ * nonce))))).mod(new BigInteger(Math.max(1, context.getBlockChain().currentBlock()) + "")).longValue(), context),
////                    byteArrayOutputStream.toByteArray(),
////                    encrypted_input,
////                    input_hash);
//
//        input_hash                        = custom_hash(byteArrayOutputStream.toByteArray());
//        byteArrayOutputStream.write(input_hash);
//        byteArrayOutputStream.write(custom_hash(byteArrayOutputStream.toByteArray()));
////                    ByteUtil.concatenate(input_hash,
////                            input,
////                            custom_hash(ByteUtil.encode(nonce))));
//
//        encrypted_input                   = aes.encrypt(input_hash, byteArrayOutputStream.toByteArray());
//
//        byteArrayOutputStream.write(encrypted_input);
//    }
//
//        byteArrayOutputStream.flush();
//        byteArrayOutputStream.close();
//
//    hash_ = custom_hash(byteArrayOutputStream.toByteArray());

    private byte[] getInput(long l, ContextI context) throws IOException
    {
        File file = new File(context.getConfig().getBlockChainDirectory() + File.separator + "block[" + l + "]");

        if(!file.exists()) return new byte[2560];

        byte input[] = new byte[(int) file.length()];

        DataInputStream stream = new DataInputStream(new FileInputStream(file));

        stream.read(input);

        stream.close();

        return input;
    }

    public Tuple<byte[], Long> mine(byte input[], BigInteger difficulty, ContextI context) throws Exception
    {
        mine(input, context);

        while (new BigInteger(hash_).abs().compareTo(difficulty) >= 0)
            mine(input, context);

        return new Tuple<>(hash_, nonce);
    }
}
