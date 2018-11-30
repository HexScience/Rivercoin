package tests;

import nucleus.crypto.KeyChain;
import nucleus.crypto.MnemonicPhraseSeeder;
import nucleus.crypto.ec.ECLib;
import nucleus.exceptions.ECLibException;
import nucleus.util.Base58;
import nucleus.util.ByteUtil;

public class WalletGeneration
{
    public static void main() throws ECLibException
    {
        MnemonicPhraseSeeder seeder = new MnemonicPhraseSeeder();

        byte[] seed = seeder.getSeed();

        System.out.println(seeder.getString());
        System.out.println(Base58.encode(seed));

        KeyChain chain = new KeyChain(seed);

        System.out.println(ByteUtil.equals(chain.pair().getPublicKey(), ECLib.ECPublicKeyFromCompressed(chain.pair().getPublicKey(true)).getQ().getEncoded(false)));

        System.out.println(chain.pair());

        for (int i = 0; i < 100; i ++)
            System.out.println(chain.generate().pair());
    }
}