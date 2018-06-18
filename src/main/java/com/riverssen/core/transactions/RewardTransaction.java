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

import com.riverssen.core.FullBlock;
import com.riverssen.core.RiverCoin;
import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.security.CompressedAddress;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.core.system.Config;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.core.utils.SmartDataTransferer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class RewardTransaction implements TransactionI
{
    private PublicAddress       receiver;
    private long                time;
    private TXIList             txids;

    public RewardTransaction(DataInputStream stream) throws IOException, Exception
    {
        receiver = new PublicAddress(ByteUtil.read(stream, 20));
        time     = stream.readLong();
        txids    = new TXIList(stream);
    }

    public RewardTransaction(PublicAddress receiver, FullBlock block)
    {
        this.receiver = receiver;
        this.time     = System.currentTimeMillis();
        this.txids    = new TXIList();

        this.txids.add(new TransactionInput(new TransactionOutput(receiver, new RiverCoin(Config.getReward()), block.getHeader().getMerkleRoot())));
    }

    @Override
    public boolean valid(ContextI context)
    {
        return txids.getInputAmount().equals(new BigInteger(Config.getReward()));
    }

    @Override
    public long getTimeStamp() {
        return time;
    }

    @Override
    public CompressedAddress getSender() {
        return null;
    }

    @Override
    public PublicAddress getReceiver() {
        return receiver;
    }

    @Override
    public TXIList getTXIDs() {
        return txids;
    }

    @Deprecated
    public List<TransactionOutput> generateOutputs()
    {
        return generateOutputs(null, null);
    }

    public List<TransactionOutput> generateOutputs(PublicAddress miner, ContextI context)
    {
        List<TransactionOutput> utxos = new ArrayList<>();

        utxos.add(new TransactionOutput(receiver, new RiverCoin(getInputAmount()), encode(ByteUtil.defaultEncoder())));

        return utxos;
    }

    @Override
    public RiverCoin cost() {
        return new RiverCoin("0");
    }

    @Override
    public BigInteger getInputAmount() {
        BigInteger amount = BigInteger.ZERO;

        for(TransactionInput txi : txids)
            amount = amount.add(((txi.getUTXO()).getValue().toBigInteger()));

        return amount;
    }

    @Override
    public void revertOutputs(PublicAddress miner, ContextI context)
    {
    }

    @Override
    public String toJSON() {
        return "{"+
                jsonLine("type", "reward") + comma() +
                jsonLine("miner", getReceiver().toString()) + comma() +
                jsonLine("time", getTimeStamp() + "")
            + "}";
    }

    @Override
    public byte[] getBytes() {
        return ByteUtil.concatenate(receiver.getBytes(), getTXIDs().getBytes(), ByteUtil.encode(time));
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
    public void export(DataOutputStream dost)
    {
        try{
            dost.write(REWARD);
            getSender().export(dost);
            dost.writeLong(time);
            txids.export(dost);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
