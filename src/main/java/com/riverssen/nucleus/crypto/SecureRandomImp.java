package com.riverssen.nucleus.crypto;

import java.security.SecureRandom;

public class SecureRandomImp implements SecureRandomI
{
    private SecureRandom random = new SecureRandom();

    @Override
    public void get(byte[] bytes)
    {
        random.nextBytes(bytes);
    }

    @Override
    public byte get()
    {
        return (byte) random.nextInt(256);
    }

    @Override
    public short getShort()
    {
        return (short) random.nextInt(65536);
    }

    @Override
    public int getInt()
    {
        return random.nextInt();
    }

    @Override
    public long getLong()
    {
        return random.nextLong();
    }

    @Override
    public int getInt(int bounds)
    {
        return random.nextInt(bounds);
    }

    @Override
    public long getLong(long bounds)
    {
        return random.nextLong();
    }
}
