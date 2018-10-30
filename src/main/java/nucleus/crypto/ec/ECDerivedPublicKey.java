package nucleus.crypto.ec;

import com.riverssen.core.utils.ByteUtil;
import nucleus.crypto.Key;
import nucleus.protocol.protobufs.Address;
import org.bouncycastle.asn1.eac.ECDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.PublicKey;

public class ECDerivedPublicKey implements Key
{
    private PublicKey key;

    public ECDerivedPublicKey()
    {
    }

    public ECDerivedPublicKey(PublicKey key)
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
        return ((BCECPublicKey)key).getQ().getEncoded(false);
    }

    @Override
    public byte[] sign(byte[] bytes, byte[] encryption)
    {
        return new byte[0];
    }

    public boolean verify(byte signature[], byte signatureData[])
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
