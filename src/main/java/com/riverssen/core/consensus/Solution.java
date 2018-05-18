package com.riverssen.core.consensus;

import com.riverssen.core.tokens.Token;
import com.riverssen.core.headers.Encodeable;

import java.io.DataInputStream;
import java.io.IOException;

public class Solution implements Encodeable
{
    public Solution(DataInputStream in) throws IOException
    {
    }

    public Solution()
    {
    }

    @Override
    public byte[] getBytes()
    {
        return new byte[0];
    }

    public long blockID()
    {
        return 0;
    }
}