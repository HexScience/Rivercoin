package com.riverssen.core;

import java.io.DataInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

public class RiverCoin
{
    //9.223.372.0,36.854.775.807

    public  static final long    NANO_COIN_TO_RVC    = 100_000_000_000L;
    private static final String  NANO_COIN_ZEROS     = "00000000000";
    public  static final long    MAX_RIVERCOINS      = 4_000_000_000_000_000_000L;
//    public  static final long    MAX_RIVERCOINS      = 18446744073709551615L;

    //    public static final long    MAX_VALUE           = 9_223_372_036_854_775_807L;
    //    public static final long    MAX_VALUE           = 18_223_372_036__854_775_807L;
    public static final int     MAX_BYTES = new RiverCoin(MAX_RIVERCOINS + "").nanocoin.toByteArray().length;

    private BigInteger nanocoin;

    /**
     * @param nanocoin Be careful when passing 1, 1 = 1/100.000.000.000 of a rivercoin.
     */
    @Deprecated
    public RiverCoin(BigInteger nanocoin)
    {
        this.nanocoin = nanocoin;//new BigInteger(new BigDecimal(nanocoin).multiply(new BigDecimal(NANO_COIN_TO_RVC + "")).toPlainString().replaceAll("\\..*", ""));
    }

    public RiverCoin(String rvc)
    {
        this.nanocoin = new BigInteger(new BigDecimal(rvc).multiply(new BigDecimal(NANO_COIN_TO_RVC + "")).toPlainString().replaceAll("\\..*", ""));
    }

    public RiverCoin(byte[] bytes)
    {
        this.nanocoin = new BigInteger(bytes);
    }

    public RiverCoin(BigDecimal decimal)
    {
        this(decimal.toPlainString());
    }

    public static String fastRiverCoinValueToNanoCoinConversion(String amt)
    {
//        if(!amt.contains(".")) return amt + NANO_COIN_ZEROS;
//        int index = amt.indexOf(".") + 1;
//        return amt.replaceAll("\\.", "") + NANO_COIN_ZEROS.substring(amt.substring(index).length());

        return new RiverCoin(amt).toString();
    }

    @Override
    public String toString()
    {
        return nanocoin.toString();
    }

    public String toRiverCoinString()
    {
        return new BigDecimal(nanocoin).divide(new BigDecimal(NANO_COIN_TO_RVC + "")).toPlainString();
    }

    public BigInteger toBigInteger()
    {
        return nanocoin;
    }

    public byte[] getBytes()
    {
        byte bytes[] = new byte[MAX_BYTES];

        byte nn[]    = nanocoin.toByteArray();

        int toPad    = MAX_BYTES - nn.length;

        for(int i = 0; i < toPad; i ++) bytes[i] = 0;

        for(int i = 0; i < nn.length; i ++) bytes[i + toPad] = nn[i];

        return bytes;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof RiverCoin) return nanocoin.compareTo(((RiverCoin) obj).nanocoin) == 0;
        else if(obj instanceof BigInteger) return nanocoin.compareTo(((BigInteger) obj)) == 0;

        return super.equals(obj);
    }

    public static RiverCoin fromStream(DataInputStream stream) throws Exception
    {
        byte array[] = new byte[MAX_BYTES];

        stream.read(array);

        return new RiverCoin(array);
    }
}