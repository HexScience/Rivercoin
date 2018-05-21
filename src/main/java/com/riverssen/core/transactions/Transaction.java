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
import com.riverssen.core.security.CompressedAddress;
import com.riverssen.core.security.PrivKey;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.utils.Base58;
import com.riverssen.utils.ByteUtil;

import java.math.BigInteger;

public class Transaction
{
    /** 64 byte compressed ecdsa public key **/
    private CompressedAddress                   sender;
    /** 20 byte receiver public address **/
    private PublicAddress                       receiver;
    /** list containing the type and amount of txids to be transferred **/
    private TXIList                             txids;
    /** amount of txids to be transferred **/
    private RiverCoin                           amount;
    /** 256 byte comment in UTF format **/
    private byte                                data[];
    /** 140 byte signature **/
    private byte                                signature[];
    /** 8 byte 'honest' typestamp **/
    private byte                                timestamp[];

    public Transaction(CompressedAddress        sender,
                      PublicAddress             receiver,
                      TXIList                   goods,
                      RiverCoin                 amount,
                      String                    comment,
                      long                      timestamp)
    {
        this.sender     = sender;
        this.receiver   = receiver;
        this.txids      = goods;
        this.amount     = amount;

        /** trim excess comment bytes **/
        if(comment.length() > 256) comment = comment.substring(0, 256);
        this.data       = comment.getBytes();
        this.timestamp  = ByteUtil.encode(timestamp);
    }

    /** sign transaction **/
    public void sign(PrivKey key)
    {
        byte[] bytes = generateSignatureData();

        this.signature = key.signEncoded(bytes);
    }

    public boolean valid()
    {
        if (sender.toPublicKey() == null) return false;

        if (!sender.toPublicKey().isValid()) return false;

        if (amount.toBigInteger().compareTo(BigInteger.ZERO) <= 0) return false;

        /** check utxo amount is more than transaction amount **/
        if (amount.toBigInteger().compareTo(getInputAmount()) > 0) return false;

        return sender.toPublicKey().verifySignature(generateSignatureData(), Base58.encode(signature));
    }

    /** read all transaction inputs and return a rivercoin value **/
    BigInteger getInputAmount()
    {
        BigInteger amount = BigInteger.ZERO;

        for(TransactionInput txi : txids)
            amount = amount.add(((TransactionOutput<RiverCoin>)txi.getUTXO()).getValue().toBigInteger());

        return amount;
    }

    public byte[] generateSignatureData()
    {
        return generateSignatureData(sender, receiver, amount, txids, data, timestamp);
    }


    public static byte[] generateSignatureData(CompressedAddress sender, PublicAddress receiver, RiverCoin amount, TXIList txilist, byte comment[], byte timestamp[])
    {
        return ByteUtil.concatenate(sender.getBytes(), receiver.getBytes(), amount.getBytes(), txilist.getBytes(), comment, timestamp);
    }
}
