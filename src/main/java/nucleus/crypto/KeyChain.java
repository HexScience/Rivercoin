package nucleus.crypto;

import nucleus.exceptions.ECLibException;
import nucleus.protocols.protobufs.Address;
import nucleus.protocols.transaction.Signature;
import nucleus.protocols.transaction.Transaction;
import nucleus.protocols.transaction.TransactionData;
import nucleus.protocols.transaction.TransactionInput;
import nucleus.system.Context;
import nucleus.system.Parameters;
import nucleus.util.Base58;
import nucleus.util.ByteUtil;

import javax.crypto.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedList;
import java.util.List;

public class KeyChain
{
    public static final long MAGIC_HEADER = 0xfff16;
    private byte[]                  seed;
    private int                     numIterations;
    private LinkedList<KeyPair>     keyPair;

    byte[] salt = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };

    public KeyChain(byte seed[]) throws ECLibException
    {
        this.seed = seed;
        this.keyPair = new LinkedList<>();

        this.keyPair.add(new SafeKeyPair(seed).get());
    }

    public KeyChain() throws ECLibException
    {
        this.keyPair = new LinkedList<>();
    }

    public KeyChain generate() throws ECLibException
    {
        KeyPair lastKeypair = pair();

        keyPair.add(new KeyPair(new MnemonicPhraseSeeder(ByteUtil.concatenate(seed, lastKeypair.getPrivateKey().toByteArray())).getSeed()));
        numIterations ++;

        return this;
    }

    /**
     * @param data
     * @return A valid ECDSA Signature.
     *
     * The function generates a signature and then generates a new Keypair for improved safety,
     * The old keypair and seed are left in the chain for further usability, however the newly
     * generated keypair should be used to send and or receive any transactions in the future.
     */
    public Signature generateSignature(byte data[]) throws ECLibException
    {
        generate();
        return new Signature();
    }

    public Transaction send(Context context, List<TransactionData> data)
    {
        long totalAmount = 0;

        for (TransactionData transactionData : data)
        {
            if (transactionData.getAmount() >= Parameters.MINIMUM_TRANSACTION)
                totalAmount += transactionData.getAmount();
        }

        Address current = pair().getAddress();
        Signature signature = new Signature();
        signature.setPrivateKey(pair().getPrivateKeyObject());

        TransactionInput inputs[] = context.getLedger().getBalanceTable(current).getOutputs(totalAmount, signature.getSignature());

        return null;
    }

    public void write(String password, DataOutputStream stream) throws IOException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException
    {
        PBE pbe = new PBE();
        String encrypted = pbe.encrypt(password, Base58.encode(ByteUtil.concatenate(seed, ByteUtil.encodei(numIterations))));

//        byte[] data = Base58.decode(Base58.encode(ByteUtil.concatenate(seed, ByteUtil.encodei(numIterations))));
//        byte seed[] = ByteUtil.trim(data, 0, 32);
//        int numInterations = ByteUtil.decodei(ByteUtil.trim(data, 32, 36));
//
//        System.out.println(numInterations + " " + this.numIterations);
//        System.out.println(ByteUtil.equals(seed, this.seed));

        stream.writeLong(MAGIC_HEADER);
        stream.writeUTF(encrypted);
    }

    public void read(String password, DataInputStream stream) throws IOException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException, ECLibException
    {
        String encrypted = stream.readUTF();

        PBE pbe = new PBE();
        String decrypted = pbe.decrypt(password, encrypted);

        byte[] data = Base58.decode(decrypted);
        seed = ByteUtil.trim(data, 0, 32);
        int numInterations = ByteUtil.decodei(ByteUtil.trim(data, 32, 36));

        this.keyPair.add(new KeyPair(seed));

        while (this.numIterations < numInterations)
            generate();
    }

    public KeyPair pair()
    {
        return keyPair.getLast();
    }
}