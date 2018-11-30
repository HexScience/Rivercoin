package nucleus.system;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

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
    public static final double MINIMUM_DIFFICULTY               = 1.978;
    /** the maximum allowed difficulty **/
    public static final double MAXIMUM_DIFFICULY                = 1.0256;
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

    public static final short TEST_NETWORK_NODE_PORT            =                        5621;
    public static final short MAIN_NETWORK_NODE_PORT            =                        5622;
    public static final long MAXIMUM_BLOCK_SIZE                 =                      60_000;
//    public static final long PRICE_PER_STORAGE_GBYTE            =                  7_500_000L;

    public static final long TIME_PER_EPOCH                     = ((2_563_200L) / 12L) * 3;
    public static final String ShortName                        = "NCL";

    public static byte TOTAL_OPCODES                            = 0;

    /** calculates the reward per block **/
    public static long rewardAtBlock(long x)
    {
        long halves = new BigDecimal(x).divide(new BigDecimal(REWARD_HALVING_RATE), 100, BigDecimal.ROUND_FLOOR).longValue();
        double reward = BIGGEST_REWARD_AMOUNT;

        for (int i = 0; i < halves; i ++)
            reward /= 2.0;

        return (long) Math.ceil(reward);
    }

    public static double calculateDifficulty()
    {
        return 1;
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

    public static BigInteger toInteger(double difficulty)
    {
        return new BigDecimal(difficulty).pow(256).toBigInteger();
    }

    public static String prependedHash(String hash)
    {
        while (hash.length() < 64)
            hash = "0" + hash;

        return hash;
    }

    /**
     * @param prevBlock chain[chain.length]
     * @param blockBefore chain[chain.length - 1]
     * @return
     */
    public static double calculateDifficulty(long prevBlock, long blockBefore, double oldDifficulty)
    {
        BigDecimal blockTime_average    = new BigDecimal(AVERAGE_BLOCK_TIME);
        BigDecimal blockInterval        = new BigDecimal(prevBlock).subtract(new BigDecimal(blockBefore));
        BigDecimal severeness           = blockTime_average.divide(blockInterval, 6, BigDecimal.ROUND_HALF_UP);
        double     doubleValu           = severeness.doubleValue();

        if (doubleValu < 0)
            return oldDifficulty;

        double twoSided = severeness.doubleValue() - 1.0;
        if (doubleValu < 1)
            twoSided *= Math.abs(1.0/doubleValu);

        double diff = Math.min(MINIMUM_DIFFICULTY, Math.max(MAXIMUM_DIFFICULY, oldDifficulty - (twoSided * 0.01299)));
//        Logger.alert(Parameters.prependedHash(new BigDecimal(diff).pow(256).toBigInteger().toString(16)));

        return diff;
    }

    public static String getMyIP()
    {
        try{
            URL url = new URL("http://checkip.amazonaws.com/");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

            return br.readLine();
        } catch (Exception e)
        {
        }

        return null;
    }
}