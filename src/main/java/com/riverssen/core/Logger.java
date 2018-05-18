package com.riverssen.core;

public class Logger
{
    public static final String COLOUR_YELLOW = (char)27 + "[33m";
    public static final String COLOUR_LIME = (char)27 + "[32m";
    public static final String COLOUR_WHITE = (char)27 + "[27m";
    public static final String COLOUR_BLUE = (char)27 + "[34m";
    public static final String COLOUR_RED = (char)27 + "[31m";

    public static void prt(String msg)
    {
        prt(COLOUR_WHITE, msg);
    }

    public static void prt(String colour, String msg)
    {
        System.out.println(colour + " " + msg);
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