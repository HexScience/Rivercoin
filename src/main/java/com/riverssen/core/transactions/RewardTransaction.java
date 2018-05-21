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
import com.riverssen.core.FullBlock;
import com.riverssen.core.RiverCoin;
import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.security.CompressedAddress;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.utils.ByteUtil;
import com.riverssen.utils.SmartDataTransferer;

import java.io.DataOutputStream;
import java.math.BigInteger;

public class RewardTransaction implements TransactionI
{
    private PublicAddress       receiver;
    private long                time;
    private TXIList             txids;

    public RewardTransaction(PublicAddress receiver, FullBlock block, TXIList feeInputs)
    {
        this.receiver = receiver;
        this.time     = System.currentTimeMillis();
        this.txids    = new TXIList();

        this.txids.addAll(feeInputs);
        this.txids.add(new TransactionInput(new RiverCoin(Config.getReward())));
    }

    @Override
    public boolean valid()
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

    @Override
    public boolean matches(short type) {
        return false;
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
    public void export(DataOutputStream dost) {
    }
}
