package com.riverssen.nucleus.crypto;

import com.riverssen.nucleus.crypto.ec.ECLib;
import com.riverssen.nucleus.exceptions.ECLibException;
import com.riverssen.nucleus.protocols.protobufs.Address;
import com.riverssen.nucleus.system.Parameters;
import com.riverssen.nucleus.util.Base58;
import com.riverssen.nucleus.util.ByteUtil;
import com.riverssen.nucleus.util.HashUtil;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

import java.math.BigInteger;

public class KeyPair
{
    private BCECPublicKey       publicKey;
    private byte[]              enckey;

    public KeyPair(byte rawSeed[]) throws ECLibException
    {
        this.generate(rawSeed);
    }

//    private byte[] seed(byte rawSeed[])
//    {
//        return rawSeed;
//    }

    private boolean runchecks(BCECPrivateKey privateKey) throws ECLibException
    {
        return ECLib.ECPairRecover(privateKey, publicKey, enckey);
    }

//    public BigInteger getPrivateKey()
//    {
//        return privateKey.getD();
//    }
//
//    public BCECPrivateKey getPrivateKeyObject()
//    {
//        return privateKey;
//    }

    public byte[] getPublicKey()
    {
        return getPublicKey(false);
    }

    public byte[] getPublicKey(boolean compressed)
    {
        return publicKey.getQ().getEncoded(compressed);
    }

    public BCECPublicKey getPublicKeyObject()
    {
        return publicKey;
    }

    /**
     * Generate an ECDSA Key Pair;
     * This function takes a double sha256 seed (or any arbitrary 256bit byte array)
     * and generates a private key DIRECTLY from the array.
     * This produces the same result as opposed to SecureRandom which can produce dif
     * -ferent results on different platforms.
     */
    private void generate(byte rawSeed[]) throws ECLibException
    {
            BCECPrivateKey privateKey = getPrivateKey(rawSeed);
            this.publicKey = ECLib.ECPublicKey(privateKey);
            this.enckey = this.publicKey.getQ().getEncoded(true);

            runchecks(privateKey);
    }

    public Address getAddress()
    {
        byte keyprefixbyte[] = {0x04};

        byte sha256[]   = HashUtil.applySha256(ByteUtil.concatenate(keyprefixbyte, publicKey.getQ().getEncoded(false)));
        byte sha2562[]  = HashUtil.applySha256(sha256);
        byte ripeMD[]   = HashUtil.applyRipeMD160(sha2562);

        byte version    = Parameters.MAIN_NETWORK_PUBLIC_ADDRESS_PREFIX;
        byte key_21[]   = ByteUtil.concatenate(new byte[] {version}, ripeMD);

        byte checksum[] = ByteUtil.trim(HashUtil.applySha256(HashUtil.applySha256(key_21)), 0, 4);

        return new Address(ByteUtil.concatenate(key_21, checksum));
    }

    public static String WIFPrivateKey(BCECPrivateKey privateKey)
    {
        byte prefix = (byte) 0x80;

        byte sha256[]   = HashUtil.applySha256(ByteUtil.concatenate(new byte[] {prefix}, privateKey.getD().toByteArray()));
        byte sha2562[]  = HashUtil.applySha256(sha256);

        byte version    = 0x00;
        byte checksum[] = ByteUtil.trim(sha2562, 0, 4);

        return Base58.encode(ByteUtil.concatenate(ByteUtil.concatenate(new byte[] {prefix}, privateKey.getD().toByteArray()), checksum));
    }

    public byte[] getCompressedPublicKey()
    {
        return enckey;
    }

    @Override
    public String toString()
    {
        return /**"\tprivate: " + getWIFPrivateKey() + " " + privateKey.getD().toByteArray().length + "\n\t**/"public:  " + Base58.encode(publicKey.getQ().getEncoded(false)) + " " + enckey.length + "\n\taddress: " + getAddress();
    }

    public static BCECPrivateKey getPrivateKey(byte seed[]) throws ECLibException
    {
        return (BCECPrivateKey) ECLib.ECPrivateKey(new BigInteger(seed));
    }
}