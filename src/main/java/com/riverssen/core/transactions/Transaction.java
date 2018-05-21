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

import com.riverssen.core.Config;
import com.riverssen.core.RiverCoin;
import com.riverssen.core.headers.Encodeable;
import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.security.CompressedAddress;
import com.riverssen.core.security.PrivKey;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.utils.Base58;
import com.riverssen.utils.ByteUtil;
import com.riverssen.utils.SmartDataTransferer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Transaction implements TransactionI, Encodeable
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
    /** var size byte signature **/
    private byte                                signature[];
    /** 8 byte 'honest' typestamp **/
    private byte                                timestamp[];

    public

    Transaction(DataInputStream stream) throws IOException, Exception
    {
        sender      = new CompressedAddress(stream);
        receiver    = new PublicAddress(ByteUtil.read(stream, 20));
        txids       = new TXIList(stream);
        amount      = RiverCoin.fromStream(stream);
        data        = ByteUtil.read(stream, 256);
        signature   = ByteUtil.read(stream, stream.read());
        timestamp   = ByteUtil.read(stream, 8);
    }

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

        for(TransactionInput input : txids) if(!input.getUTXO().getOwner().equals(sender)) return false;

        if (amount.toBigInteger().compareTo(BigInteger.ZERO) <= 0) return false;

        /** check utxo amount is more than transaction amount **/
        if (amount.toBigInteger().compareTo(getInputAmount()) > 0) return false;
        /** check utxo amount is contains a transaction fee **/
        if (amount.toBigInteger().add(new BigDecimal(amount.toBigInteger()).multiply(new BigDecimal(Config.getConfig().MINING_FEE)).toBigInteger()).compareTo(getInputAmount()) >= 0) return false;

        if(amount.toBigInteger().compareTo(new BigInteger(Config.getConfig().MINIMUM_TRANSACTION_AMOUNT)) < 0) return false;

        return sender.toPublicKey().verifySignature(generateSignatureData(), Base58.encode(signature));
    }

    @Override
    public long getTimeStamp() {
        return ByteUtil.decode(timestamp);
    }

    @Override
    public CompressedAddress getSender() {
        return sender;
    }

    @Override
    public PublicAddress getReceiver() {
        return receiver;
    }

    @Override
    public TXIList getTXIDs() {
        return txids;
    }

    /** read all transaction inputs and return a rivercoin value **/
    public BigInteger getInputAmount()
    {
        BigInteger amount = BigInteger.ZERO;

        for(TransactionInput txi : txids)
            amount = amount.add(((txi.getUTXO()).getValue().toBigInteger()));

        return amount;
    }

    @Deprecated
    public List<TransactionOutput> getOutputs()
    {
        return getOutputs(null);
    }

    public List<TransactionOutput> getOutputs(PublicAddress miner)
    {
        List<TransactionOutput> utxos = new ArrayList<>();

        utxos.add(new TransactionOutput(receiver, amount, encode(ByteUtil.defaultEncoder())));

        return utxos;
    }

    @Override
    public RiverCoin cost() {
        return new RiverCoin("0");
    }

    public byte[] generateSignatureData()
    {
        return generateSignatureData(sender, receiver, amount, txids, data, timestamp);
    }


    public static byte[] generateSignatureData(CompressedAddress sender, PublicAddress receiver, RiverCoin amount, TXIList txilist, byte comment[], byte timestamp[])
    {
        return ByteUtil.concatenate(sender.getBytes(), receiver.getBytes(), amount.getBytes(), txilist.getBytes(), comment, timestamp);
    }

    @Override
    public byte[] getBytes() {
        return ByteUtil.concatenate(sender.getBytes(),
                                    receiver.getBytes(),
                                    txids.getBytes(),
                                    amount.getBytes(),
                                    data,
                                    timestamp);
    }

    @Override
    public byte[] header() {
        return new byte[0];
    }

    @Override
    public byte[] content() {
        return new byte[0];
    }

    @Override
    public void export(SmartDataTransferer smdt) {

    }

    @Override
    public void export(DataOutputStream dost) {

    }

    @Override
    public String toJSON() {
        return null;
    }
}
