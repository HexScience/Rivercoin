package nucleus.crypto;

import nucleus.crypto.ec.ECLib;
import nucleus.protocols.protobufs.Address;
import nucleus.system.Parameters;
import nucleus.util.Base58;
import nucleus.util.ByteUtil;
import nucleus.util.HashUtil;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class KeyPair
{
    private byte[]              seed;
    private BCECPrivateKey      privateKey;
    private BCECPublicKey       publicKey;
    private byte[]              enckey;
    private long                seeds[];

    public KeyPair(byte seed[])
    {
        this.seed = HashUtil.applySha256(HashUtil.applySha256(seed));
        this.generate(seed);
    }

    public byte[] getSeed()
    {
        return seed;
    }

    public BigInteger getPrivateKey()
    {
        return privateKey.getD();
    }

    public BCECPrivateKey getPrivateKeyObject()
    {
        return privateKey;
    }

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
    private void generate(byte seed[])
    {
        try
        {
            this.privateKey = (BCECPrivateKey) ECLib.ECPrivateKey(new BigInteger(seed));
            this.publicKey = ECLib.ECPublicKey(this.privateKey);
            this.enckey = this.publicKey.getQ().getEncoded(true);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
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

    public String getWIFPrivateKey()
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
        return "\tprivate: " + getWIFPrivateKey() + " " + privateKey.getD().toByteArray().length + "\n\tpublic:  " + Base58.encode(publicKey.getQ().getEncoded(false)) + " " + enckey.length + "\n\taddress: " + getAddress();
    }
}