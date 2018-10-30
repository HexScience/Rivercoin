package nucleus.crypto.ec;

import nucleus.crypto.Key;

import java.security.PrivateKey;

public class ECPrivateKey implements Key
{
    private PrivateKey key;

    public ECPrivateKey(PrivateKey key)
    {
        this.key = key;
    }

    @Override
    public boolean fromBytes(byte[] bytes)
    {
        return false;
    }

    @Override
    public byte[] getBytes()
    {
        return ((org.bouncycastle.asn1.sec.ECPrivateKey)(key)).getKey().toByteArray();
    }

    @Override
    public byte[] sign(byte[] bytes, byte[] encryption)
    {
        return new byte[0];
    }

    @Override
    public boolean verify(byte[] signature, byte[] data)
    {
        return false;
    }

    @Override
    public boolean decrypt(byte key)
    {
        return false;
    }

    @Override
    public boolean encrypt(byte key)
    {
        return false;
    }

    @Override
    public Key getCompressedForm()
    {
        return null;
    }

    @Override
    public Key getUncompressedForm()
    {
        return null;
    }

    @Override
    public Key getAddressForm(byte prefix)
    {
        return null;
    }
}
