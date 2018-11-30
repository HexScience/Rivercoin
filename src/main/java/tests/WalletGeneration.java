package tests;

import nucleus.crypto.MnemonicPhraseSeeder;
import nucleus.crypto.Wallet;
import nucleus.crypto.ec.ECLib;
import nucleus.util.FileService;

public class WalletGeneration
{
    public static void main(String... args) throws Throwable
    {
        ECLib.init();

        Wallet wallet = new Wallet(new FileService("potato-test.nwf"), "potat", new MnemonicPhraseSeeder("potat".getBytes()).getSeed());
        Wallet b      = wallet.clone(new FileService("potato-test2.nwf"));

        for (int i = 0; i < 12; i ++)
        {
            wallet.newKeyPair("potat");
            b.newKeyPair("potat");

            String pubkey_a = wallet.getBase58EncodedPublicKey(false);

            wallet = new Wallet(new FileService("potato-test.nwf"), "potat");

            String pubkey_b = wallet.getBase58EncodedPublicKey(false);

            System.out.println(pubkey_a + " " + pubkey_a.equals(pubkey_b) + " " + pubkey_a.equals(b.getBase58EncodedPublicKey(false)));
        }
//        MnemonicPhraseSeeder seeder = new MnemonicPhraseSeeder();
//
//        byte[] seed = seeder.getSeed();
//
//        System.out.println(seeder.getString());
//        System.out.println(Base58.encode(seed));
//
//        KeyChain chain = new KeyChain(seed);
//
//        System.out.println(ByteUtil.equals(chain.pair().getPublicKey(), ECLib.ECPublicKeyFromCompressed(chain.pair().getPublicKey(true)).getQ().getEncoded(false)));
//
//        System.out.println(chain.pair());
//
//        for (int i = 0; i < 100; i ++)
//            System.out.println(chain.incrementPair().pair());
    }
}