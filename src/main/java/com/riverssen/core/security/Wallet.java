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

import com.riverssen.core.Config;
import com.riverssen.core.Logger;

import java.io.*;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;

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