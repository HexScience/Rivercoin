package nucleus.mining;

import nucleus.util.ByteUtil;

import java.math.BigInteger;

public class Nonce
{
    private long nonce_a;
    private long nonce_b;
    private long nonce_c;

    public Nonce()
    {
    }

    public Nonce(long a, long b, long c)
    {
        this.nonce_a = a;
        this.nonce_b = b;
        this.nonce_c = c;
    }

    public void set(long a, long b, long c)
    {
        this.nonce_a = a;
        this.nonce_b = b;
        this.nonce_c = c;
    }

    public Nonce increment()
    {
        return set(new BigInteger(getNonce()).add(BigInteger.ONE));
    }

    public Nonce set(BigInteger integer)
    {
        byte data[] = integer.toByteArray();

        if (data.length < 24)
        {
            int difference = 24 - data.length;

            data = ByteUtil.concatenate(new byte[difference], data);
        }

        this.nonce_a = ByteUtil.decode(ByteUtil.trim(data,  0, 8));
        this.nonce_b = ByteUtil.decode(ByteUtil.trim(data,  8, 16));
        this.nonce_c = ByteUtil.decode(ByteUtil.trim(data,  16, 24));

        return this;
    }

    public void setA(long x)
    {
        this.nonce_a = x;
    }

    public void setB(long x)
    {
        this.nonce_b = x;
    }

    public void setC(long x)
    {
        this.nonce_c = x;
    }

    public long getA()
    {
        return nonce_a;
    }

    public long getB()
    {
        return nonce_b;
    }

    public long getC()
    {
        return nonce_c;
    }

    public byte[] getNonce()
    {
        return ByteUtil.concatenate(ByteUtil.encode(nonce_a), ByteUtil.encode(nonce_b), ByteUtil.encode(nonce_c));
    }
}