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

package com.riverssen.utils;

import com.riverssen.core.algorithms.Sha3;
import com.riverssen.core.headers.HashAlgorithm;
import com.riverssen.core.headers.Serialisable;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class ByteUtil
{
    private static HashAlgorithm dEncoder = new Sha3();

    public static HashAlgorithm defaultEncoder()
    {
        return dEncoder;
    }

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

    public static String[] concatenate(String[]...arrays)
    {
        int size = 0;
        for(String[] array : arrays)
            size += array.length;

        String concatenated[] = new String[size];

        int index = 0;

        for(String[] array : arrays)
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