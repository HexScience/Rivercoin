package com.riverssen.core.consensus;

import com.riverssen.core.RVCCore;
import com.riverssen.core.tokens.Token;
import com.riverssen.utils.Encodeable;
import com.riverssen.utils.HashUtil;
import com.riverssen.utils.MerkleTree;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

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