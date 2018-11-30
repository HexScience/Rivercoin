package nucleus.crypto;

import nucleus.crypto.ec.ECLib;
import nucleus.exceptions.EncryptionPasswordInvalidException;
import nucleus.exceptions.InvalidWalletFileException;
import nucleus.protocols.protobufs.Address;
import nucleus.util.*;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This wallet creates a keychain from a master key,
 * The master key is NEVER STORED on file, and instead
 * an "immediate" key is stored on file along with an
 * "iteration" indication.
 *
 * The immediate key is the result of SafeKeyPair(master),
 * where the master key is mixed with salt (a nonce) to
 * generate a valid keypair,
 *
 * This means that the master key never generates a keypair
 * directly but instead it is double hashed along with a no
 * -nce to generate an immediate seed for a valid keypair.
 *
 * The immediate key = sha256(sha256((seed + random(seed + iteration)) + nonce))); double sha256 of 72 bytes
 *
 * This is important for security, whereas the user shall store
 * the originating mnemonic phrases or master seed somewhere secure
 * while the wallet only stores a double salted double hashed
 * version of the master key, which keeps changing every time
 * a new keypair is generated.
 *
 * So if the wallet is stolen at some point, any previous keypairs
 * generated at any point in time will not be stolen by the attacker
 * because only the current keypair is retained on disk.
 *
 * Thus it is of utmost importance for user to store their master
 * key or mnemonic phrases somewhere secure, preferably on paper.
 */
public class Wallet
{
    private static final long   MAGIC_HEADER = 0xfff16;

    private FileService         service;
    private BCECPublicKey       pubKey;
    private long                iteration;
    /**
     * @param service The save-to file location of the wallet.
     * @param password The encryption password of the wallet.
     * @param master The master seed and or key of the wallet.
     *
     *               This function generates a new wallet from a master-key
     *               and generates an output file with encrypted wallet data.
     */
    public Wallet(FileService service, String password, byte[] master) throws Throwable
    {
        this.service = service;
        this.generate(master, password);
    }

    /**
     * @param service The read-from location of the wallet.
     * @param password The decryption password of the wallet.
     *
     *                 This function loads a wallet from a file location.
     */
    public Wallet(FileService service, String password) throws Throwable
    {
        this.service = service;

        Tuple<byte[], Long> data = loadFromDisk(password);

        KeyPair pair = new KeyPair(data.getI());

        pubKey = pair.getPublicKeyObject();

        iteration = data.getJ();
    }

    private Wallet()
    {
    }

    public BCECPrivateKey getPrivateKey(String password) throws Throwable
    {
        Tuple<byte[], Long> data = loadFromDisk(password);

        return KeyPair.getPrivateKey(data.getI());
    }

    public BCECPrivateKey getPrivateKeyWIF(String password) throws Throwable
    {
        Tuple<byte[], Long> data = loadFromDisk(password);

        return KeyPair.getPrivateKey(data.getI());
    }

    public void newKeyPair(String password) throws Throwable
    {
        Tuple<byte[], Long> data = loadFromDisk(password);

        generate(data.getI(), password);
    }

    private void generate(final byte key[], final String password) throws Throwable
    {
        SafeKeyPair pair = new SafeKeyPair(ByteUtil.concatenate(key, new MnemonicPhraseSeeder(ByteUtil.concatenate(key, ByteUtil.encode(iteration ++))).getSeed()));

        byte immediateSeed[] = pair.getSeed();
        this.pubKey = pair.get().getPublicKeyObject();

        saveToDisk(immediateSeed, password);
    }

    /**
     * @param currentSeed The current seed.
     * @param password The encryption password.
     * @throws Throwable
     */
    private void saveToDisk(final byte currentSeed[], final String password) throws Throwable
    {
        DataOutputStream stream = (DataOutputStream) service.as(DataOutputStream.class);

        /**
         * Format:
         *  masterKey
         *  iteration
         */
        String walletImportFormat = Base58.encode(ByteUtil.concatenate(currentSeed, ByteUtil.encode(iteration)));

        PBE pbe = new PBE();
        String export = pbe.encrypt(password, walletImportFormat);

        stream.writeLong(MAGIC_HEADER);
        stream.writeUTF(export);

        stream.flush();
        stream.close();
    }

    private Tuple<byte[], Long> loadFromDisk(final String password) throws Throwable
    {
        DataInputStream stream = (DataInputStream) service.as(DataInputStream.class);

        long header = stream.readLong();

        if (header != MAGIC_HEADER)
            throw new InvalidWalletFileException();

        String encrypted = stream.readUTF();

        /**
         * Format:
         *  masterKey
         *  iteration
         */
        PBE pbe = new PBE();
        String decrypted = pbe.decrypt(password, encrypted);

        if (decrypted.length() == 0)
            throw new EncryptionPasswordInvalidException("password '" + password + "' invalid.");

        byte[] walletData = Base58.decode(decrypted);

        stream.close();

        return new Tuple<>(ByteUtil.trim(walletData, 0, 32), ByteUtil.decode(ByteUtil.trim(walletData, 32, 40)));
    }

    /**
     * @return The ripemd160(sha256()) representation of the current public key.
     */
    public Address getAddress()
    {
        return ECLib.ECGenAddress(pubKey);
    }

    /**
     * @param compressed Whether or not the key should be compressed.
     * @return The human readable version of the current public key.
     */
    public String getBase58EncodedPublicKey(boolean compressed)
    {
        return Base58.encode(pubKey.getQ().getEncoded(compressed));
    }

    /**
     * @param oldPassword The old encryption password.
     * @param newPassword The new encryption password.
     * @throws Throwable
     */
    public void changePassword(String oldPassword, String newPassword) throws Throwable
    {
        Tuple<byte[], Long> data = loadFromDisk(oldPassword);

        saveToDisk(data.getI(), newPassword);
    }

    /**
     * @param service The new location for the cloned wallet.
     * @return A new wallet cloned from this current wallet.
     * @throws IOException
     */
    public Wallet clone(FileService service) throws IOException
    {
        this.service.copyTo(service);

        Wallet wallet = new Wallet();
        wallet.service = service;
        wallet.pubKey = pubKey;
        wallet.iteration = iteration;

        return wallet;
    }

    public static String WIFPrivateKey(BCECPrivateKey privateKey)
    {
        byte prefix = (byte) 0x80;

        byte sha256[]   = HashUtil.applySha256(ByteUtil.concatenate(new byte[] {prefix}, privateKey.getD().toByteArray()));
        byte sha2562[]  = HashUtil.applySha256(sha256);

        byte version    = 0x00;
        byte checksum[] = ByteUtil.trim(sha2562, 0, 4);

        return Base58.encode(ByteUtil.concatenate(ByteUtil.concatenate(new byte[] {prefix}, privateKey.getD().toByteArray()), checksum));
    }
}