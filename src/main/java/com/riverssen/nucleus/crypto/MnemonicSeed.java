package com.riverssen.nucleus.crypto;

import com.riverssen.nucleus.util.ByteUtil;

public class MnemonicSeed
{
    private byte[] mnemonicString;

    public MnemonicSeed()
    {
        this.mnemonicString = new byte[0];
    }
    public MnemonicSeed(byte[] string)
    {
        this.mnemonicString = string;
    }

    public MnemonicSeed concatenate(MnemonicSeed other)
    {
        return new MnemonicSeed(ByteUtil.concatenate(mnemonicString, other.mnemonicString));
    }
}