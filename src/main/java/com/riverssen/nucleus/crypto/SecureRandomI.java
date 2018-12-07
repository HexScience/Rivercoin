package com.riverssen.nucleus.crypto;

public interface SecureRandomI
{
    void get(byte[] bytes);
    byte get();
    short getShort();
    int getInt();
    long getLong();
    int getInt(int bounds);
    long getLong(long bounds);
}