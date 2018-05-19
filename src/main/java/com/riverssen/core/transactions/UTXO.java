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
import com.riverssen.core.headers.JSONFormattable;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.utils.Base58;
import com.riverssen.utils.ByteUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class UTXO<T extends Encodeable & JSONFormattable> implements Encodeable, JSONFormattable
{
    private final PublicAddress owner;
    private final T             value;
    private final byte          ptxid[];

    public UTXO(PublicAddress receiver, T value, byte parentTXID[])
    {
        this.owner = receiver;
        this.value = value;
        this.ptxid = parentTXID;
    }

    /** The parent transaction ID **/
    public byte[] getParentTXID()
    {
        return ptxid;
    }
    /** The value of the UTXO, using UTXO<Rivercoin> will return rvc balances **/
    public T getValue()
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
    public byte[] getBytes() {
        return ByteUtil.concatenate(getParentTXID(), getValue().getBytes(), getOwner().getBytes());
    }

    @Override
    public String toJSON() {
        return new JSON().add("owner", getOwner().toString()).add("value", getValue().toString()).add("txid", Base58.encode(getParentTXID())).toString();
    }
}