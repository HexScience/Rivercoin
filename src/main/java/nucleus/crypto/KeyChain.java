package nucleus.crypto;

import nucleus.exceptions.ECLibException;
import nucleus.util.ByteUtil;

import java.util.LinkedList;

public class KeyChain
{
    private byte[]                  seed;
    private LinkedList<KeyPair>     keyPair;

    public KeyChain(byte seed[]) throws ECLibException
    {
        this.seed = seed;
        this.keyPair = new LinkedList<>();

        this.keyPair.add(new KeyPair(seed));
    }

    public KeyChain generate() throws ECLibException
    {
        KeyPair lastKeypair = keyPair.getLast();

        keyPair.add(new KeyPair(new MnemonicPhraseSeeder(ByteUtil.concatenate(seed, lastKeypair.getPrivateKey().toByteArray())).getSeed()));

        return this;
    }

    public void write()
    {
    }

    public void read()
    {
    }

    public KeyPair pair()
    {
        return keyPair.getLast();
    }
}