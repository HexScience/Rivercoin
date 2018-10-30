package com.riverssen.wallet;

public interface Key
{
    public boolean  fromBytes(byte bytes[]);
    public byte[]   getBytes();
    public default byte[]   sign(byte bytes[])
    {
        return sign(bytes, null);
    }
    public byte[]   sign(byte bytes[], byte encryption[]);
    public boolean  verify(byte signature[], byte data[]);
    public boolean  decrypt(byte key);
    public boolean  encrypt(byte key);

    Key getCompressedForm();
    PublicAddress getAddressForm(byte prefix);
}