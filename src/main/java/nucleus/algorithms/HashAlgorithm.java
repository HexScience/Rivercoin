package nucleus.algorithms;

import nucleus.util.ByteUtil;

public interface HashAlgorithm
{
    byte[] encode(byte data[]);
    String encode16(byte data[]);
    String encode32(byte data[]);
    String encode58(byte data[]);
    String encode64(byte data[]);

    static byte[] xorif(int max, byte[] a)
    {
        if (a.length > max)
        {
            byte[] a1 = ByteUtil.trim(a, max, a.length);
            byte[] a2 = ByteUtil.trim(a, 0, max);

            byte[] a3 = new byte[a2.length];

            for (int i = 0; i < a3.length; i ++)
                a3[i] = (byte) (a1[i] | a2[i]);

            return a3;
        }

        return a;
    }

    default int numBits() { return 256; }
}
