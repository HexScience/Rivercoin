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

import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.security.CompressedAddress;
import com.riverssen.core.security.PrivKey;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.core.transactions.UTXO;
import com.riverssen.utils.Base58;
import com.riverssen.utils.ByteUtil;
import com.riverssen.core.headers.Encodeable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;

public class Transaction implements Encodeable, TransactionI
{
    public static final short TYPE = 0;

    /** 64 byte compressed ecdsa public key **/
    private CompressedAddress sender;
    /** 20 byte receiver public address **/
    private PublicAddress     receiver;
    /** 13 byte amount of transaction **/
    private RiverCoin         amount;
    /** 256 byte comment in UTF format **/
    private byte              data[];
    /** 140 byte signature **/
    private byte              signature[];
    /** 4 byte nonce **/
    private byte              nonce[];
    /** 8 byte timestamp **/
    private byte              timestamp[];

    public Transaction(CompressedAddress sender, PublicAddress receiver, RiverCoin amount, String comment, int nonce, long timestamp, PrivKey key)
    {
        this(sender, receiver, amount, comment, nonce, timestamp, key.signEncoded(generateSignatureData(sender, receiver, amount, comment, nonce, timestamp)));
    }

    public Transaction(DataInputStream stream)
    {
        this(   new CompressedAddress(Base58.encode(ByteUtil.read(stream, 50))),
                new PublicAddress(Base58.encode(ByteUtil.read(stream, 20))),
                new RiverCoin(ByteUtil.read(stream, RiverCoin.MAX_BYTES)),
                new String(ByteUtil.read(stream, 256)),
                ByteUtil.decodei(ByteUtil.read(stream, 4)),
                ByteUtil.decode(ByteUtil.read(stream, 8)),
                ByteUtil.read(stream, 140));
    }

    public Transaction(CompressedAddress sender, PublicAddress receiver, RiverCoin amount, String comment, int nonce, long timestamp, byte signature[])
    {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.data = comment.getBytes();
        this.nonce = ByteUtil.encodei(nonce);
        this.signature = signature;
    }

    @Override
    public byte[] getBytes()
    {
        return ByteUtil.concatenate(sender.getBytes(), receiver.getBytes(), amount.getBytes(), data, signature);
    }

    @Override
    public boolean valid()
    {
        if(sender.toPublicKey() == null) return false;

        if(!sender.toPublicKey().isValid()) return false;

        if(amount.toBigInteger().compareTo(BigInteger.ZERO) <= 0) return false;

        return sender.toPublicKey().verifySignature(generateSignatureData(sender, receiver, amount, data, nonce, timestamp), Base58.encode(signature));
    }

    @Override
    public void write(DataOutputStream stream) throws IOException
    {
        stream.writeShort(TYPE);
        stream.write(getBytes());
    }

    @Override
    public long getTimeStamp()
    {
        return ByteUtil.decode(timestamp);
    }

    @Override
    public CompressedAddress getSender()
    {
        return sender;
    }

    @Override
    public PublicAddress getReceiver()
    {
        return receiver;
    }

    @Override
    public int getNonce()
    {
        return ByteUtil.decodei(nonce);
    }

    @Override
    public RiverCoin getAmount()
    {
        return amount;
    }

    @Override
    public Collection<? extends UTXO> getTXIDs() {
        return null;
    }

    @Override
    public boolean matches(Class<?> t) {
        return false;
    }

    public static byte[] generateSignatureData(CompressedAddress sender, PublicAddress receiver, RiverCoin amount, String comment, int nonce, long timestamp)
    {
        return ByteUtil.concatenate(sender.getBytes(), receiver.getBytes(), amount.getBytes(), comment.getBytes(), ByteUtil.encodei(nonce), ByteUtil.encode(timestamp));
    }

    public static byte[] generateSignatureData(CompressedAddress sender, PublicAddress receiver, RiverCoin amount, byte comment[], byte nonce[], byte timestamp[])
    {
        return ByteUtil.concatenate(sender.getBytes(), receiver.getBytes(), amount.getBytes(), comment, nonce, timestamp);
    }

    @Override
    public String toJSON() {
        return "{"+
                jsonLine("type", "transaction")+comma()
                +jsonLine("sender", getSender().toPublicKey().getAddress().toString())+comma()
                + jsonLine("receiver", receiver.toString())+
                comma() + jsonLine("amount", amount.toRiverCoinString())+
                comma() + jsonLine("time", timestamp + "")+comma()
                + jsonLine("signature", Base58.encode(signature))
                +"}";
    }
}