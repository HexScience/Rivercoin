package com.riverssen.nucleus.util;

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
    private String  caller;
    private String  ltn;
    private static final int numTabsPerText = 20;

    private Logger(String name)
    {
        this.caller = name.length() > 0 ? name + ": " : name;
        this.ltn    = name.length() > 0 ? numTabs(numTabsPerText - (name + ": ").length()) : numTabs(numTabsPerText);
    }

    private static final String numTabs(int num)
    {
        String tabs = "";

        for (int i = 0; i < num; i ++)
            tabs += " ";

        return tabs;
    }

    public static Logger get(String name)
    {
        return new Logger(name);
    }

    public static void prtf(Object msg, Object d)
    {
    }

    public void prt(Object msg)
    {
        prt(BLUE, msg);
    }

    public void prt(Ansi.Color colour, Object msg)
    {
        System.out.println(ansi().fg(YELLOW).a(caller).a(ltn).fg(colour).a(msg).reset());
    }

    public void err(Object msg)
    {
        prt(COLOUR_RED, msg);
    }

    public void alert(Object msg)
    {
        prt(COLOUR_LIME, msg);
    }
}
