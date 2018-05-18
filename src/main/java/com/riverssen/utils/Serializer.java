package com.riverssen.utils;

import java.io.*;
import java.math.BigInteger;

public class Serializer implements Closeable
{
    private OutputStream          baos;
    private DataOutputStream      dos;

    public Serializer(OutputStream stream)
    {
        this.baos = stream;
        this.dos  = new DataOutputStream(baos);
    }

    public Serializer()
    {
        this(new ByteArrayOutputStream());
    }

    public void flush() throws IOException
    {
        dos.flush();
    }

    public void write(byte data[]) throws IOException
    {
        dos.write(data);
    }

    public void write(int b) throws IOException
    {
        dos.write(b);
    }

    public void writeInt(int i) throws IOException
    {
        dos.writeInt(i);
    }

    public void writeLong(long i) throws IOException
    {
        dos.writeLong(i);
    }

    public void writeBigInteger(BigInteger i) throws IOException
    {
        dos.writeInt(i.toByteArray().length);
        dos.write(i.toByteArray());
    }

    public void writeFloat(float i) throws IOException
    {
        dos.writeFloat(i);
    }

    public void writeDouble(double i) throws IOException
    {
        dos.writeDouble(i);
    }

    public void writeUTF(String utf) throws IOException
    {
        dos.writeUTF(utf);
    }

    @Override
    public void close() throws IOException
    {
        dos.close();
    }

    public DataOutputStream asDataOutputStream()
    {
        return dos;
    }

    public byte[] getBytes()
    {
        return ((ByteArrayOutputStream)baos).toByteArray();
    }
}
