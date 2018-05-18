package com.riverssen.core.consensus;

import com.riverssen.core.Config;
import com.riverssen.core.Logger;
import com.riverssen.core.algorithms.*;
import com.riverssen.utils.HashAlgorithm;
import com.riverssen.utils.MerkleTree;
import com.riverssen.utils.Tuple;
import com.riverssen.utils.HashUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ConsensusAlgorithm
{
    /**
     * List of usable hashing algorithms as of 13-05-2018
     *
     * this list will be updated with each revision of Rivercoin Core
     */
    private static final List<ConsensusAlgorithm> algorithms = Collections.synchronizedList(new ArrayList<>());
    private static final List<HashAlgorithm> algorithms2 = Collections.synchronizedList(new ArrayList<>());

    static {
        algorithms.add(new SHA256PoW());
//        algorithms.add(new RipeMD128PoW());
        algorithms.add(new KeccakPoW());
        algorithms.add(new Blake2BPoW());
//        algorithms.add(new RipeMD160PoW());
//        algorithms.add(new Sha512PoW());
//        algorithms.add(new Gost3411PoW());
        algorithms.add(new Sha3256PoW());
        algorithms.add(new RipeMD256PoW());

        algorithms2.add(new Sha1());
        algorithms2.add(new Sha3());
        algorithms2.add(new Sha256());
        algorithms2.add(new Keccak());
        algorithms2.add(new RipeMD256());
    }

    public synchronized static Tuple<String, Long> applyPoWShuffle(final byte[] block, final String lastBlockHash)
    {
        Tuple<String, Long> result = algorithms.get(0).apply(block);
        /** use the hash of the last block to get a seed for the next proof of work hash algorithm
         *  this creates a system where each block is mined differently, making it harder for GPU/Asic
         *  miners to have the upper hand. As it would be cheaper to use cpu's.
         * **/
        Collections.shuffle(algorithms, new Random(new BigInteger(lastBlockHash, 16).longValue()));

        ArrayList<Tuple<String, Long>> listOfPoW = new ArrayList<>();

        for(int i = 1; i < algorithms.size(); i ++)
        {
            listOfPoW.add(result);
            result = algorithms.get(i).apply(result.getI().getBytes());
        }

        return result;
    }

    public synchronized static Tuple<String, Long> applyPoW(final String lastBlockHash, final MerkleTree tree)
    {
        return new Tuple("", 0);
    }

    public synchronized static String applyPoW(final long nonce, final String lastBlockHash, final MerkleTree tree)
    {
        return "";
    }

    public synchronized static Tuple<String, Long> applyPoW(final byte[] block, final String lastBlockHash)
    {
        /** use the hash of the last block to get a seed for the next proof of work hash algorithm
         *  this creates a system where each block is mined differently, making it harder for GPU/Asic
         *  miners to have the upper hand. As it would be cheaper to use cpu's.
         * **/
//        Collections.shuffle(algorithms, new Random(new BigInteger(lastBlockHash, 16).longValue()));
//        System.out.println(lastBlockHash);

        int algorithm = new Random(new BigInteger(lastBlockHash, 16).longValue()).nextInt(algorithms.size());
        Logger.alert("mining block with: " + algorithms.get(algorithm).getClass().getSimpleName().substring(0, algorithms.get(algorithm).getClass().getSimpleName().length() - 3));
        Logger.err("mining difficulty: " + Config.getConfig().TARGET_DIFFICULTY.toBigInteger());
        Tuple<String, Long> result = algorithms.get(algorithm).apply(block);

        return result;
    }

    public synchronized Tuple<String, Long> apply(final byte[] block)
    {
        return null;
    }

    public synchronized String              applyLinear(final byte[] block) { return null; }

    public synchronized boolean verify(final byte[] block, final long nonce)
    {
        return false;
    }

    public static HashAlgorithm getInstance(int provider)
    {
        if(provider < 0)
        {
            Logger.err("no such provider: " + provider);
            return null;
        }

        return algorithms2.get(provider % algorithms2.size());
    }

    public static HashAlgorithm getLatestInstance(byte[] parentHash)
    {
        int algorithm = new Random(new BigInteger(parentHash).longValue()).nextInt(algorithms2.size());

        return algorithms2.get(algorithm);
    }

    private static class KeccakPoW extends ConsensusAlgorithm
    {
        @Override
        public synchronized Tuple<String, Long> apply(byte[] block)
        {
            String string = "11111111111111111111111111";

            ByteBuffer block_ = ByteBuffer.allocate(Math.max(64, block.length) + 8);

            block_.putLong(0);
            block_.put(block);

            block_.flip();

            long nonce = -1L;

            String target = HashUtil.createDifficultyString();

            string = HashUtil.hashToStringBase16(HashUtil.applyKeccak(block_.array()));

            while(new BigInteger(string, 16).compareTo(Config.getConfig().TARGET_DIFFICULTY.toBigInteger()) >= 0)
            {
                block_.clear();
                block_.putLong(++nonce);
                block_.put(string.getBytes());
                block_.flip();
                string = HashUtil.hashToStringBase16(HashUtil.applyKeccak(block_.array()));
            }

            return new Tuple<>(string, nonce);
        }

        @Override
        public synchronized String applyLinear(byte[] block)
        {
            return HashUtil.hashToStringBase16(HashUtil.applyKeccak(block));
        }
    }

    private static class Blake2BPoW extends ConsensusAlgorithm
    {
        @Override
        public synchronized Tuple<String, Long> apply(byte[] block)
        {
            String string = "11111111111111111111111111";

            ByteBuffer block_ = ByteBuffer.allocate(Math.max(64, block.length) + 8);

            block_.putLong(0);
            block_.put(block);

            block_.flip();

            long nonce = -1L;

            String target = HashUtil.createDifficultyString();

            string = HashUtil.hashToStringBase16(HashUtil.applyBlake2b(block_.array()));

            while(new BigInteger(string, 16).compareTo(Config.getConfig().TARGET_DIFFICULTY.toBigInteger()) >= 0)
            {
                block_.clear();
                block_.putLong(++nonce);
                block_.put(string.getBytes());
                block_.flip();
                string = HashUtil.hashToStringBase16(HashUtil.applyBlake2b(block_.array()));
            }

            return new Tuple<>(string, nonce);
        }

        @Override
        public synchronized String applyLinear(byte[] block)
        {
            return HashUtil.hashToStringBase16(HashUtil.applyBlake2b(block));
        }
    }

    private static class RipeMD128PoW extends ConsensusAlgorithm
    {
        @Override
        public synchronized Tuple<String, Long> apply(byte[] block)
        {
            String string = "11111111111111111111111111";

            ByteBuffer block_ = ByteBuffer.allocate(Math.max(64, block.length) + 8);

            block_.putLong(0);
            block_.put(block);

            block_.flip();

            long nonce = -1L;

            String target = HashUtil.createDifficultyString();

            string = HashUtil.hashToStringBase16(HashUtil.applyRipeMD128(block_.array()));

            while(new BigInteger(string, 16).compareTo(Config.getConfig().TARGET_DIFFICULTY.toBigInteger()) >= 0)
            {
                block_.clear();
                block_.putLong(++nonce);
                block_.put(string.getBytes());
                block_.flip();
                string = HashUtil.hashToStringBase16(HashUtil.applyRipeMD128(block_.array()));
            }

            return new Tuple<>(string, nonce);
        }

        @Override
        public synchronized String applyLinear(byte[] block)
        {
            return HashUtil.hashToStringBase16(HashUtil.applyRipeMD128(block));
        }
    }

    private static class RipeMD160PoW extends ConsensusAlgorithm
    {
        @Override
        public synchronized Tuple<String, Long> apply(byte[] block)
        {
            String string = "11111111111111111111111111";

            ByteBuffer block_ = ByteBuffer.allocate(Math.max(64, block.length) + 8);

            block_.putLong(0);
            block_.put(block);

            block_.flip();

            long nonce = -1L;

            String target = HashUtil.createDifficultyString();

            string = HashUtil.hashToStringBase16(HashUtil.applyRipeMD160(block_.array()));

            while (new BigInteger(string, 16).compareTo(Config.getConfig().TARGET_DIFFICULTY.toBigInteger()) >= 0)
            {
                block_.clear();
                block_.putLong(++nonce);
                block_.put(string.getBytes());
                block_.flip();
                string = HashUtil.hashToStringBase16(HashUtil.applyRipeMD160(block_.array()));
            }

            return new Tuple<>(string, nonce);
        }

        @Override
        public synchronized String applyLinear(byte[] block)
        {
            return HashUtil.hashToStringBase16(HashUtil.applyRipeMD160(block));
        }
    }

    private static class RipeMD256PoW extends ConsensusAlgorithm
    {
        @Override
        public synchronized Tuple<String, Long> apply(byte[] block)
        {
            String string = "11111111111111111111111111";

            ByteBuffer block_ = ByteBuffer.allocate(Math.max(64, block.length) + 8);

            block_.putLong(0);
            block_.put(block);

            block_.flip();

            long nonce = -1L;

            String target = HashUtil.createDifficultyString();

            string = HashUtil.hashToStringBase16(HashUtil.applyRipeMD256(block_.array()));

            while(new BigInteger(string, 16).compareTo(Config.getConfig().TARGET_DIFFICULTY.toBigInteger()) >= 0)
            {
                block_.clear();
                block_.putLong(++nonce);
                block_.put(string.getBytes());
                block_.flip();
                string = HashUtil.hashToStringBase16(HashUtil.applyRipeMD256(block_.array()));
            }

            return new Tuple<>(string, nonce);
        }

        @Override
        public synchronized String applyLinear(byte[] block)
        {
            return HashUtil.hashToStringBase16(HashUtil.applyRipeMD256(block));
        }
    }

    private static class Sha512PoW extends ConsensusAlgorithm
    {
        @Override
        public synchronized Tuple<String, Long> apply(byte[] block)
        {
            String string = "11111111111111111111111111";

            ByteBuffer block_ = ByteBuffer.allocate(Math.max(64, block.length) + 8);

            block_.putLong(0);
            block_.put(block);

            block_.flip();

            long nonce = -1L;

            String target = HashUtil.createDifficultyString();

            string = HashUtil.hashToStringBase16(HashUtil.applySha512(block_.array()));

            while(new BigInteger(string, 16).compareTo(Config.getConfig().TARGET_DIFFICULTY.toBigInteger()) >= 0)
            {
                block_.clear();
                block_.putLong(++nonce);
                block_.put(string.getBytes());
                block_.flip();
                string = HashUtil.hashToStringBase16(HashUtil.applySha512(block_.array()));
            }

            return new Tuple<>(string, nonce);
        }

        @Override
        public synchronized String applyLinear(byte[] block)
        {
            return HashUtil.hashToStringBase16(HashUtil.applySha512(block));
        }
    }

    private static class Sha3256PoW extends ConsensusAlgorithm
    {
        @Override
        public synchronized Tuple<String, Long> apply(byte[] block)
        {
            String string = "11111111111111111111111111";

            ByteBuffer block_ = ByteBuffer.allocate(Math.max(64, block.length) + 8);

            block_.putLong(0);
            block_.put(block);

            block_.flip();

            long nonce = -1L;

            String target = HashUtil.createDifficultyString();

            string = HashUtil.hashToStringBase16(HashUtil.applySha3(block_.array()));

            while(new BigInteger(string, 16).compareTo(Config.getConfig().TARGET_DIFFICULTY.toBigInteger()) >= 0)
            {
                block_.clear();
                block_.putLong(++nonce);
                block_.put(string.getBytes());
                block_.flip();
                string = HashUtil.hashToStringBase16(HashUtil.applySha3(block_.array()));
            }

            return new Tuple<>(string, nonce);
        }

        @Override
        public synchronized String applyLinear(byte[] block)
        {
            return HashUtil.hashToStringBase16(HashUtil.applySha3(block));
        }
    }

    private static class Gost3411PoW extends ConsensusAlgorithm
    {
        @Override
        public synchronized Tuple<String, Long> apply(byte[] block)
        {
            String string = "11111111111111111111111111";

            ByteBuffer block_ = ByteBuffer.allocate(Math.max(64, block.length) + 8);

            block_.putLong(0);
            block_.put(block);

            block_.flip();

            long nonce = -1L;

            String target = HashUtil.createDifficultyString();

            string = HashUtil.hashToStringBase16(HashUtil.applyGost3411(block_.array()));

            while(new BigInteger(string, 16).compareTo(Config.getConfig().TARGET_DIFFICULTY.toBigInteger()) >= 0)
            {
                block_.clear();
                block_.putLong(++nonce);
                block_.put(string.getBytes());
                block_.flip();
                string = HashUtil.hashToStringBase16(HashUtil.applyGost3411(block_.array()));
            }

            return new Tuple<>(string, nonce);
        }

        @Override
        public synchronized String applyLinear(byte[] block)
        {
            return HashUtil.hashToStringBase16(HashUtil.applyGost3411(block));
        }
    }

    private static class SHA256PoW extends ConsensusAlgorithm
    {
        @Override
        public synchronized Tuple<String, Long> apply(byte[] block)
        {
            String string = "11111111111111111111111111";

            ByteBuffer block_ = ByteBuffer.allocate(Math.max(64, block.length) + 8);

            block_.putLong(0);
            block_.put(block);

            block_.flip();

//            int result[] = new int[8];

            long nonce = -1L;

//            ByteBuffer result_ = ByteBuffer.allocate(8 * 4);
//            IntBuffer resultBuffer = result_.asIntBuffer();
//
//            ProofOfUpdate.sha256(result, block_.array());
//
//            resultBuffer.put(result);
//            resultBuffer.flip();

            String target = HashUtil.createDifficultyString();

            string = HashUtil.hashToStringBase16(HashUtil.applySha256(block_.array()));

            while(new BigInteger(string, 16).compareTo(Config.getConfig().TARGET_DIFFICULTY.toBigInteger()) >= 0)
            {
                block_.clear();
                block_.putLong(++nonce);
                block_.put(string.getBytes());
                block_.flip();
                string = HashUtil.hashToStringBase16(HashUtil.applySha256(block_.array()));
            }

            return new Tuple<>(string, nonce);
        }

        @Override
        public synchronized boolean verify(byte[] block, long nonce)
        {
            String string = "";

            ByteBuffer block_ = ByteBuffer.allocate(block.length + 8);

            block_.putLong(0);
            block_.put(block);

            block_.flip();

            String target = HashUtil.createDifficultyString();

            block_.putLong(0, nonce ++);
            return HashUtil.hashToStringBase16(HashUtil.applySha256(block_.array())).substring(0, Config.getConfig().BLOCK_MINING_DIFFICULTY).equals(target);
        }
    }

    private class x256
    {
        int output[] = new int[8];
    }

    private final static int H0 = 0x6a09e667;
    private final static int H1 = 0xbb67ae85;
    private final static int H2 = 0x3c6ef372;
    private final static int H3 = 0xa54ff53a;
    private final static int H4 = 0x510e527f;
    private final static int H5 = 0x9b05688c;
    private final static int H6 = 0x1f83d9ab;
    private final static int H7 = 0x5be0cd19;


    static int rotr(int x, int n) {
        if (n < 32) return (x >> n) | (x << (32 - n));
        return x;
    }

    static int ch(int x, int y, int z) {
        return (x & y) ^ (~x & z);
    }

    static int maj(int x, int y, int z) {
        return (x & y) ^ (x & z) ^ (y & z);
    }

    static int sigma0(int x) {
        return rotr(x, 2) ^ rotr(x, 13) ^ rotr(x, 22);
    }

    static int sigma1(int x) {
        return rotr(x, 6) ^ rotr(x, 11) ^ rotr(x, 25);
    }

    static int gamma0(int x) {
        return rotr(x, 7) ^ rotr(x, 18) ^ (x >> 3);
    }

    static int gamma1(int x) {
        return rotr(x, 17) ^ rotr(x, 19) ^ (x >> 10);
    }

    private static void sha256(int[] digest, byte[] plain_key)
    {
        int t, gid, msg_pad;
        int stop, mmod;
        int i, ulen, item, total;
        int W[] = new int[80], temp, A, B, C, D, E, F, G, H, T1, T2;
        int num_keys = 1;
        int current_pad;

        int K[] = {
                0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
                0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
                0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
                0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
                0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
                0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
                0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
                0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
        };

        msg_pad = 0;

        ulen = plain_key.length;
        total = ulen % 64 >= 56 ? 2 : 1 + ulen / 64;

//  printf("ulen: %u total:%u\n", ulen, total);

        digest[0] = H0;
        digest[1] = H1;
        digest[2] = H2;
        digest[3] = H3;
        digest[4] = H4;
        digest[5] = H5;
        digest[6] = H6;
        digest[7] = H7;
        for (item = 0; item < total; item++)
        {

            A = digest[0];
            B = digest[1];
            C = digest[2];
            D = digest[3];
            E = digest[4];
            F = digest[5];
            G = digest[6];
            H = digest[7];

            for (t = 0; t < 80; t++)
            {
                W[t] = 0x00000000;
            }
            msg_pad = item * 64;
            if (ulen > msg_pad)
            {
                current_pad = (ulen - msg_pad) > 64 ? 64 : (ulen - msg_pad);
            } else
            {
                current_pad = -1;
            }

            //  printf("current_pad: %d\n",current_pad);
            if (current_pad > 0)
            {
                i = current_pad;

                stop = i / 4;
                //    printf("i:%d, stop: %d msg_pad:%d\n",i,stop, msg_pad);
                for (t = 0; t < stop; t++)
                {
                    W[t] = ((byte) plain_key[msg_pad + t * 4]) << 24;
                    W[t] |= ((byte) plain_key[msg_pad + t * 4 + 1]) << 16;
                    W[t] |= ((byte) plain_key[msg_pad + t * 4 + 2]) << 8;
                    W[t] |= (byte) plain_key[msg_pad + t * 4 + 3];
                    //printf("W[%u]: %u\n",t,W[t]);
                }
                mmod = i % 4;
                if (mmod == 3)
                {
                    W[t] = ((byte) plain_key[msg_pad + t * 4]) << 24;
                    W[t] |= ((byte) plain_key[msg_pad + t * 4 + 1]) << 16;
                    W[t] |= ((byte) plain_key[msg_pad + t * 4 + 2]) << 8;
                    W[t] |= ((byte) 0x80);
                } else if (mmod == 2)
                {
                    W[t] = ((byte) plain_key[msg_pad + t * 4]) << 24;
                    W[t] |= ((byte) plain_key[msg_pad + t * 4 + 1]) << 16;
                    W[t] |= 0x8000;
                } else if (mmod == 1)
                {
                    W[t] = ((byte) plain_key[msg_pad + t * 4]) << 24;
                    W[t] |= 0x800000;
                } else /*if (mmod == 0)*/
                {
                    W[t] = 0x80000000;
                }

                if (current_pad < 56)
                {
                    W[15] = ulen * 8;
                    //printf("ulen avlue 2 :w[15] :%u\n", W[15]);
                }
            } else if (current_pad < 0)
            {
                if (ulen % 64 == 0)
                    W[0] = 0x80000000;
                W[15] = ulen * 8;
                //printf("ulen avlue 3 :w[15] :%u\n", W[15]);
            }

            for (t = 0; t < 64; t++)
            {
                if (t >= 16)
                    W[t] = gamma1(W[t - 2]) + W[t - 7] + gamma0(W[t - 15]) + W[t - 16];
                T1 = H + sigma1(E) + ch(E, F, G) + K[t] + W[t];
                T2 = sigma0(A) + maj(A, B, C);
                H = G;
                G = F;
                F = E;
                E = D + T1;
                D = C;
                C = B;
                B = A;
                A = T1 + T2;
            }
            digest[0] += A;
            digest[1] += B;
            digest[2] += C;
            digest[3] += D;
            digest[4] += E;
            digest[5] += F;
            digest[6] += G;
            digest[7] += H;
        }
    }
}