package com.riverssen.core.security;

import com.riverssen.utils.Base58;
import com.riverssen.core.headers.Encodeable;

public class CompressedAddress implements Encodeable
{
    private String address;

    public CompressedAddress(String address)
    {
        this.address = address;
    }

    @Override
    public String toString()
    {
        return address;
    }

    public PubKey toPublicKey()
    {
        PubKey key = new PubKey(address);

        if(key.isValid()) return key;
        return null;
    }

    @Override
    public byte[] getBytes()
    {
        return Base58.decode(address);
    }
}