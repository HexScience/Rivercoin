package com.riverssen.utils;

public interface HashAlgorithm
{
    byte[] encode(byte data[]);
    String encode16(byte data[]);
    String encode32(byte data[]);
    String encode58(byte data[]);
    String encode64(byte data[]);

    default int numBits() { return 256; }
}