package com.riverssen.utils;

import com.riverssen.core.Logger;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class FileUtils
{
    private static Set<String> freeFiles = Collections.synchronizedSet(new HashSet<>());

    public static synchronized void setFileIsUsed(String string)
    {
        freeFiles.add(string);
    }

    public static synchronized boolean isFileBeingUsed(String string)
    {
        return freeFiles.contains(string);
    }

    public static synchronized void freeFile(String string)
    {
        freeFiles.remove(string);
    }

    public static void createDirectoryIfDoesntExist(String directory)
    {
        File dir = new File(directory);
        if(!dir.exists()) Logger.prt(Logger.COLOUR_BLUE, "directory '" + directory + "' doesn't exist, creating it.");
        dir.mkdirs();
    }

    public static void moveFromTemp(String directory)
    {
        File temp = new File(directory + File.separator + "temp");
        File dirc = new File(directory);

        File temporary[] = temp.listFiles();

        for(File file : temporary)
            try
            {
                Files.move(FileSystems.getDefault().getPath(file.toString()), FileSystems.getDefault().getPath(new File(dirc + File.separator + file.getName()).toString()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
    }

    public static String readUTF(String file)
    {
        try
        {
            DataInputStream stream = new DataInputStream(new FileInputStream(new File(file)));

            String utf = stream.readUTF();

            stream.close();

            return utf;
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return "";
    }

    public static void writeUTF(File file, String info)
    {
        try{
            DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));

            stream.writeUTF(info);

            stream.flush();

            stream.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static byte[] readBytes(String file)
    {
        try
        {
            DataInputStream stream = new DataInputStream(new FileInputStream(new File(file)));

            byte bytes[] = new byte[stream.readInt()];

            stream.read(bytes);

            stream.close();

            return bytes;
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return new byte[1];
    }

    public static void writeBytes(File file, byte info[])
    {
        try{
            DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));

            stream.write(info.length);
            stream.write(info);

            stream.flush();

            stream.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
