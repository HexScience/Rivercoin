package nucleus.protocols.transaction;

import nucleus.crypto.ec.ECDerivedPublicKey;

public class Signature
{
    private ECDerivedPublicKey  ecKey;
    private byte[]              signature;
    private byte[]              data;

    public ECDerivedPublicKey getKey()
    {
        return ecKey;
    }

    public byte[] getSignature()
    {
        return signature;
    }

    public byte[] getSignatureData()
    {
        return data;
    }
}