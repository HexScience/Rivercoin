package nucleus.util;

import nucleus.exceptions.FileServiceException;

import java.io.*;

public class FileService
{
    private String entryPoint;

    public FileService(Object entryPoint)
    {
        this.entryPoint = new File(entryPoint.toString()).toString();
        if (this.entryPoint.endsWith("\\") || this.entryPoint.endsWith("/"))
            this.entryPoint = this.entryPoint.substring(0, this.entryPoint.length() - 1);
    }

    public static FileService homeDir()
    {
        return new FileService(System.getProperty("user.home"));
    }

    public static FileService appDir()
    {
        return new FileService(System.getProperty("user.home")).newFile("Applications");
    }

    public long length()
    {
        return file().length();
    }

    public FileService newFile(String name)
    {
        return new FileService(entryPoint + File.separator + name);
    }

    public File file()
    {
        return new File(entryPoint);
    }

    public <T> T as(Class<T> ctype) throws FileServiceException, IOException
    {
        if (ctype == BufferedReader.class)
            return (T) new BufferedReader(new FileReader(file()));
        else if (ctype == InputStream.class)
            return (T) new FileInputStream(file());
        else if (ctype == DataInputStream.class)
            return (T) new DataInputStream(new FileInputStream(file()));


        else if (ctype == BufferedWriter.class)
            return (T) new BufferedWriter(new FileWriter(file()));
        else if (ctype == OutputStream.class)
            return (T) new FileOutputStream(file());
        else if (ctype == DataOutputStream.class)
            return (T) new DataOutputStream(new FileOutputStream(file()));
        else
            throw new FileServiceException("no useful type provided.");
    }

    public void move(FileService service) throws IOException
    {
        copyTo(service);
        file().delete();
    }

    public void copyTo(FileService service) throws IOException
    {
        FileInputStream instream = new FileInputStream(file());

        FileOutputStream out = new FileOutputStream(service.file());

        while (instream.available() > 0)
            out.write(instream.read());

        instream.close();
        out.flush();
        out.close();
    }

    @Override
    public String toString()
    {
        return file().toString();
    }
}
