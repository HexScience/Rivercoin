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

package com.riverssen.core.security;

import com.riverssen.core.RiverCoin;
import com.riverssen.core.headers.Encodeable;
import com.riverssen.utils.Base58;

public class ContractAddress implements Encodeable
{
    private String address;

    public ContractAddress(String address)
    {
        this.address = address;
    }

    public ContractAddress(byte address[])
    {
    }

    @Override
    public String toString()
    {
        return address;
    }

    public byte[] getBytes()
    {
        return Base58.decode(address);
    }

    public static byte[] decode(String public_address)
    {
        return Base58.decode(public_address);
    }

    public RiverCoin getCost()
    {
        return new RiverCoin("0");
    }

    public boolean equals(ContractAddress address)
    {
        return address.address.equals(this.address);
    }
}