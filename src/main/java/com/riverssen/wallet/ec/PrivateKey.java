package com.riverssen.wallet.ec;

import com.riverssen.core.algorithms.RipeMD160;
import com.riverssen.core.algorithms.Sha256;
import com.riverssen.core.algorithms.Sha3;
import com.riverssen.core.utils.Base58;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.system.Parameters;
import org.bouncycastle.crypto.ec.ECPair;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.*;

import java.math.BigInteger;
import java.security.SignatureException;

public class PrivateKey
{
    private byte       key[];

    public PrivateKey(String seedphrases, byte prefix)
    {
        byte sha256[] = new Sha256().encode(seedphrases.getBytes());

        byte extended[] = ByteUtil.concatenate(new byte[] {prefix}, sha256);

        byte sha2sha2[] = new Sha256().encode(new Sha256().encode(extended));

        byte checksum[] = ByteUtil.trim(sha2sha2, 28, 32);

        this.key = (ByteUtil.concatenate(extended, checksum));
    }

    public static boolean checkKeyValid(byte[] key, byte prefix)
    {
        byte extended[] = ByteUtil.concatenate(new byte[] {prefix}, ByteUtil.trim(key, 1, 33));

        byte sha2sha2[] = new Sha256().encode(new Sha256().encode(extended));

        byte checksum[] = ByteUtil.trim(sha2sha2, 28, 32);

        return ByteUtil.equals(ByteUtil.concatenate(extended, checksum), key);
    }

    public BigInteger getRawKey()
    {
        return new BigInteger(ByteUtil.trim(key, 1, 33));
    }

//    @Override
    public byte[] sign(BigInteger publiC, byte[] bytes, byte[] encryption)
    {
        Sign.SignatureData signature = Sign.signMessage(Hash.sha3(bytes), new ECKeyPair(this.getRawKey(), publiC), false);

        return ByteUtil.concatenate(new byte[] {signature.getV()}, signature.getR(), signature.getS());
    }

    @Override
    public String toString()
    {
        return Base58.encode(key);
    }
}
