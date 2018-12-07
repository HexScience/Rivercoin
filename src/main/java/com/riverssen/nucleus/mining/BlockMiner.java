package com.riverssen.nucleus.mining;

import com.riverssen.nucleus.algorithms.Sha256;
import com.riverssen.nucleus.protocols.protobufs.BlockHeader;

import java.io.IOException;
import java.math.BigInteger;

public class BlockMiner
{
    public static byte[] MineBlock(final BlockHeader header) throws IOException
    {
        byte headerBytes[] = header.getBytes();
        Sha256 sha256 = new Sha256();
        long nonce = 0;

        BigInteger result = new BigInteger("");

//        while ()

        return sha256.encode(headerBytes);
    }
}
