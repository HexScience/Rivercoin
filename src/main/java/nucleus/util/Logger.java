package nucleus.util;

public class Logger
{
    public static final String COLOUR_YELLOW    = isUnixBased() ? (char)27 + "[33m" : "";
    public static final String COLOUR_LIME      = isUnixBased() ? (char)27 + "[32m" : "";
    public static final String COLOUR_WHITE     = isUnixBased() ? (char)27 + "[27m" : "";
    public static final String COLOUR_BLUE      = isUnixBased() ? (char)27 + "[34m" : "";
    public static final String COLOUR_RED       = isUnixBased() ? (char)27 + "[31m" : "";

    private static boolean isUnixBased()
    {
        return !System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static void prt(String msg)
    {
        prt(COLOUR_WHITE, msg);
    }

    public static void prt(String colour, String msg)
    {
        System.out.println(colour + msg);
    }

    public static void err(String msg)
    {
        prt(COLOUR_RED, msg);
    }

    public static void alert(String s)
    {
        prt(COLOUR_LIME, s);
    }
}
