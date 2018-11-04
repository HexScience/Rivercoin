package nucleus.crypto.ec;

import nucleus.crypto.Key;
import nucleus.exceptions.ECLibException;
import nucleus.protocol.protobufs.Address;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class ECDerivedPublicKey implements Key
{
    private BCECPublicKey key;

    public ECDerivedPublicKey()
    {
    }

    public ECDerivedPublicKey(BCECPublicKey key)
    {
        this.key = key;
    }

    public ECDerivedPublicKey(byte key[]) throws ECLibException
    {
        this.key = ECLib.ECPublicKey(new BigInteger(key));
    }

    @Override
    public boolean fromBytes(byte[] bytes)
    {
        return false;
    }

    @Override
    public byte[] getBytes()
    {
        return ((BCECPublicKey)key).getQ().getEncoded(false);
    }

    @Override
    public byte[] sign(byte[] bytes, byte[] encryption)
    {
        return new byte[0];
    }

    public boolean verify(byte signature[], byte signatureData[]) throws ECLibException
    {
        return ECLib.ECSigVerify(key, signatureData, signature);
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

    //IO Functions And Commands

    public void write(final DataOutputStream stream) throws IOException
    {
    }


    public void read(final DataInputStream stream) throws IOException
    {
    }

    public Address toAddress()
    {
        return null;
    }
}
