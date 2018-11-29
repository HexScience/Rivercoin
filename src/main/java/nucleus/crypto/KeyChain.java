package nucleus.crypto;

public class KeyChain
{
    private KeyPair keyPair;

    public KeyChain(byte seed[])
    {
        this.keyPair = new KeyPair(seed);
    }
}