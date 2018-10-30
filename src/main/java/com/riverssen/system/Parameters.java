package com.riverssen.system;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Parameters
{
    /** public addresses to send/receive money (main network) **/
    public static final byte MAIN_NETWORK_PUBLIC_ADDRESS_PREFIX = 23;//A
    /** public addresses to send/receive money (test network) **/
    public static final byte TEST_NETWORK_PUBLIC_ADDRESS_PREFIX = 24;//B
    /** public addresses of an object for download (main network) **/
    public static final byte MAIN_NETWORK_ADDRSS_ADDRESS_PREFIX = 43;//2b
    /** public addresses of website deployable (main network) **/
    public static final byte TEST_NETWORK_WEBSYT_ADDRESS_PREFIX = 59;//3b

    /** public transaction id (test network) **/
    public static final byte TEST_NETWORK_TRXNID_ADDRESS_PREFIX = 12;//0c
    /** public transaction id (test network) **/
    public static final byte MAIN_NETWORK_TRXNID_ADDRESS_PREFIX = 28;//1c

    /** average block required blocktime in milliseconds **/
    public static final long AVERAGE_BLOCK_TIME                 = 12_000L;
    /** the minimum acceptable difficulty **/
    public static final BigInteger MINIMUM_DIFFICULTY           = new BigDecimal("2252695363532346326408320327221716344571888488440004845743123953585319770870").toBigInteger();
    /** the maximum allowed difficulty **/
    public static final BigInteger MAXIMUM_DIFFICULTY           = new BigDecimal("269595352910113094931564763447239913360").toBigInteger();
    /** block reward halving rate **/
    public static final long REWARD_HALVING_RATE                = 2_563_200L * 4;
    /** starting reward **/
    public static final long BIGGEST_REWARD_AMOUNT              =            100_000_000_000L;

    public static final long MAXIMUM_SATOSHIS                   = 32_000_000__00_000_000_000L;//92_233_720___36_854_775_807L;
    public static final long SATOSHIS_PERCOIN                   =            100_000_000_000L;

    public static final long PRICE_PER_NETWORK_GBYTE            =                    750_000L;
    public static final long PRICE_PER_NETWORK_MBYTE            =                        750L;
    public static final long PRICE_PER_NETWORK_KBYTE            =                          1L;
    public static final long PRICE_PER_NETWORK_BBYTE            =                          1L;
    public static final double PRICE_PER_NETWORK_MEGA_BYTE      =                           0.75;
    public static final double PRICE_PER_NETWORK_BYTE           =                           0.00075;
    public static final long MINIMUM_TRANSACTION                =                    547_000L;
//    public static final long PRICE_PER_STORAGE_GBYTE            =                  7_500_000L;


    /** calculates the reward per block **/
    public static long rewardAtBlock(long x)
    {
        long halves = new BigDecimal(x).divide(new BigDecimal(REWARD_HALVING_RATE), 100, BigDecimal.ROUND_FLOOR).longValue();
        double reward = BIGGEST_REWARD_AMOUNT;

        for (int i = 0; i < halves; i ++)
            reward /= 2.0;

        return (long) Math.ceil(reward);
    }

    public static long coinToSatoshis(double amt)
    {
        return (long) amt * SATOSHIS_PERCOIN;
    }

    public static double satoshisToCoin(long amt)
    {
        return (double) amt / SATOSHIS_PERCOIN;
    }

    public static long byteStorageCost(long amt)
    {
        long cost = (long) (PRICE_PER_NETWORK_BYTE * amt);

        return Math.max(PRICE_PER_NETWORK_KBYTE, cost);
    }
}