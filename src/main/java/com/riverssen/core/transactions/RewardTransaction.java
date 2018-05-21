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
import com.riverssen.core.headers.TransactionInputI;
import com.riverssen.core.security.CompressedAddress;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.core.transactions.UTXO;
import com.riverssen.utils.ByteUtil;
import com.riverssen.utils.SmartDataTransferer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RewardTransaction implements TransactionI
{
    public static final short TYPE = 1;
    private PublicAddress       receiver;
    private long                time;
    private List<TransactionInputI> txids;

    public RewardTransaction(PublicAddress receiver, FullBlock block)
    {
        this.receiver = receiver;
        this.time     = System.currentTimeMillis();
        this.txids    = new ArrayList<>();

        this.txids.add(new UTXO<>(receiver, new RiverCoin(Config.getReward()), ByteUtil.concatenate(block.content(), receiver.getBytes())));
    }

    @Override
    public boolean valid() {
        return getAmount().toRiverCoinString().equals(Config.getReward());
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeShort(TYPE);
        stream.write(receiver.getBytes());
        stream.write(reward.content());
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
    public int getNonce() {
        return 0;
    }

    @Override
    public List<UTXO> getTXIDs() {
        return null;
    }

    @Override
    public boolean matches(byte[] header) {
        return false;
    }

    @Override
    public void getTXIDs(List<UTXO<?>> list) {

    }

    @Override
    public boolean matches(Class<?> t) {
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
        return ByteUtil.concatenate(receiver.getBytes(), amount.getBytes(), ByteUtil.encode(time));
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
