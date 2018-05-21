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

import com.riverssen.core.headers.Encodeable;
import com.riverssen.core.headers.Exportable;
import com.riverssen.utils.ByteUtil;
import com.riverssen.utils.SmartDataTransferer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

public class TXIList extends ArrayList<TransactionInput> implements Encodeable, Exportable
{
    public TXIList()
    {
    }

    public TXIList(DataInputStream stream) {

    }
    /** read all transaction inputs and return a rivercoin value **/
    /** this only works with Transactions & Rewards and not contracts **/
    BigInteger getInputAmount()
    {
        BigInteger amount = BigInteger.ZERO;

        for(TransactionInput txi : this)
            amount = amount.add(txi.getUTXO().getValue().toBigInteger());

        return amount;
    }

    /** this function should be used for traversing a list for a Contract **/
    public <K> K traverse(TraverserI<K> traverserI)
    {
        return traverserI.traverse(this);
    }

    @Override
    public byte[] getBytes()
    {
        byte data[] = get(0).getBytes();

        for(int i = 1; i < size(); i ++) data = ByteUtil.concatenate(data, get(i).getBytes());
        return data;
    }

    @Override
    public byte[] header() {
        return ByteUtil.encodei(size());
    }

    @Override
    public byte[] content() {
        byte data[] = get(0).data();
        for(int i = 1; i < size(); i ++) data = ByteUtil.concatenate(data, get(i).data());
        return new byte[0];
    }

    @Override
    public void export(SmartDataTransferer smdt) {
        throw new RuntimeException("TransactionInputList: unexportable.");
    }

    @Override
    public void export(DataOutputStream dost) throws IOException {
        dost.writeInt(size());
        for(TransactionInput txi : this)
            txi.export(dost);

//        throw new RuntimeException("TransactionInputList: unexportable.");
    }

    public interface TraverserI<T>
    {
        T traverse(TXIList list);
    }
}
