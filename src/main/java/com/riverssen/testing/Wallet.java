package com.riverssen.testing;

public class Wallet {
    public static final void test()
    {
        String nameOfWallet = "name"; //unimportant
        String seedOfWallet = "seed"; //extremely important and must be secure + unique.
        com.riverssen.core.security.Wallet
                    wallet  = new com.riverssen.core.security.Wallet(nameOfWallet, seedOfWallet);

        //Name Of Keypair Is Not Important To Security.
        wallet.generateNewKeyPair("test pair a");
        wallet.generateNewKeyPair("test pair b");
        wallet.generateNewKeyPair("test pair c");
        wallet.generateNewKeyPair("test pair d");
        wallet.generateNewKeyPair("test pair e");
        wallet.generateNewKeyPair("test pair f");
        wallet.generateNewKeyPair("test pair g");
        wallet.generateNewKeyPair("test pair h");
        wallet.generateNewKeyPair("test pair i");
        wallet.generateNewKeyPair("test pair j");
        wallet.generateNewKeyPair("test pair k");
        wallet.generateNewKeyPair("test pair l");
        wallet.generateNewKeyPair("test pair m");
        wallet.generateNewKeyPair("test pair n");

        System.out.println(wallet.getPublicAddress(0));
        System.out.println(wallet.getPublicAddress(1));
        System.out.println(wallet.getPublicAddress(2));
        System.out.println(wallet.getPublicAddress(3));
        System.out.println(wallet.getPublicAddress(4));
        System.out.println(wallet.getPublicAddress(5));
        System.out.println(wallet.getPublicAddress(6));
        System.out.println(wallet.getPublicAddress(7));
        System.out.println(wallet.getPublicAddress(8));
        System.out.println(wallet.getPublicAddress(9));
        System.out.println(wallet.getPublicAddress(10));
        System.out.println(wallet.getPublicAddress(11));
        System.out.println(wallet.getPublicAddress(12));
        System.out.println(wallet.getPublicAddress(13));
        System.out.println(wallet.getPublicAddress(14));

        //Going out of lists bounds will overflow the index and go back to 0
        System.out.println("overflow address: " + wallet.getPublicAddress(15));

        System.exit(0);
    }
}
