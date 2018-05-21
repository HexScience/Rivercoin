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

package com.riverssen.core.transactions;

import com.riverssen.core.RiverCoin;
import com.riverssen.core.headers.TransactionInputI;
import com.riverssen.core.security.CompressedAddress;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.utils.Base58;
import com.riverssen.utils.ByteUtil;

import java.util.List;

public class Transaction
{
    /** 64 byte compressed ecdsa public key **/
    private CompressedAddress       sender;
    /** 20 byte receiver public address **/
    private PublicAddress           receiver;
    /** list containing the type and amount of goods to be transferred **/
    private TransactionOutput       goods;
    /** 256 byte comment in UTF format **/
    private byte                    data[];
    /** 140 byte signature **/
    private byte                    signature[];
    /** 8 byte 'honest' typestamp **/
    private byte                    timestamp[];

    public boolean valid()
    {
        if (sender.toPublicKey() == null) return false;

        if (!sender.toPublicKey().isValid()) return false;

//        if(amount.toBigInteger().compareTo(BigInteger.ZERO) <= 0) return false;

        return sender.toPublicKey().verifySignature(generateSignatureData(sender, receiver, goods, data, timestamp), Base58.encode(signature));
    }


    public static byte[] generateSignatureData(CompressedAddress sender, PublicAddress receiver, TransactionOutput goods, String comment, int nonce, long timestamp)
    {
        return ByteUtil.concatenate(sender.getBytes(), receiver.getBytes(), amount.getBytes(), comment.getBytes(), ByteUtil.encodei(nonce), ByteUtil.encode(timestamp));
    }

    public static byte[] generateSignatureData(CompressedAddress sender, PublicAddress receiver, RiverCoin amount, byte comment[], byte nonce[], byte timestamp[])
    {
        return ByteUtil.concatenate(sender.getBytes(), receiver.getBytes(), amount.getBytes(), comment, nonce, timestamp);
    }
}
