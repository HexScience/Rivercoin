package nucleus.crypto;

import com.riverssen.core.utils.Base58;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.core.utils.HashUtil;
import nucleus.crypto.ec.ECLib;
import nucleus.protocol.protobufs.Address;
import nucleus.util.Base16;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

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
        this.seed = HashUtil.applySha512(seed);

        this.generate(HashUtil.applySha512(seed));
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

    /** Generate an ECDSA Key Pair **/
    private void generate(byte seed[])
    {
        try
        {
            X9ECParameters params = SECNamedCurves.getByName("secp256k1");
            ECDomainParameters CURVE = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(seed);

            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");//"prime192v1");

            keyGen.initialize(ecSpec, random);
            java.security.KeyPair keyPair = keyGen.generateKeyPair();

            this.privateKey = ((BCECPrivateKey) keyPair.getPrivate());//.getD();

            this.publicKey = (((BCECPublicKey)  keyPair.getPublic()));// .getQ().getEncoded(false));
            this.enckey = ((((BCECPublicKey)  keyPair.getPublic()) .getQ().getEncoded(true)));
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

        byte version    = 0x00;
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
        return "\tprivate: " + getWIFPrivateKey() + " " + privateKey.getD().toByteArray().length + "\n\tpublic:  " + Base16.encode(publicKey.getQ().getEncoded(false)) +  " " + publicKey.getQ().getEncoded(false) + " " + enckey.length + "\n\taddress: " + getAddress();
    }
}