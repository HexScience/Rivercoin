package com.riverssen.nucleus.protocols.transaction;

import com.riverssen.nucleus.crypto.ec.ECLib;
import com.riverssen.nucleus.exceptions.ECLibException;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

public class Signature
{
    private byte[]              signature;
    private byte[]              data;

    private BCECPrivateKey      privateKey;
    private BCECPublicKey       publicKey;

    public byte[] getSignature()
    {
        return signature;
    }
    public byte[] getSignatureData()
    {
        return data;
    }

    public void setPrivateKey(BCECPrivateKey privateKeyObject)
    {
        this.privateKey = privateKeyObject;
    }

    public void setPublicKey(byte enckey[]) throws ECLibException
    {
        this.publicKey = ECLib.ECPublicKeyFromCompressed(enckey);
    }

    public void setData(byte data[])
    {
        this.data = data;
    }

    public void sign() throws ECLibException
    {
        this.signature = ECLib.ECSign(privateKey, data);
    }

    public boolean verify() throws ECLibException
    {
        return ECLib.ECSigVerify(publicKey, data, signature);
    }
}