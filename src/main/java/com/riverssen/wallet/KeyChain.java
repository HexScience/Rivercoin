package com.riverssen.wallet;

import com.riverssen.core.algorithms.Sha256;
import com.riverssen.core.system.Logger;
import com.riverssen.core.utils.Base58;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.system.Parameters;

import java.io.DataInputStream;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;

public class KeyChain
{
    public static KeyFactory Factory = null;
    public static java.security.PublicKey defaultPublicKey;
    public static java.security.PrivateKey defaultPrivateKey;

    static {
        try{
            Factory = KeyFactory.getInstance("EC", "BC");

            KeyChain keyChain = new KeyChain("default", "default".getBytes());

            defaultPublicKey = (java.security.PublicKey) ((PubKey)keyChain.getPublic()).getPublic();
            defaultPrivateKey = (java.security.PrivateKey) ((PrivKey)keyChain.getPrivate()).getPrivate();
        } catch (Exception e)
        {
            Logger.err("Key Factory couldn't be created!");
            e.printStackTrace();
            System.exit(0);
        }
    }

    private Key privateKey;
    private Key publicKey;
    private PublicAddress address;
    private KeyChain childChain;
    private long randomseed[];

    public KeyChain(String label, byte seed[]) throws Exception
    {
        if (seed == null)
            throw new Exception("no seed provided");

        randomseed = new long[4];

        generate(ByteUtil.concatenate(seed, new Sha256().encode(seed)));
    }

    public KeyChain(DataInputStream stream) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
//        PKCS8EncodedKeySpec specp = new PKCS8EncodedKeySpec(aPrivate.getBytes());
//
//        KeyFactory factory = KeyFactory.getInstance("ECDSA", "BC");
//
//        PrivateKey privateKey = factory.generatePrivate(specp);
//
//        this.privateKey = new PrivKey(privateKey);
//        this.publicKey  = new PubKey(factory.generatePrivate(specp));
//        this.address    = this.publicKey.getAddressForm(Parameters.MAIN_NETWORK_PUBLIC_ADDRESS_PREFIX);
    }

    private static String adjustTo64(String s) {
        switch(s.length()) {
            case 62: return "00" + s;
            case 63: return "0" + s;
            case 64: return s;
            default:
                throw new IllegalArgumentException("not a valid key: " + s);
        }
    }

    /** Generate an ECDSA Key Pair **/
    private void generate(byte seed[])
    {
        try
        {
//            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
//
//            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
//            keyPairGenerator.initialize(ecSpec);
//
//            KeyPair kp = keyPairGenerator.generateKeyPair();
//            PublicKey pub = kp.getPublic();
//            PrivateKey pvt = kp.getPrivate();
//
//            ECPrivateKey epvt = (ECPrivateKey)pvt;
//            String sepvt = adjustTo64(epvt.getS().toString(16)).toUpperCase();
//            System.out.println("s[" + sepvt.length() + "]: " + sepvt);


            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(seed);

            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");//"prime192v1");

            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();

            randomseed[0] = random.nextLong();
            randomseed[1] = random.nextLong();
            randomseed[2] = random.nextLong();
            randomseed[3] = random.nextLong();

            this.privateKey = new PrivKey(keyPair.getPrivate());
            this.publicKey  = new PubKey(keyPair.getPublic());
            this.address    = this.publicKey.getAddressForm(Parameters.MAIN_NETWORK_PUBLIC_ADDRESS_PREFIX);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void generateNewChain() throws Exception {
        childChain = new KeyChain("", ByteUtil.concatenate(privateKey.getBytes(), publicKey.getBytes(), ByteUtil.encode(randomseed[0]), ByteUtil.encode(randomseed[1]), ByteUtil.encode(randomseed[2]), ByteUtil.encode(randomseed[3])));
    }

    public Key getPublic() {
        return publicKey;
    }

    public Key getPrivate() {
        return privateKey;
    }

    @Override
    public String toString() {
        return "private: \n\t" + Base58.encode(this.privateKey.getBytes()) + "\npublic: \n\t" + Base58.encode(this.publicKey.getBytes()) + "\naddress: \n\t" + this.address;
    }
}