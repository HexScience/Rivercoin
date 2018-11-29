package tests;

import nucleus.crypto.KeyChain;
import nucleus.crypto.MnemonicPhraseSeeder;
import nucleus.exceptions.ECLibException;
import nucleus.util.Base58;

public class WalletGeneration
{
    public static void main() throws ECLibException
    {
        MnemonicPhraseSeeder seeder = new MnemonicPhraseSeeder();

        byte[] seed = seeder.getSeed();

        System.out.println(seeder.getString());
        System.out.println(Base58.encode(seed));

        KeyChain chain = new KeyChain(seed);

        System.out.println(chain.pair());

        for (int i = 0; i < 100; i ++)
            System.out.println(chain.generate().pair());
    }
}