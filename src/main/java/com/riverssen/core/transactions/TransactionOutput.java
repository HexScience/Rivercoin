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
import com.riverssen.core.algorithms.Sha3;
import com.riverssen.core.headers.Encodeable;
import com.riverssen.core.headers.Exportable;
import com.riverssen.core.headers.JSONFormattable;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.utils.*;

import java.io.DataOutputStream;

/** Instances of this class will be created during runtime using the Transaction::getOutputs(Transaction*); **/
public class TransactionOutput/**<T extends Encodeable & JSONFormattable & Exportable>**/ implements Encodeable, JSONFormattable, Exportable
{
    private final PublicAddress owner;
    private final RiverCoin     value;
    private final byte          ptxid[];
    private final byte          txoid[];

    public TransactionOutput(PublicAddress receiver, RiverCoin value, byte parentTXID[])
    {
        this.owner = receiver;
        this.value = value;
        this.ptxid = parentTXID;
        /** generate a custom hash id for this particular transactionoutput **/
        this.txoid = ByteUtil.defaultEncoder().encode(ByteUtil.concatenate(receiver.getBytes(), value.getBytes(), parentTXID));
    }

    /** The parent transaction ID **/
    public byte[] getParentTXID()
    {
        return ptxid;
    }
    public byte[] getHash()
    {
        return txoid;
    }
    /** The value of the UTXO, using UTXO<Rivercoin> will return rvc balances **/
    public RiverCoin getValue()
    {
        return value;
    }
    /** The recepient of the unspent transaction output **/
    public PublicAddress getOwner()
    {
        return owner;
    }
    /** Any class must have a toString method returning a simple readable format of this header **/
    public String toString()
    {
        return toJSON();
    }

    @Override
    public byte[] getBytes()
    {
        return ByteUtil.concatenate(getOwner().getBytes(), getValue().getBytes(), getParentTXID());
    }

    @Override
    public String toJSON()
    {
        return new JSONFormattable.JSON(encode58(new Sha3())).add("owner", getOwner().toString()).add("value", getValue().toString()).add("txid", Base58.encode(getParentTXID())).toString();
    }

    @Override
    public byte[] header()
    {
        return ByteUtil.concatenate(getValue().header());
    }

    @Override
    public byte[] content()
    {
        return ByteUtil.concatenate(getOwner().getBytes(), getValue().getBytes(), getParentTXID());
    }

    @Override
    public void export(SmartDataTransferer smdt)
    {
    }

    @Override
    public void export(DataOutputStream dost) {
        throw new RuntimeException("TransactionOutput: should not be written to stream.");
//        try {
//            dost.write(header());
//            dost.write(content());
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
    }
}
