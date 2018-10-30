package nucleus;

import com.riverssen.core.utils.Base58;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.core.utils.HashUtil;
import nucleus.crypto.KeyPair;
import nucleus.crypto.ec.ECLib;
import nucleus.exceptions.ECLibException;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;

public class Start
{
    private ASN1Primitive toDERObject(byte[] data) throws IOException
    {
        ByteArrayInputStream inStream = new ByteArrayInputStream(data);
        ASN1InputStream asnInputStream = new ASN1InputStream(inStream);

        return asnInputStream.readObject();
    }

    private static byte[] removeSignByte(byte[] arr)
    {
        if(arr.length==33)
        {
            byte[] newArr = new byte[32];
            System.arraycopy(arr, 1, newArr, 0, newArr.length);
            return newArr;
        }
        return arr;
    }

    public static byte[] getCompressed(byte key[])
    {
        byte x[] = ByteUtil.trim(key, 0, 32);
        byte y[] = ByteUtil.trim(key, 32, key.length);

        BigInteger primeY = new BigInteger(y);

        byte theKey[] = new byte[1];

        /** if Y is even **/
        if (primeY.mod(new BigInteger("2")).equals("0"))
            theKey[0] = 0x02;
        /** but if it "can't even", jokes lol. **/
        else
            theKey[0] = 0x03;

        return ByteUtil.concatenate(theKey, x);
    }

    public static String compressPubKey(BigInteger pubKey) {
        String pubKeyYPrefix = pubKey.testBit(0) ? "03" : "02";
        String pubKeyHex = pubKey.toString(16);
        String pubKeyX = pubKeyHex.substring(0, 64);
        return pubKeyYPrefix + pubKeyX;
    }

    public static void main(String args[])
    {
        ECLib.init();
        for (int i = 0; i < 100000; i ++)
        {
            KeyPair pair = new KeyPair(("bitburger" + i).getBytes());

            if (pair.getPrivateKey().toByteArray().length > 32)
            {
                try
                {
                    BCECPrivateKey pkey = (BCECPrivateKey) ECLib.ECPrivateKey(new BigInteger(ByteUtil.trim(pair.getPrivateKey().toByteArray(), 1, 33)));

                    System.out.println(ByteUtil.equals(pair.getPublicKey(), ECLib.ECPublicKey(pair.getPrivateKeyObject()).getQ().getEncoded(false)));
//                    System.out.println(pkey.getD().equals(pair.getPrivateKey()));


                } catch (ECLibException e)
                {
                    e.printStackTrace();
                }
            }

//            try
//            {
////                System.out.println(ECLib.ECPairRecover(pair.getPrivateKey(), pair.getPublicKeyObject(), pair.getCompressedPublicKey()));
//            } catch (ECLibException e)
//            {
//                e.printStackTrace();
//            }

//            ECPoint point = CURVE.getCurve().decodePoint(pair.getCompressedPublicKey().toByteArray());
//
//            System.out.println(ByteUtil.equals(ByteUtil.trim(point.getEncoded(false), 1, 65), pair.getPublicKey().toByteArray()));

//            BCECPrivateKey key = (BCECPrivateKey) KeyPair.getPrivateKeyFromBytes(pair.getPrivateKey(), "secp256k1");
//            System.out.println(Base58.encode(key.getD().toByteArray()));

//            ECPoint point = new ECPoint.Fp()
//            point = point.normalize();
//            BigInteger x = point.getAffineXCoord().toBigInteger();
//            BigInteger y = point.getAffineYCoord().toBigInteger();


//            System.out.println(pair);
//            System.out.println(Base58.encode(pair.getPublicKey().toByteArray()).equals(Base58.encode(Sign.publicKeyFromPrivate(pair.getPrivateKey()).toByteArray())));

//            org.bouncycastle.jce.spec.ECNamedCurveParameterSpec SPEC = ECNamedCurveTable.getParameterSpec("secp256k1");
//
//            org.bouncycastle.math.ec.ECPoint point = SPEC.getCurve().decodePoint(Base16.decode(compressPubKey(pair.getPublicKey())));
//            BigInteger x = point.getXCoord().toBigInteger();//.getEncoded();
//            BigInteger y = point.getYCoord().toBigInteger();//.getEncoded();
//         concat 0x04, x, and y, make sure x and y has 32-bytes:
//        byte bytes[] = (ByteUtil.concatenate(new byte[] {0x04}, x, y));

//        X509EncodedKeySpec specp = new X509EncodedKeySpec(bytes);

//        KeyFactory factory = KeyFactory.getInstance("ECDSA", "BC");
//
//        this.key = factory.generatePublic(specp);

//            java.security.spec.ECPoint point2 = new java.security.spec.ECPoint(x, y);
//            ECPublicKeySpec pubSpec = new ECPublicKeySpec(point2, ((ECPublicKey) KeyChain.defaultPublicKey).getParams());

//            Sign.signMessage(Hash.sha3(msg.getBytes()), new ECKeyPair(pair.getPrivateKey(), pair.getPublicKey()));
//            System.out.println(pair.getPublicKey().getBytes()[0] + " " + 0x04);

//            byte t[] = ByteUtil.trim(pair.getPublicKey().getBytes(), 1, 65);
//
//            byte x[] = ByteUtil.trim(pair.getPublicKey().getBytes(), 0, 32);
//            byte y[] = ByteUtil.trim(pair.getPublicKey().getBytes(), 32, 64);
//
//            byte p   = (byte) (new BigInteger(y).mod(new BigInteger("2")).equals(BigInteger.ZERO) ? 0x02 : 0x03);
//
//            byte compressed[] = ByteUtil.concatenate(new byte[] {p}, ByteUtil.trim(pair.getPublicKey().getBytes(), 1, 33));

//            System.out.println(pair);
        }
//        Logger.alert(Parameters.difficultyToBase2(Parameters.MINIMUM_DIFFICULTY) + "");
//        Logger.alert(Parameters.difficultyToBase2(Parameters.MAXIMUM_DIFFICULY) + "");
//        Logger.alert(Parameters.base2ToDifficulty(Parameters.difficultyToBase2(Parameters.MINIMUM_DIFFICULTY)) + "");
//        Logger.alert(Parameters.MINIMUM_DIFFICULTY + "");

//        Logger.alert(Parameters.prependedHash(new BigDecimal(Parameters.MAXIMUM_DIFFICULY).pow(256).toBigInteger().toString(16)));

//        Parameters.calculateDifficulty(12_000, 0, Parameters.MINIMUM_DIFFICULTY);
//        Parameters.calculateDifficulty(12_000 * 6, 0, Parameters.MAXIMUM_DIFFICULY);
//        Parameters.calculateDifficulty(12_000 / 2, 0, Parameters.MINIMUM_DIFFICULTY);
//        Parameters.calculateDifficulty(12_000 / 4, 0, Parameters.MINIMUM_DIFFICULTY);
    }
}