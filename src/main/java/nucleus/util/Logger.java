package nucleus.util;

import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

public class Logger
{
    public static final Ansi.Color COLOUR_YELLOW    = YELLOW;//isUnixBased() ? (char)27 + "[33m" : "";
    public static final Ansi.Color COLOUR_LIME      = GREEN;//isUnixBased() ? (char)27 + "[32m" : "";
    public static final Ansi.Color COLOUR_WHITE     = WHITE;//isUnixBased() ? (char)27 + "[27m" : "";
    public static final Ansi.Color COLOUR_BLUE      = BLUE;//isUnixBased() ? (char)27 + "[34m" : "";
    public static final Ansi.Color COLOUR_RED       = RED;//isUnixBased() ? (char)27 + "[31m" : "";

    private static boolean isUnixBased()
    {
        return !System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static void prtf(Object msg, Object d)
    {
    }

    public static void prt(Object msg)
    {
        prt(BLUE, msg);
    }

    public static void prt(Ansi.Color colour, Object msg)
    {
        System.out.println(ansi().fg(colour).a(msg).reset());
    }

    public static void err(Object msg)
    {
        prt(COLOUR_RED, msg);
    }

    public static void alert(Object msg)
    {
        prt(COLOUR_LIME, msg);
    }
}
