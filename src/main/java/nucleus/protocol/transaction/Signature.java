package nucleus.protocol.transaction;

import nucleus.crypto.ec.ECDerivedPublicKey;

public class Signature
{
    private ECDerivedPublicKey  ecKey;
    private byte[]              signature;
}