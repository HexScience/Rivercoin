/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Riverssen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package nucleus.util;

import nucleus.system.Context;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FileUtils
{
    private static final Logger Logger = nucleus.util.Logger.get("FileUtils");
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
        dir.mkdir();
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

    public static byte[] readBytes(InputStream file)
    {
        try
        {
            DataInputStream stream = new DataInputStream(file);

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

    public static byte[] readBytesRAW(InputStream file)
    {
        try
        {
            DataInputStream stream = new DataInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte bytes[] = new byte[1024];

            while (stream.available() > 0)
            {
                int read = stream.read(bytes);
                out.write(bytes, 0, read);
            }

            stream.close();
            out.flush();
            out.close();

            return out.toByteArray();
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

    public static void writeBytesRAW(File file, byte info[])
    {
        try{
            DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));

            stream.write(info);

            stream.flush();

            stream.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String readUTF(InputStream resourceAsStream)
    {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));

            String line = "";
            String text = "";

            while((line = reader.readLine()) != null) text += line + "\n";

            reader.close();

            return text;
        } catch (Exception e)
        {
        }

        return "";
    }

    public static void deleteblock(long i, Context context)
    {
//        File file = new File(context.getConfig().getBlockChainDirectory() + File.separator + "block[" + i + "]");
//
//        if(file.exists())
//            file.delete();
    }
}
