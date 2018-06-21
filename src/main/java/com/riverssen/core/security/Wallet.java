/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Riverssen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.riverssen.core.security;

import com.riverssen.core.Logger;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.core.utils.HashUtil;
import com.riverssen.core.utils.Truple;
import com.riverssen.core.utils.Tuple;

import java.io.*;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

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

    private Set<Truple<String, PrivKey, PubKey>> keyPairs;
    private String  name;

    public Wallet(String name, String seed)
    {
        this(name, seed.getBytes());
    }

    public Wallet(String name, byte   seed[])
    {
        this.name       = name;
        this.keyPairs   = new LinkedHashSet<>();
        generateNewKeyPair("default master keys", ByteUtil.concatenate(seed, HashUtil.applySha256(seed), HashUtil.applySha512("default master keys".getBytes())));
    }

    public PrivKey getPrivateKey()
    {
        return getPrivateKey(0);
    }

    public PrivKey getPrivateKey(int i)
    {
        int index = 0;

        for(Truple<String, PrivKey, PubKey> keyPair : keyPairs)
            if(index ++ == i)
                return keyPair.getJ();

        return keyPairs.iterator().next().getJ();
    }

    public int generateNewKeyPair(String name)
    {
        return this.generateNewKeyPair(name, getNewSeed());
    }

    private byte[] getNewSeed()
    {
        byte seed[] = ByteUtil.encode(keyPairs.size());

        for(Truple<String, PrivKey, PubKey> keyPair : keyPairs)
        {
            byte privHash[] = HashUtil.applySha512(keyPair.getJ().getPrivate().getEncoded());
            byte publHash[] = HashUtil.applySha512(keyPair.getC().getPublic().getEncoded());

            seed            = ByteUtil.concatenate(privHash, publHash, HashUtil.applySha512(ByteUtil.concatenate(privHash, publHash, seed)));
        }

        return ByteUtil.concatenate(seed, HashUtil.applySha512(seed), ByteUtil.encode(seed.length));
    }

    public int generateNewKeyPair(String name, byte seed[])
    {
        try
        {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(seed);

            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();

            keyPairs.add(new Truple<>("default", new PrivKey(keyPair.getPrivate()), new PubKey(keyPair.getPublic())));
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return keyPairs.size() - 1;
    }

    public PubKey getPublicKey()
    {
        return getPublicKey(0);
    }

    public PubKey getPublicKey(int i)
    {
        int index = 0;

        for(Truple<String, PrivKey, PubKey> keyPair : keyPairs)
            if(index ++ == i)
                return keyPair.getC();

        return keyPairs.iterator().next().getC();
    }

    public void export(String password, ContextI context)
    {
        try{
            File diry = new File(context.getConfig().getBlockChainWalletDirectory() + name + "//");
            diry.mkdirs();

            File file = new File(context.getConfig().getBlockChainWalletDirectory() + name + "//" + name + ".rwt");
            File pub = new File(context.getConfig().getBlockChainWalletDirectory() + name + "//readme.txt");

            FileOutputStream writer = new FileOutputStream(file);
            DataOutputStream stream = new DataOutputStream(writer);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void export(PrivKey privateKey, PubKey publicKey, String password, ContextI context)
    {
        try
        {
            File diry = new File(context.getConfig().getBlockChainWalletDirectory() + name + "//");
            diry.mkdirs();
            File file = new File(context.getConfig().getBlockChainWalletDirectory() + name + "//" + name + ".rwt");
            File pub = new File(context.getConfig().getBlockChainWalletDirectory() + name + "//readme.txt");

            FileOutputStream writer = new FileOutputStream(file);
            DataOutputStream stream = new DataOutputStream(writer);

            byte out[] = privateKey.getPrivate().getEncoded();

            if (password != null)
            {
                AdvancedEncryptionStandard aes = new AdvancedEncryptionStandard(password.getBytes());
                out = aes.encrypt(out);
            }

            stream.writeShort(publicKey.getPublic().getEncoded().length);
            stream.writeShort(out.length);

            stream.write(publicKey.getPublic().getEncoded());
            stream.write(out);

            stream.flush();
            stream.close();

            final String api = "https://api.qrserver.com/v1/create-qr-code/?size=512x512&data=" + publicKey.getCompressed();

            FileWriter writer1 = new FileWriter(pub);

            writer1.write("This file and the image are public, so don't worry about sharing it with people!\n\n");
            writer1.write(publicKey.getCompressed().toString());
            writer1.write("\n\n\t<3 Riverssen\n");

            writer1.flush();
            writer1.close();

            Logger.prt(Logger.COLOUR_BLUE + "wallet[" + name + "] created.");
        } catch (Exception e)
        {
            Logger.err("couldn't export wallet!");
            e.printStackTrace();
        }
    }
}