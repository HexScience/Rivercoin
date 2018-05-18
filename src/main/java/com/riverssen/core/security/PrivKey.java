package com.riverssen.core.security;

import com.riverssen.core.Logger;
import com.riverssen.utils.Base58;
import com.riverssen.utils.HashUtil;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Signature;

public class PrivKey
{
    private PrivateKey key;

    public PrivKey(PrivateKey key)
    {
        this.key = key;
    }

    public boolean verifySignature(PubKey pky, byte data[])
    {
        return false;
    }

    public String sign(String data)
    {
        return sign(data.getBytes());
    }

    public String sign(byte data[])
    {
        byte output[] = signedBytes(data);

        return Base58.encode(output);
    }

    public byte[] signEncoded(byte data[])
    {
        byte output[] = signedBytes(data);

        return (output);
    }

    public byte[] signedBytes(byte data[])
    {
        Signature dsa;
        byte[] output = new byte[1];

        try
        {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(key);
            byte[] strByte = data;
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (Exception e)
        {
            Logger.err("couldn't sign data!");
            e.printStackTrace();
        }

        return output;
    }

    public Key getPrivate()
    {
        return key;
    }

    public String decrypt(byte msg[])
    {
        try{
            Cipher cipher = Cipher.getInstance("ECDSA", "BC");
            cipher.init(Cipher.DECRYPT_MODE, key);

            return new String(cipher.doFinal(msg));
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return "";
    }
}
