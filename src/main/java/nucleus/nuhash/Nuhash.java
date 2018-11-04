package nucleus.nuhash;

import nucleus.algorithms.HashAlgorithm;
import nucleus.algorithms.Keccak512;
import nucleus.algorithms.Skein_512_512;
import nucleus.exceptions.NuHashException;
import nucleus.system.Context;
import nucleus.system.Parameters;
import nucleus.util.ByteUtil;

import java.io.IOException;
import java.util.Random;

public class Nuhash
{
    public static final int index3D(int x, int y, int z, int WIDTH, int DEPTH)
    {
        return x + WIDTH * (y + DEPTH * z);
    }

    public static final int index3D(int xyz[], int WIDTH, int DEPTH)
    {
        return xyz[0] + WIDTH * (xyz[1] + DEPTH * xyz[2]);
    }

    public static byte getBit(int value, int x)
    {
        return (byte) (((byte) (value & (1 << x)) != 0) ? 1 : 0);
    }

    public static byte setBit(int value, int x, int to)
    {
        return (byte) (value & (1 << x));
    }

    public static byte numProtons(byte b)
    {
        byte i = 0;

        for (int x = 0; x < 3; x ++)
            i += getBit(b, x);

        return i;
    }

    public static void NextPoint(int entryPoint[], Random random)
    {
        final byte x = 0, y = 1, z = 2;
        entryPoint[x] += (random.nextInt(32) - 16);
        entryPoint[y] += (random.nextInt(32) - 16);
        entryPoint[z] += (random.nextInt(32) - 16);

        for (int i = 0; i < 3; i ++)
            if (entryPoint[i] > 15)
                entryPoint[i] = random.nextInt(16);
            else if (entryPoint[i] < 0)
                entryPoint[i] = random.nextInt(16);
    }

    public static final byte[] RandomBits(byte data[], Random random)
    {
        final byte x = 0, y = 1, z = 2;
        int entryPoint[] = new int[3];

        entryPoint[x] = random.nextInt(16);
        entryPoint[y] = random.nextInt(16);
        entryPoint[z] = random.nextInt(16);

        /**
         * All bytes must be activated.
         */

        byte nuk = data[index3D(entryPoint, 16, 16)];

        int p = numProtons(nuk);

//        if (p == 0)
//        {
//            p = 1;
//            data[index3D(entryPoint, 16, 16)] |= 1 << 0;
//        }
        int n = 8 - p;

//        boolean fissile = ((((double) p) / 8.0) <= 0.125);

        {
            int a = random.nextInt(4) + 4;
            int b = random.nextInt(4) + 4;
            int c = random.nextInt(4) + 4;

            int numns = Math.max(1, getBit(nuk, a) + getBit(nuk, b) + getBit(nuk, c));

            nuk     = setBit(nuk, a, 1);
            nuk     = setBit(nuk, b, 1);
            nuk     = setBit(nuk, c, 1);

            data[index3D(entryPoint, 16, 16)] = nuk;

            for (int i = 0; i < 16; i ++)
                for (int j = 0; j < 16; j ++)
                    for (int k = 0; k < 16; k ++)
                    {
                        NextPoint(entryPoint, random);
                        a = random.nextInt(4) + 4;
                        b = random.nextInt(4) + 4;
                        c = random.nextInt(4) + 4;
                        numns += Math.max(1, getBit(nuk, a) + getBit(nuk, b) + getBit(nuk, c)) - 1;
                        nuk = data[index3D(entryPoint, 16, 16)];

                        nuk = setBit(nuk, a, 1);
                        nuk = setBit(nuk, b, 1);
                        nuk = setBit(nuk, c, 1);
                        data[index3D(entryPoint, 16, 16)] = nuk;

                        NextPoint(entryPoint, random);
                        a = random.nextInt(4) + 4;
                        b = random.nextInt(4) + 4;
                        c = random.nextInt(4) + 4;
                        numns += Math.max(1, getBit(nuk, a) + getBit(nuk, b) + getBit(nuk, c)) - 1;
                        nuk = data[index3D(entryPoint, 16, 16)];

                        nuk = setBit(nuk, a, 1);
                        nuk = setBit(nuk, b, 1);
                        nuk = setBit(nuk, c, 1);
                        data[index3D(entryPoint, 16, 16)] = nuk;

                        NextPoint(entryPoint, random);
                        a = random.nextInt(4) + 4;
                        b = random.nextInt(4) + 4;
                        c = random.nextInt(4) + 4;
                        numns += Math.max(1, getBit(nuk, a) + getBit(nuk, b) + getBit(nuk, c)) - 1;
                        nuk = data[index3D(entryPoint, 16, 16)];

                        nuk = setBit(nuk, a, 1);
                        nuk = setBit(nuk, b, 1);
                        nuk = setBit(nuk, c, 1);
                        data[index3D(entryPoint, 16, 16)] = nuk;
                    }
        }

        return data;
    }

//    public static final byte[] BuildMiniDump(final Context context, long epoch, final long seed) throws IOException
//    {
//        int maxsize = 4096;
//        int timetgt = maxsize / 64;
//        Skein_512_512 skein = new Skein_512_512();
//
//        Random random = new Random(seed);
//
//        byte dump[] = new byte[0];
//
//        for (int i = 0; i < timetgt; i ++)
//            dump = ByteUtil.concatenate(dump, skein.encode(context.getSerializer().loadHeader((long) (random.nextFloat() * epoch)).getBytes()));
//
//        return RandomBits(dump, random);
//    }

    public static final byte[] BuildMiniDump(final Context context, long epoch, final long seed) throws IOException
    {
        int maxsize = 4096;
        int timetgt = maxsize / 64;
        Skein_512_512 skein = new Skein_512_512();

        Random random = new Random(seed);

        byte dump[] = new byte[0];

        for (int i = 0; i < timetgt; i ++)
            dump = ByteUtil.concatenate(dump, skein.encode(context.getSerializer().loadHeader((long) (random.nextFloat() * epoch)).getBytes()));

        Keccak512 keccak = new Keccak512();
        byte seedbytes[] = keccak.encode(dump);

        /**
         * expand bytes.
         */
        dump = seedbytes;

        while (dump.length < 4096)
        {
            dump = ByteUtil.concatenate(dump, keccak.encode(seedbytes));
            seedbytes = ByteUtil.concatenate(ByteUtil.trim(seedbytes, random.nextInt(seedbytes.length / 2), seedbytes.length), seedbytes);
        }

        return RandomBits(dump, random);
    }

    public static long[] BuildSeedlings(byte data[])
    {
        HashAlgorithm skein512 = new Skein_512_512();
        byte blockHeaderSeedx512[] = skein512.encode(data);

        /** initial seedlings **/
        long    a0 = ByteUtil.decode(ByteUtil.trim(blockHeaderSeedx512, 0, 8)), a1 = ByteUtil.decode(ByteUtil.trim(blockHeaderSeedx512, 8, 16)), a2 = ByteUtil.decode(ByteUtil.trim(blockHeaderSeedx512, 16, 24)), a3 = ByteUtil.decode(ByteUtil.trim(blockHeaderSeedx512, 24, 32)),
                a4 = ByteUtil.decode(ByteUtil.trim(blockHeaderSeedx512, 32, 40)), a5 = ByteUtil.decode(ByteUtil.trim(blockHeaderSeedx512, 40, 48)), a6 = ByteUtil.decode(ByteUtil.trim(blockHeaderSeedx512, 48, 56)), a7 = ByteUtil.decode(ByteUtil.trim(blockHeaderSeedx512, 56, 64));

        return new long[] {a0, a1, a2, a3, a4, a5, a6, a7};
    }

    /**
     * @param context Program context.
     * @return A memory dump built randomly from a seed block header.
     */
    public static final byte[] RandomAccessMemoryGraphBuildDump(Context context, long blockHeight, byte[] blockHeaderData) throws IOException, NuHashException
    {
        HashAlgorithm skein512 = new Skein_512_512();
        byte blockHeaderSeedx512[] = skein512.encode(blockHeaderData);

        if (10 >= blockHeight) return blockHeaderSeedx512;

        /** initial seedlings **/
        long    a0 = ByteUtil.decode(ByteUtil.trim(blockHeaderSeedx512, 0, 8)), a1 = ByteUtil.decode(ByteUtil.trim(blockHeaderSeedx512, 8, 16)), a2 = ByteUtil.decode(ByteUtil.trim(blockHeaderSeedx512, 16, 24)), a3 = ByteUtil.decode(ByteUtil.trim(blockHeaderSeedx512, 24, 32)),
                a4 = ByteUtil.decode(ByteUtil.trim(blockHeaderSeedx512, 32, 40)), a5 = ByteUtil.decode(ByteUtil.trim(blockHeaderSeedx512, 40, 48)), a6 = ByteUtil.decode(ByteUtil.trim(blockHeaderSeedx512, 48, 56)), a7 = ByteUtil.decode(ByteUtil.trim(blockHeaderSeedx512, 56, 64));

        long EPOCH = Math.floorDiv(blockHeight, Parameters.TIME_PER_EPOCH);

        /**
         * build a 32.768 kB + 1 byte mini-dump
         * to be used as a seed in the
         * future.
         * **/
        byte[]  seedDump    = new byte[] {0x08};

        seedDump = ByteUtil.concatenate(seedDump, BuildMiniDump(context, EPOCH, a0));
        seedDump = ByteUtil.concatenate(seedDump, BuildMiniDump(context, EPOCH, a1));
        seedDump = ByteUtil.concatenate(seedDump, BuildMiniDump(context, EPOCH, a2));
        seedDump = ByteUtil.concatenate(seedDump, BuildMiniDump(context, EPOCH, a3));
        seedDump = ByteUtil.concatenate(seedDump, BuildMiniDump(context, EPOCH, a4));
        seedDump = ByteUtil.concatenate(seedDump, BuildMiniDump(context, EPOCH, a5));
        seedDump = ByteUtil.concatenate(seedDump, BuildMiniDump(context, EPOCH, a6));
        seedDump = ByteUtil.concatenate(seedDump, BuildMiniDump(context, EPOCH, a7));

        byte[]  RAMg        = new byte[] {0x08};
        int esize           = (int) (256_000 * EPOCH);
        int steps           = esize / seedDump.length;

        RAMg                = ByteUtil.concatenate(RAMg, seedDump);

        for (int i = 0; i < steps; i ++)
        {
            seedDump        = new byte[] {0x08};

            byte seedx512[] = skein512.encode(RAMg);

            /** rebuild seedlings **/
            a0              = ByteUtil.decode(ByteUtil.trim(seedx512, 0, 8));
            a1              = ByteUtil.decode(ByteUtil.trim(seedx512, 8, 16));
            a2              = ByteUtil.decode(ByteUtil.trim(seedx512, 16, 24));
            a3              = ByteUtil.decode(ByteUtil.trim(seedx512, 24, 32));
            a4              = ByteUtil.decode(ByteUtil.trim(seedx512, 32, 40));
            a5              = ByteUtil.decode(ByteUtil.trim(seedx512, 40, 48));
            a6              = ByteUtil.decode(ByteUtil.trim(seedx512, 48, 56));
            a7              = ByteUtil.decode(ByteUtil.trim(seedx512, 56, 64));

            seedDump        = ByteUtil.concatenate(seedDump, BuildMiniDump(context, EPOCH, a0));
            seedDump        = ByteUtil.concatenate(seedDump, BuildMiniDump(context, EPOCH, a1));
            seedDump        = ByteUtil.concatenate(seedDump, BuildMiniDump(context, EPOCH, a2));
            seedDump        = ByteUtil.concatenate(seedDump, BuildMiniDump(context, EPOCH, a3));
            seedDump        = ByteUtil.concatenate(seedDump, BuildMiniDump(context, EPOCH, a4));
            seedDump        = ByteUtil.concatenate(seedDump, BuildMiniDump(context, EPOCH, a5));
            seedDump        = ByteUtil.concatenate(seedDump, BuildMiniDump(context, EPOCH, a6));
            seedDump        = ByteUtil.concatenate(seedDump, BuildMiniDump(context, EPOCH, a7));

            RAMg            = ByteUtil.concatenate(RAMg, seedDump);
        }

        if (RAMg.length >= esize)
            throw new NuHashException();

        return RAMg;
    }
}
