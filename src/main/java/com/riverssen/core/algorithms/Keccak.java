package com.riverssen.core.algorithms;

import com.riverssen.utils.Base58;
import com.riverssen.core.headers.HashAlgorithm;
import com.riverssen.utils.HashUtil;

public class Keccak implements HashAlgorithm
{
    @Override
    public byte[] encode(byte[] data)
    {
        return HashUtil.applyKeccak(data);
    }

    @Override
    public String encode16(byte[] data)
    {
        return HashUtil.hashToStringBase16(encode(data));
    }

    @Override
    public String encode32(byte[] data)
    {
        return HashUtil.base36Encode(encode(data));
    }

    @Override
    public String encode58(byte[] data)
    {
        return Base58.encode(encode(data));
    }

    @Override
    public String encode64(byte[] data)
    {
        return HashUtil.base64StringEncode(encode(data));
    }
}