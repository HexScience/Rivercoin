package com.riverssen.core.security;

import com.riverssen.core.Config;
import com.riverssen.core.Logger;
import com.riverssen.core.RiverCoin;
import com.riverssen.utils.Tuple;
import com.riverssen.core.chain.WalletOutputInput;
import com.riverssen.utils.FileUtils;

import java.io.*;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.List;

public class Wallet
{
    public static       KeyFactory Factory = null;
    public static final Wallet     DEFAULT = new Wallet("DefaultWallet", "DefaultWallet");

    static {
        try{
            Factory = KeyFactory.getInstance("ECDSA", "BC");
        } catch (Exception e)
        {
            Logger.err("KeyFactory couldn't be created!");
            e.printStackTrace();
            System.exit(0);
        }
    }

    private PrivKey privateKey;
    private PubKey  publicKey;
    private String  name;

    public static synchronized void updateBalance(TXIO txio)
    {
        //TODO: add latest blockID to insure correct ledger keeping.

        synchronized (txio)
        {
            for(String address : txio.getAddresses().keySet())
                updateBalance(address, txio.get(address).get());
        }
    }

    private static synchronized void updateBalance(String address, Tuple<List<Tuple<RiverCoin, String>>, List<Tuple<RiverCoin, String>>> io)
    {
        File file = new File(Config.getConfig().BLOCKCHAIN_WLT_DB + File.separator /**+ "temp\\" +**/ + address);//HashUtil.base36Encode(HashUtil.applySha256(address.getBytes())));

//        BigInteger balance = readBalance(address);

//        if(file.exists())
//        {
        WalletOutputInput wlt = new WalletOutputInput();
        wlt.addAll(io);

        if(file.exists())
        wlt.readFromFile(file);
//        }

        BigInteger balance = BigInteger.ZERO;

        for(Tuple<RiverCoin, String> tx : io.getI())
            balance = balance.add(tx.getI().toBigInteger());

        for(Tuple<RiverCoin, String> tx : io.getJ())
            balance = balance.subtract(tx.getI().toBigInteger());

//        FileUtils.writeUTF(file, balance.toString());

        wlt.exportToFile(file);
    }
//
//    public static synchronized BigInteger readBalance(String address)
//    {
//        return readBalance(address, null);
//    }

    public static synchronized BigInteger readBalance(String address)//, Tuple<List<Tuple<RiverCoin, String>>, List<Tuple<RiverCoin, String>>> io)
    {
        FileUtils.moveFromTemp(Config.getConfig().BLOCKCHAIN_WLT_DB);

        File file = new File(Config.getConfig().BLOCKCHAIN_WLT_DB + File.separator + address);//HashUtil.base36Encode(HashUtil.applySha256(address.getBytes())));

        if(!file.exists()) return BigInteger.ZERO;

//        String balance = FileUtils.readUTF(Config.getConfig().BLOCKCHAIN_WLT_DB + File.separator + HashUtil.base36Encode(HashUtil.applySha256(address.getBytes())));
//
//        if(balance.isEmpty()) return BigInteger.ZERO;
//        try{
//            BigInteger integer = new BigInteger(balance);
//            return integer;
//        } catch (Exception e) {}

        WalletOutputInput wlt = new WalletOutputInput();

        if(file.exists())
            wlt.readFromFile(file);
//        if(io != null)
//            wlt.addAll(io);
//        }

        BigInteger balance = BigInteger.ZERO;

        for(Tuple<RiverCoin, String> tx : wlt.get().getI())
            balance = balance.add(tx.getI().toBigInteger());

        for(Tuple<RiverCoin, String> tx : wlt.get().getJ())
            balance = balance.subtract(tx.getI().toBigInteger());

        return balance;
    }

    public Wallet(String name, String seed)
    {
        this(name, seed.getBytes());
    }

    public Wallet(String name, byte   seed[])
    {
        this.name = name;
        try
        {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(seed);
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();

            this.privateKey = new PrivKey(keyPair.getPrivate());
            this.publicKey  = new PubKey(keyPair.getPublic());
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public Wallet(PubKey key)
    {
        this.publicKey = key;
    }

    public PrivKey getPrivateKey()
    {
        return privateKey;
    }

    public void setPrivateKey(PrivKey privateKey)
    {
        this.privateKey = privateKey;
    }

    public PubKey getPublicKey()
    {
        return publicKey;
    }

    public void setPublicKey(PubKey publicKey)
    {
        this.publicKey = publicKey;
    }

    public void export()
    {
        export(null);
    }

    public void export(String password)
    {
        try
        {
            File diry = new File(Config.getConfig().WALLET_DIRECTORY + name + "//");
            diry.mkdirs();
            File file = new File(Config.getConfig().WALLET_DIRECTORY + name + "//" + name + ".rwt");
            File pub = new File(Config.getConfig().WALLET_DIRECTORY + name + "//readme.txt");

//            ByteBuffer buffer = ByteBuffer.allocate(4);

            FileOutputStream writer = new FileOutputStream(file);
            DataOutputStream stream = new DataOutputStream(writer);

            byte out[] = privateKey.getPrivate().getEncoded();

            if (password != null)
            {
                AdvancedEncryptionStandard aes = new AdvancedEncryptionStandard(password.getBytes());
                out = aes.encrypt(out);
            }

//            buffer.putShort((short) out.length);
//            buffer.putShort((short) publicKey.getPublic().getEncoded().length);
//
//            buffer.flip();

            stream.writeShort(publicKey.getPublic().getEncoded().length);
            stream.writeShort(out.length);

            stream.write(publicKey.getPublic().getEncoded());
            stream.write(out);

            stream.flush();
            stream.close();

//            writer.write(buffer.array());
//
//            writer.write(publicKey.getPublic().getEncoded());
//            writer.write(out);
//
//            writer.flush();
//            writer.close();

            final String api = "https://api.qrserver.com/v1/create-qr-code/?size=512x512&data=" + publicKey.getPublicAddress();

            FileWriter writer1 = new FileWriter(pub);

            writer1.write("This file and the image are public, so don't worry about sharing it with people!\n\n");
            writer1.write(publicKey.getPublicAddress().toString());
            writer1.write("\n\n\t<3 Riverssen\n");

            writer1.flush();
            writer1.close();

//            try
//            {
//                BufferedImage image = ImageIO.read(new URL(api));
//
//                ImageIO.write(image, "PNG", new File(Config.getConfig().WALLET_DIRECTORY + name + "//img.png"));
//            } catch (IOException e)
//            {
//                e.printStackTrace();
//            }

            Logger.prt(Logger.COLOUR_BLUE + "wallet[" + name + "] created.");
        } catch (Exception e)
        {
            Logger.err("couldn't export wallet!");
            e.printStackTrace();
        }
    }
}