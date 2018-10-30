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

package com.riverssen.wallet;

import com.riverssen.core.security.CompressedAddress;
import com.riverssen.core.security.Wallet;
import com.riverssen.core.system.Config;
import com.riverssen.core.system.Logger;
import com.riverssen.core.utils.Base58;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.core.utils.HashUtil;
import org.bouncycastle.jce.ECNamedCurveTable;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.Key;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.Arrays;

public class PubKey implements com.riverssen.wallet.Key
{
    public static final int SIZE_IN_BYTES = 85;

    private PublicKey           key;

    public PubKey(CompressedAddress address) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        org.bouncycastle.jce.spec.ECNamedCurveParameterSpec SPEC = ECNamedCurveTable.getParameterSpec("secp256k1");

        org.bouncycastle.math.ec.ECPoint point = SPEC.getCurve().decodePoint(address.getBytes());
        BigInteger x = point.getXCoord().toBigInteger();//.getEncoded();
        BigInteger y = point.getYCoord().toBigInteger();//.getEncoded();
//         concat 0x04, x, and y, make sure x and y has 32-bytes:
//        byte bytes[] = (ByteUtil.concatenate(new byte[] {0x04}, x, y));

//        X509EncodedKeySpec specp = new X509EncodedKeySpec(bytes);

//        KeyFactory factory = KeyFactory.getInstance("ECDSA", "BC");
//
//        this.key = factory.generatePublic(specp);

        ECPoint point2 = new ECPoint(x, y);
        ECPublicKeySpec pubSpec = new ECPublicKeySpec(point2, ((ECPublicKey) KeyChain.defaultPublicKey).getParams());

        this.key = KeyChain.Factory.generatePublic(pubSpec);
    }

    public PubKey(PublicKey key)
    {
        this.key = key;
    }

    public boolean verifySignature(String data, String signature)
    {
        return verifySignature(data.getBytes(), signature);
    }

    public boolean verifySignature(byte data[], String signature)
    {
        if(signature == null) return false;
        if(signature.length() == 0) return false;
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(key);
            ecdsaVerify.update(data);
            return ecdsaVerify.verify(Base58.decode(signature));
        }catch(Exception e) {
            Logger.err("couldn't verify signature!");
            e.printStackTrace();
        }

        return false;
    }

    public boolean verifySignature(byte data[], byte signature[])
    {
        if(signature == null) return false;
        if(signature.length == 0) return false;
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(key);
            ecdsaVerify.update(data);
            return ecdsaVerify.verify(signature);
        }catch(Exception e) {
            Logger.err("couldn't verify signature!");
            e.printStackTrace();
        }

        return false;
    }

    public byte[] sign(String data)
    {
        return sign(data.getBytes());
    }

    @Override
    public boolean fromBytes(byte[] bytes)
    {
        return true;
    }

    @Override
    public byte[] getBytes() {
        return this.key.getEncoded();
    }

    public byte[] sign(byte data[])
    {
        return null;
    }

    @Override
    public byte[] sign(byte[] bytes, byte[] encryption) {
        return new byte[0];
    }

    @Override
    public boolean verify(byte[] signature, byte[] data) {
        return verifySignature(data, signature);
    }

    @Override
    public boolean decrypt(byte key) {
        return false;
    }

    @Override
    public boolean encrypt(byte key) {
        return false;
    }

    @Override
    public com.riverssen.wallet.Key getCompressedForm() {
        return getCompressed();
    }

    @Override
    public PublicAddress getAddressForm(byte prefix) {
        return publicKeyToAddress(prefix);
    }

    public Key getPublic()
    {
        return key;
    }

    private PublicAddress publicKeyToAddress(byte prefix)
    {
        try{
            ECPublicKey key = (ECPublicKey) this.key;

            ECPoint point = key.getW();

            byte keyprefixbyte[] = {0x04};

            byte sha256[]   = HashUtil.applySha256(ByteUtil.concatenate(keyprefixbyte, point.getAffineX().toByteArray(), point.getAffineY().toByteArray()));
            byte sha2562[]  = HashUtil.applySha256(sha256);
            byte ripeMD[]   = HashUtil.applyRipeMD160(sha2562);

            byte version    = prefix;
            byte key_21[]   = ByteUtil.concatenate(new byte[] {version}, ripeMD);

            byte checksum[] = ByteUtil.trim(HashUtil.applySha256(HashUtil.applySha256(key_21)), 0, 4);

            return new PublicAddress(Base58.encode(ByteUtil.concatenate(key_21, checksum)));
        } catch (Exception e)
        {
            Logger.err("couldn't convert publickey to publicaddresskey");
            e.printStackTrace();
        }

        return new PublicAddress("error");
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

    public CompressedAddress getCompressed()
    {
        ECPublicKey key = (ECPublicKey) this.key;
        ECPoint point = key.getW();

        byte x[] = point.getAffineX().toByteArray();
        byte y[] = point.getAffineY().toByteArray();

        BigInteger primeY = new BigInteger(y);

        byte theKey[] = new byte[1];

        /** if Y is even **/
        if (primeY.mod(new BigInteger("2")).equals("0"))
            theKey[0] = 0x02;
        /** but if it "can't even", jokes lol. **/
        else
            theKey[0] = 0x03;

        return new CompressedAddress(ByteUtil.concatenate(theKey, removeSignByte(x)));
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof PubKey) return key.equals(((PubKey) obj).key);
        return false;
    }

    public boolean isValid()
    {
        return key != null;
    }

    public String toString()
    {
        return Base58.encode(getBytes());
    }

    public static byte[] getBytes(String keyAddress)
    {
        return Base58.decode(keyAddress);
    }

    public PublicKey getKey() {
        return key;
    }

//    public PublicAddress getAddress()
//    {
//        return wad;
//    }
}
