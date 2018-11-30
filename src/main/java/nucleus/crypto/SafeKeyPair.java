package nucleus.crypto;

import nucleus.exceptions.ECLibException;
import nucleus.util.ByteUtil;

import static nucleus.util.HashUtil.*;

public class SafeKeyPair
{
    private long nonce;
    private KeyPair pair;

    public SafeKeyPair(byte seed[])
    {
        generate(seed);
    }

    private void generate(final byte seed[])
    {
        byte realSeed[] = applySha256(ByteUtil.concatenate(seed, ByteUtil.encode(nonce)));
        nonce ++;

        try
        {
            pair = new KeyPair(realSeed);
        } catch (ECLibException e)
        {
            generate(seed);
        }
    }

    public KeyPair get()
    {
        return pair;
    }
}