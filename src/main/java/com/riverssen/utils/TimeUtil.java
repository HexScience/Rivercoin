package com.riverssen.utils;

public class TimeUtil
{
    public static String getPretty(String format)
    {
        long milliseconds = System.currentTimeMillis();

        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);

        return format.replaceAll("H", hours + "").replaceAll("M", minutes + "").replaceAll("S", seconds + "");
    }
}
