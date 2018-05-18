package com.riverssen.utils;

import com.riverssen.core.Config;
import com.riverssen.core.Logger;
import com.riverssen.core.security.PubKey;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.util.Base64;

public class HashUtil
{
    public static String HASH_ADDRESS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toLowerCase() + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

//    public static String toPublicAddress(byte key[])
//    {
//        String address = "";
//
//        for(byte b : key)
//        {
//            address += HASH_ADDRESS.charAt(b);
//        }
//
//        return address;
//    }

    public static String compressPublicKey(PubKey key)
    {
        ECPublicKey key1 = (ECPublicKey) key.getPublic();

        ECPoint w = key1.getW();

        return null;
    }

    public static byte[] base64Encode(byte data[])
    {
        return Base64.getUrlEncoder().withoutPadding().encode(data);
    }

    public static byte[] base64StringDecode(String data)
    {
        return Base64.getUrlDecoder().decode(data);
    }

    public static String base64Encode(String string)
    {
        return new String(base64Encode(string.getBytes()));
    }

    public static String base64StringEncode(byte data[])
    {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

//    public static String base64StringDecode(String string)
//    {
//        return new String(Base64.getUrlDecoder().decode(string));
//    }
//
//    public static String base64Decoded(byte data[])
//    {
//        return new String(base64StringDecode(data));
//    }

    public static String base36Encode(byte data[])
    {
        return new BigInteger(data).toString(36);
    }

    public static byte[] base36Decode(String data)
    {
        return new BigInteger(data, 36).toByteArray();
    }

    public static String createDifficultyString()
    {
        return new String(new byte[Config.getConfig().BLOCK_MINING_DIFFICULTY]).replaceAll("\0", "0");
    }

    public static byte[] applySha256(byte[] input)
    {
        return hashWith(input, "SHA-256");
    }

    public static byte[] applyBlake2b(byte[] input)
    {
        return hashWith(input, "BLAKE2B-256");
    }

    public static byte[] applySha3(byte[] input)
    {
        return hashWith(input, "SHA3-256");
    }

    public static byte[] applySha512(byte[] input)
    {
        return hashWith(input, "SHA-512");
    }

    public static byte[] applyKeccak(byte[] input)
    {
        return hashWith(input, "KECCAK-256");
    }

    public static byte[] applyRipeMD128(byte[] input) { return hashWith(input, "RIPEMD128"); }
    public static byte[] applyRipeMD160(byte[] input) { return hashWith(input, "RIPEMD160"); }
    public static byte[] applyRipeMD256(byte[] input) { return hashWith(input, "RIPEMD256"); }
    public static byte[] applyGost3411(byte[] input) { return hashWith(input, "GOST3411"); }

    public static byte[] hashWith(byte[] input, String instanceName)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance(instanceName, "BC");
            byte[] hash = digest.digest(input);
            return hash;
        } catch (Exception e)
        {
            System.out.println(Logger.COLOUR_RED + e.getMessage());
            return null;
        }
    }

    public static String hashToStringBase16(byte[] hash)
    {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++)
        {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public static String publicKeyExport(String key)
    {
        while(key.length() < 85) key += "\0" + key;

        return key;
    }

    public static String publicKeyImport(String key)
    {
        String k = "";

        for(int i = 0; i < key.length(); i ++)
            if(key.charAt(i) != '\0') k += key.charAt(i);

        return k;
    }

    public static String blockHashExport(String hash)
    {
        while(hash.length() < 256)
            hash = "\0" + hash;

        return hash;
    }

    public static String blockHashImport(String hash)
    {
        String k = "";

        for(int i = 0; i < hash.length(); i ++)
            if(hash.charAt(i) != '\0') k += hash.charAt(i);

        return k;
    }

    public static byte[] applySha1(byte[] bytes)
    {
        return hashWith(bytes, "SHA1-256");
    }
}
