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
import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.mpp.compiler.Namespace;
import com.riverssen.core.security.CompressedAddress;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.core.system.Config;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.utils.SmartDataTransferer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class Contract implements TransactionI
{
    private CompressedAddress sender;
    private Namespace         global;
    private long              timestamp;

    public Contract(DataInputStream stream)
    {
    }

    public Contract(String text)
    {
    }

    @Override
    public boolean valid(ContextI context)
    {
        return false;
    }

    @Override
    public long getTimeStamp()
    {
        return 0;
    }

    @Override
    public CompressedAddress getSender()
    {
        return null;
    }

    @Override
    public PublicAddress getReceiver()
    {
        return null;
    }

    @Override
    public TXIList getTXIDs()
    {
        return null;
    }

    @Override
    public List<TransactionOutput> generateOutputs(PublicAddress miner, ContextI context)
    {
        return null;
    }

    @Override
    public RiverCoin cost()
    {
        return null;
    }

    @Override
    public BigInteger getInputAmount()
    {
        return null;
    }

    @Override
    public void revertOutputs(PublicAddress miner, ContextI context)
    {

    }

    @Override
    public byte[] getBytes()
    {
        return new byte[0];
    }

    @Override
    public byte[] header()
    {
        return new byte[0];
    }

    @Override
    public byte[] content()
    {
        return new byte[0];
    }

    @Override
    public void export(SmartDataTransferer smdt)
    {

    }

    @Override
    public void export(DataOutputStream dost) throws IOException
    {

    }

    @Override
    public String toJSON()
    {
        return null;
    }
}