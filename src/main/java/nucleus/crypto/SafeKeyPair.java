package nucleus.crypto;

import nucleus.exceptions.ECLibException;
import nucleus.util.ByteUtil;

import static nucleus.util.HashUtil.*;

/**
 * This class should always be used to generate a key-pair
 * It insures that the Keypair, seed, and generated data are all valid for use.
 * It returns an immediate seed for generating keypairs the unsafe way, and a
 * valid keypair.
 */
public class SafeKeyPair
{
    private long nonce;
    private KeyPair pair;
    private byte seed[];

    public SafeKeyPair(byte seed[])
    {
        generate(seed);
    }

    private void generate(final byte seed[])
    {
        byte realSeed[] = applySha256(applySha256(ByteUtil.concatenate(seed, ByteUtil.encode(nonce))));
        nonce ++;

        try
        {
            pair = new KeyPair(realSeed);
            this.seed = realSeed;
        } catch (ECLibException e)
        {
            generate(seed);
        }
    }

    public KeyPair get()
    {
        return pair;
    }

    public byte[] getSeed()
    {
        return seed;
    }
}