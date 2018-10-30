package com.riverssen.testing;

import com.riverssen.core.security.CompressedAddress;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.core.utils.HashUtil;
import com.riverssen.system.Parameters;
import com.riverssen.wallet.KeyChain;
import com.riverssen.wallet.PubKey;
import com.riverssen.wallet.ec.DerivedKey;
import com.riverssen.wallet.ec.PrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.Sign;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Wallet {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void main(String args[]) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());


//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
//        keyPairGenerator.initialize(new ECGenParameterSpec("secp256k1"), new SecureRandom());
//        java.security.KeyPair keyPair = keyPairGenerator.generateKeyPair();
//
//        java.security.PrivateKey privateKey = keyPair.getPrivate();
//        System.out.println(privateKey.getFormat());
//        PublicKey publicKey = keyPair.getPublic();
//        System.out.println(publicKey.getFormat());
//
//        // A KeyFactory is used to convert encoded keys to their actual Java classes
//        KeyFactory ecKeyFac = KeyFactory.getInstance("EC", "BC");
//
//        // Now do a round-trip for a private key,
//        byte [] encodedPriv = privateKey.getEncoded();
//        // now take the encoded value and recreate the private key
//        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(encodedPriv);
//        java.security.PrivateKey privateKey2 = ecKeyFac.generatePrivate(pkcs8EncodedKeySpec);
//        System.out.println(((ECPrivateKey) privateKey).getS().toByteArray().length);
//
//        // And a round trip for the public key as well.
//        byte [] encodedPub = publicKey.getEncoded();
//        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(encodedPub);
//        PublicKey publicKey2 = ecKeyFac.generatePublic(x509EncodedKeySpec);
//        System.out.println(publicKey2);












        for (int i = 0; i < 100000000; i++)
        {
            KeyChain chain = new KeyChain("b", ("." + System.currentTimeMillis()).getBytes());

            System.out.println(chain.getPublic().toString().equals(new PubKey((CompressedAddress) chain.getPublic().getCompressedForm()).toString()));
        }





//        System.exit(0);
//        for (int i = 0; i < 500000; i ++)
//        {
//            PrivateKey pkey = new PrivateKey(System.currentTimeMillis() + "", Parameters.MAIN_NETWORK_PUBLIC_ADDRESS_PREFIX);
//            DerivedKey dKey = new DerivedKey(pkey.getRawKey(), Parameters.MAIN_NETWORK_PUBLIC_ADDRESS_PREFIX);
//
//            String msg = "hello there boi.";
//
//            byte signature[] = pkey.sign(dKey.getRawKey(), msg.getBytes(), null);
//
//            Sign.SignatureData signature1 = new Sign.SignatureData(signature[0], ByteUtil.trim(signature, 1, 33), ByteUtil.trim(signature, 33, 65));
//
//            CompressedAddress address = (CompressedAddress) dKey.getCompressedForm();
//
//
//            System.out.println(Sign.signedMessageToKey(msg.getBytes(), signature1).equals(dKey.getRawKey()));
//            System.out.println(Sign.signedMessageToKey(msg.getBytes(), signature1).equals(address.decompress()));
//        }
//
//        System.out.println(pkey + "\n" + dKey + "\n" + dKey.getCompressedForm().getBytes().length);
//
//        X9ECParameters ecp = SECNamedCurves.getByName("secp256k1");
//        ECPoint curvePt = ecp.getG().multiply(pkey.getRawKey());
//        BigInteger x = curvePt.getXCoord().toBigInteger();
//        BigInteger y = curvePt.getYCoord().toBigInteger();
//        byte[] xBytes = removeSignByte(x.toByteArray());
//        byte[] yBytes = removeSignByte(y.toByteArray());
//        byte[] pubKeyBytes = new byte[65];
//        pubKeyBytes[0] = new Byte("04");
//        System.arraycopy(xBytes,0, pubKeyBytes, 1, xBytes.length);
//        System.arraycopy(yBytes, 0, pubKeyBytes, 33, yBytes.length);
//        bytesToHex(pubKeyBytes);
//
//        System.out.println(Base58.encode(pubKeyBytes) + "\n" + Base58.encode(ByteUtil.concatenate(new byte[] {0x04}, dKey.getRawKey().toByteArray())));
        for (int i = 0; i < 100000; i ++) {
            KeyChain keyChain = new KeyChain("test", ("test-keypair" + i).getBytes());

            byte x[] = ((ECPublicKey) ((PubKey) keyChain.getPublic()).getPublic()).getW().getAffineX().toByteArray();
            byte y[] = ((ECPublicKey) ((PubKey) keyChain.getPublic()).getPublic()).getW().getAffineY().toByteArray();

            BigInteger g = new BigInteger(ByteUtil.concatenate(x, y));




            System.out.println(g.toByteArray().length);
//            PubKey key = new PubKey((CompressedAddress)keyChain.getPublic().getCompressedForm());

            String message = "Hi there boy";

//            byte signature[] = keyChain.getPrivate().sign(message.getBytes());

//            System.out.println(keyChain);
//            System.out.println(signature.length);
//            System.out.println(key.toString().equals(keyChain.getPublic().toString()));
//
//            System.out.println(keyChain.getPublic().verify(signature, message.getBytes()));
//            System.out.println(key.verify(signature, message.getBytes()));

//            System.out.println(y.length + " " + x.length);
//            System.out.println(new PubKey(new CompressedAddress(HashUtil.hashToStringBase16(compressed))).toString().equals(keyChain.getPublic().toString()));
        }

//        System.out.println(((PrivKey)keyChain.getPrivate()).getKey().getFormat());
//        System.out.println(((PubKey)keyChain.getPublic()).getKey().getFormat());
//
//            System.out.println(((PrivKey) keyChain.getPrivate()).getKey().getEncoded().length);
//            System.out.println(((PubKey) keyChain.getPublic()).getKey().getEncoded().length);
//            System.out.println(((PubKey) keyChain.getPublic()).getCompressed().getBytes().length);
//            System.out.println(((PubKey) keyChain.getPublic()).getCompressed());

//            PubKey key = new PubKey(((PubKey) keyChain.getPublic()).getCompressed());
//            System.out.println(Base58.encode(key.getBytes()));

//            System.out.println(Base58.encode(key.getBytes()).equals(Base58.encode(keyChain.getPublic().getBytes())));
//        }

//        keyChain = new KeyChain(keyChain.getPrivate());

//        System.out.println(keyChain);
    }
}