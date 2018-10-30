package com.riverssen.wallet.ec;

import com.riverssen.core.algorithms.Sha256;
import com.riverssen.core.security.CompressedAddress;
import com.riverssen.core.utils.Base58;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.wallet.Key;
import com.riverssen.wallet.PublicAddress;
import org.web3j.crypto.Sign;

import java.math.BigInteger;

public class DerivedKey implements Key
{
    private byte key[];

    public DerivedKey(CompressedAddress key, byte prefix)
    {
        BigInteger publIc = org.web3j.crypto.Sign.publicKeyFromPrivate(key.decompress());

        byte extended[] = ByteUtil.concatenate(new byte[] {prefix}, publIc.toByteArray());

        byte sha2sha2[] = new Sha256().encode(new Sha256().encode(extended));

        byte checksum[] = ByteUtil.trim(sha2sha2, 28, 32);

        this.key = ByteUtil.concatenate(extended, checksum);
    }

    public DerivedKey(BigInteger key, byte prefix)
    {
        BigInteger publIc = org.web3j.crypto.Sign.publicKeyFromPrivate(key);

        byte extended[] = ByteUtil.concatenate(new byte[] {prefix}, publIc.toByteArray());

        byte sha2sha2[] = new Sha256().encode(new Sha256().encode(extended));

        byte checksum[] = ByteUtil.trim(sha2sha2, 28, 32);

        this.key = ByteUtil.concatenate(extended, checksum);
    }

    public static boolean checkKeyValid(byte[] key, byte prefix)
    {
        byte extended[] = ByteUtil.concatenate(new byte[] {prefix}, ByteUtil.trim(key, 1, 66));

        byte sha2sha2[] = new Sha256().encode(new Sha256().encode(extended));

        byte checksum[] = ByteUtil.trim(sha2sha2, 28, 32);

        return ByteUtil.equals(ByteUtil.concatenate(extended, checksum), key);
    }

    public BigInteger getRawKey()
    {
        return new BigInteger(ByteUtil.trim(key, 1, 66));
    }

    @Override
    public String toString()
    {
        return Base58.encode(key);
    }

    @Override
    public boolean fromBytes(byte[] bytes)
    {
        return false;
    }

    @Override
    public byte[] getBytes()
    {
        return key;
    }

    @Override
    public byte[] sign(byte[] bytes, byte[] encryption)
    {
        return null;
    }

    @Override
    public boolean verify(byte[] signature, byte[] data) {
        return false;
    }

    @Override
    public boolean decrypt(byte key) {
        return false;
    }

    @Override
    public boolean encrypt(byte key) {
        return false;
    }

    public static String compressPubKey(BigInteger pubKey) {
        String pubKeyYPrefix = pubKey.testBit(0) ? "03" : "02";
        String pubKeyHex = pubKey.toString(16);
        String pubKeyX = pubKeyHex.substring(0, 64);
        return pubKeyYPrefix + pubKeyX;
    }

    @Override
    public Key getCompressedForm() {
        return new CompressedAddress(compressPubKey(getRawKey()));
    }

    @Override
    public PublicAddress getAddressForm(byte prefix) {
        return null;
    }
}
