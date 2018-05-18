package com.riverssen.utils;

public interface Encodeable
{
    default byte[] encode(HashAlgorithm algorithm)
    {
        return algorithm.encode(getBytes());
    }
    default String encode16(HashAlgorithm algorithm)
    {
        return algorithm.encode16(getBytes());
    }
    default String encode32(HashAlgorithm algorithm)
    {
        return algorithm.encode32(getBytes());
    }
    default String encode58(HashAlgorithm algorithm)
    {
        return algorithm.encode58(getBytes());
    }
    default String encode64(HashAlgorithm algorithm)
    {
        return algorithm.encode64(getBytes());
    }

    byte[] getBytes();
}
