package nucleus.crypto;

import nucleus.exceptions.*;
import nucleus.protocols.protobufs.Address;
import nucleus.protocols.transaction.Signature;
import nucleus.protocols.transaction.Transaction;
import nucleus.protocols.transaction.TransactionData;
import nucleus.protocols.transaction.TransactionInput;
import nucleus.system.Context;
import nucleus.system.Parameters;
import nucleus.util.Base58;
import nucleus.util.ByteUtil;
import nucleus.util.FileService;

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
    public static final long        MAGIC_HEADER = 0xfff16;
    private FileService             location;
    private int                     numIterations;
    private LinkedList<KeyPair>     keyPair;


    /**
     * @param service The save-to location of this wallet.
     * @param password The encryption password.
     * @param rawSeed The seed for generating the wallet information.
     * @throws Throwable
     *
     * This function generates a keychain and saves it to file.
     */
    public KeyChain(FileService service, String password, final byte rawSeed[]) throws Throwable
    {
        this.keyPair = new LinkedList<>();
        this.numIterations = 0;
        this.location = service;

        byte seed[] = generateFirstPair(rawSeed);
        write(password, seed, service.as(DataOutputStream.class));
    }

    /**
     * @param service The load-from location of this wallet.
     * @param password The decryption password.
     * @throws Throwable
     *
     * This function reads wallet information from file.
     */
    public KeyChain(FileService service, String password) throws Throwable
    {
        this.location = service;

        read(password, service.as(DataInputStream.class));
    }

    private byte[] generateFirstPair(byte seed[])
    {
        SafeKeyPair pair = new SafeKeyPair(seed);

        this.keyPair.add(pair.get());

        return pair.getSeed();
    }

    public KeyChain incrementPair(String password, byte seed[]) throws Throwable
    {
//        keyPair.add(new SafeKeyPair(new MnemonicPhraseSeeder(ByteUtil.concatenate(seed, lastKeypair.getPrivateKey().toByteArray())).getSeed()).get());
        SafeKeyPair pair = new SafeKeyPair(generateNewSeed(seed));

        keyPair.add(pair.get());

        write(password, pair.getSeed(), location.as(DataOutputStream.class));

        return this;
    }

    private byte[] generateNewSeed(byte seed[])
    {
        byte nseed[] = new MnemonicPhraseSeeder(seed).getSeed();

        numIterations ++;

        return nseed;
    }

    /**
     * @param data
     * @return A valid ECDSA Signature.
     *
     * The function generates a signature and then generates a new Keypair for improved safety,
     * The old keypair and seed are left in the chain for further usability, however the newly
     * generated keypair should be used to send and or receive any transactions in the future.
     */
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
//        signature.setPrivateKey(pair().getPrivateKeyObject());

        try
        {
            TransactionInput inputs[] = context.getLedger().getBalanceTable(current).getOutputs(totalAmount, signature.getSignature());
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (FileServiceException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param password Encryption password.
     * @param seed The origin seed.
     * @param stream
     * @throws IOException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException
     */
    private void writeFirst(String password, byte seed[], DataOutputStream stream) throws IOException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException
    {
        PBE pbe = new PBE();
        String encrypted = pbe.encrypt(password, Base58.encode(ByteUtil.concatenate(seed, ByteUtil.encodei(numIterations))));

        stream.writeLong(MAGIC_HEADER);
        stream.writeUTF(encrypted);
    }

    private void write(String password, byte seed[], DataOutputStream stream) throws IOException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException
    {
        PBE pbe = new PBE();
        String encrypted = pbe.encrypt(password, Base58.encode(ByteUtil.concatenate(seed, ByteUtil.encodei(numIterations))));

        stream.writeLong(MAGIC_HEADER);
        stream.writeUTF(encrypted);
    }

    private void read(String password, DataInputStream stream) throws IOException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException, ECLibException, EncryptionPasswordInvalidException, InvalidWalletFileException
    {
        long magic = stream.readLong();
        if (magic != MAGIC_HEADER)
            throw new InvalidWalletFileException();
        String encrypted = stream.readUTF();

        PBE pbe = new PBE();
        String decrypted = pbe.decrypt(password, encrypted);

//        byte[] data = Base58.decode(decrypted);
//
//        if (data.length == 0)
//            throw new EncryptionPasswordInvalidException(password);
//
//        byte seed[] = ByteUtil.trim(data, 0, 32);
//        int numInterations = ByteUtil.decodei(ByteUtil.trim(data, 32, 36));
//
//        generateFirstPair(seed);
//
//        while (this.numIterations < numInterations)
//            incrementPair(seed);
    }

    public KeyPair pair()
    {
        return keyPair.getLast();
    }
}