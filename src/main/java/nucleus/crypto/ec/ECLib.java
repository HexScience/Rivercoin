package nucleus.crypto.ec;

import com.riverssen.core.utils.Base58;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.core.utils.HashUtil;
import nucleus.exceptions.ECLibException;
import nucleus.exceptions.ECPointUnobtainableException;
import nucleus.exceptions.ECPrivateKeyInconstructableException;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class ECLib
{
    static X9ECParameters params;
    static ECDomainParameters CURVE;

    public static final void init()
    {
        Security.addProvider(new BouncyCastleProvider());
        params = SECNamedCurves.getByName("secp256k1");
        CURVE = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());
    }
    /**
     * This is a utility library written for creating and manipulating ECDSA Keypairs.
     */

    /**
     * @param x256 A 32 byte raw private key
     * @return A private key
     * @throws ECLibException
     */
    public static final PrivateKey ECPrivateKey(BigInteger x256) throws ECLibException
    {
        if (x256 == null) throw new ECPrivateKeyInconstructableException("null");
//        if (x256.length != 32) throw new ECPrivateKeyInconstructableException(x256.length + "");

        return GenPrivateKey(x256, "secp256k1");
    }

    /**
     * @param privateKey The private key used to generate this public key.
//     * @param mod Ignore security flaw in seed.
     * @return A public key.
     * @throws ECLibException
     */
    public static final BCECPublicKey ECPublicKey(BCECPrivateKey privateKey) throws ECLibException
    {
        if (privateKey == null) throw new ECPrivateKeyInconstructableException("null");

        ECPoint point = new FixedPointCombMultiplier().multiply(CURVE.getG(), privateKey.getD());

        return (BCECPublicKey) GenPublicKey(EC_POINT_point2oct(point.getEncoded(false), false), "secp256k1");
    }

    /**
     * @param x512 A 64 byte raw public key
     * @return A public key
     * @throws ECLibException
     */
    public static final BCECPublicKey ECPublicKey(BigInteger x512) throws ECLibException
    {
        if (x512 == null) throw new ECPrivateKeyInconstructableException("null");
//        if (x256.length != 32) throw new ECPrivateKeyInconstructableException(x256.length + "");

        return (BCECPublicKey) GenPublicKey(EC_POINT_point2oct(x512.toByteArray(), false), "secp256k1");
    }

    /**
     * @param key Public key to extract the ECPoint from.
     * @return The extracted ECPoint.
     * @throws ECPointUnobtainableException
     */
    public static final ECPoint EC_POINT(PublicKey key) throws ECPointUnobtainableException
    {
        if (key instanceof BCECPublicKey)
            return ((BCECPublicKey) key).getQ();
        else throw new ECPointUnobtainableException("no ECPubKey provided");
    }

    /**
     * @param point ECPoint to return.
     * @param compressed A check for returning a compressed ECPoint/uncompressed ECPoint.
     * @return Byte array ECPoint (65/32 bytes).
     */
    public static final byte[] EC_POINT_point2oct(ECPoint point, boolean compressed)
    {
        return point.getEncoded(compressed);
    }


    /**
     * @param data ECPoint data to return.
     * @param compressed A check for returning a compressed ECPoint/uncompressed ECPoint.
     * @return ECPoint object.
     */
    public static final ECPoint EC_POINT_point2oct(byte data[], boolean compressed)
    {
        X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        ECDomainParameters CURVE = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());

        ECPoint point = CURVE.getCurve().decodePoint(data);

        return point;
    }

    public static final PrivateKey GenPrivateKey(BigInteger s, String curveName) throws ECLibException
    {
        return ECLib.GenPrivateKey(s, curveName, false);
    }

    /**
     * @param s The secret 32 bytes to initialize the private key.
     * @param curveName The curvename to use with key creation (secp256k1 by default).
     * @param ignoreSecurityErrs Ignore security error (Unsafe)
     * @return A PrivateKey object.
     * @throws ECLibException
     */
    public static final PrivateKey GenPrivateKey(BigInteger s, String curveName, boolean ignoreSecurityErrs) throws ECLibException
    {
        ECParameterSpec ecParameterSpec = ECNamedCurveTable.getParameterSpec(curveName);

        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(s, ecParameterSpec);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            BCECPrivateKey key = (BCECPrivateKey) keyFactory.generatePrivate(privateKeySpec);

            if (key.getD().bitLength() > CURVE.getN().bitLength())
            {
                key = (BCECPrivateKey) ECLib.ECPrivateKey(key.getD().mod(CURVE.getN()));
                if (!ignoreSecurityErrs) throw new ECLibException("ECPrivateKeyTooBigException: P%N==0 (Major security flaw).\n\t(ignoreSecurityErrs=true) to continue.");
                if (key.getD().equals(BigInteger.ZERO)) throw new ECLibException("ECPrivateKeyZeroException: Resulting private key is ZERO.");
            }

            return key;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final PublicKey GenPublicKey(ECPoint pointUncompressed, String curveName)
    {
        ECParameterSpec ecParameterSpec = ECNamedCurveTable.getParameterSpec(curveName);

        ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(pointUncompressed, ecParameterSpec);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final boolean ECPairRecover(BCECPrivateKey privateKey, BCECPublicKey publicKey, byte compressedPoint[]) throws ECLibException
    {
        /**
         * Check the recoverability of the compressed point.
         */

        boolean pointRecovered  = false;
        boolean publicKeyRcvrd  = false;
        boolean publicKeyRcvr2  = false;

        ECPoint p               = ECLib.EC_POINT_point2oct((publicKey) .getQ().getEncoded(false), false);
        BCECPublicKey recovered = ECPublicKey(new BigInteger(publicKey.getQ().getEncoded(false)));

        pointRecovered          = ByteUtil.equals(publicKey.getQ().getEncoded(false), p.getEncoded(false));
        publicKeyRcvrd          = ByteUtil.equals(recovered.getQ().getEncoded(false), publicKey.getQ().getEncoded(false));
        publicKeyRcvr2          = ByteUtil.equals(publicKey.getQ().getEncoded(false), ECLib.ECPublicKey(privateKey).getQ().getEncoded(false));

        return pointRecovered && publicKeyRcvrd && publicKeyRcvr2 && publicKey.getQ().getEncoded(false).length == 65 && privateKey.getD().toByteArray().length == 32;
    }

    public static final String WIFPrivateKey(BigInteger privateKey) throws ECLibException
    {
        byte prefix = (byte) 0x80;

        byte sha256[]   = HashUtil.applySha256(ByteUtil.concatenate(new byte[] {prefix}, privateKey.toByteArray()));
        byte sha2562[]  = HashUtil.applySha256(sha256);

        byte checksum[] = ByteUtil.trim(sha2562, 0, 4);

        return Base58.encode(ByteUtil.concatenate(ByteUtil.concatenate(new byte[] {prefix}, privateKey.toByteArray()), checksum));
    }
}
