package com.riverssen.core.security;

import com.riverssen.utils.Base58;
import com.riverssen.utils.Encodeable;

public class PublicAddress implements Encodeable
{
    private String address;

    public PublicAddress(String address)
    {
        this.address = address;
    }

    public PublicAddress(byte address[])
    {
        this.address = Base58.encode(address);
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
}