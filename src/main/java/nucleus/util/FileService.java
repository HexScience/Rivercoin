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

    public FileService newFile(String name)
    {
        return new FileService(entryPoint + File.separator + name);
    }

    public File file()
    {
        return new File(entryPoint);
    }

    public Object as(Class<?> ctype) throws FileServiceException, IOException
    {
        if (ctype == BufferedReader.class)
            return new BufferedReader(new FileReader(file()));
        else if (ctype == InputStream.class)
            return new FileInputStream(file());
        else if (ctype == DataInputStream.class)
            return new DataInputStream(new FileInputStream(file()));


        else if (ctype == BufferedWriter.class)
            return new BufferedWriter(new FileWriter(file()));
        else if (ctype == OutputStream.class)
            return new FileOutputStream(file());
        else if (ctype == DataOutputStream.class)
            return new DataOutputStream(new FileOutputStream(file()));
        else
            throw new FileServiceException("no useful type provided.");
    }
}
