package com.riverssen.utils;

import com.riverssen.core.chain.Serialisable;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteUtil
{
    public static byte[] encode(long serializable)
    {
        byte array[] = new byte[]{
                (byte) (serializable >> 56),
                (byte) (serializable >> 48),
                (byte) (serializable >> 40),
                (byte) (serializable >> 32),
                (byte) (serializable >> 24),
                (byte) (serializable >> 16),
                (byte) (serializable >> 8),
                (byte) serializable
        };

        return array;
    }

    public static byte[] encode(Serialisable serialisable)
    {
        return null;
    }

    public static void decode(Serialisable serialisable, byte data[])
    {
    }

    public static byte[] encodeHash(String hash)
    {
        return new BigInteger(hash).toByteArray();
    }

    public static String decodeHash(byte[] hash)
    {
        return HashUtil.hashToStringBase16(hash);
    }

    public static long decode(byte data[])
    {
        return ByteBuffer.wrap(data).getLong();
    }

    public static byte[] concatenate(byte[]...arrays)
    {
        int size = 0;
        for(byte[] array : arrays)
            size += array.length;

        byte concatenated[] = new byte[size];

        int index = 0;

        for(byte[] array : arrays)
        {
            System.arraycopy(array, 0, concatenated, index, array.length);

            index += array.length;
        }

        return concatenated;
    }

    public static byte[] encodei(int serializable)
    {
        byte array[] = new byte[]{
                (byte) (serializable >> 24),
                (byte) (serializable >> 16),
                (byte) (serializable >> 8),
                (byte) serializable
        };

        return array;
    }

    public static int decodei(byte data[])
    {
        return ByteBuffer.wrap(data).getInt();
    }

    public static byte[] read(DataInputStream stream, int amt)
    {
        byte b[] = new byte[amt];

        try
        {
            stream.read(b);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return b;
    }

    public static byte[] readvariable(DataInputStream stream)
    {
        try
        {
            int length = stream.readShort();

            byte b[]   = new byte[length];

            stream.read(b);

            return b;
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return new byte[1];
    }
}