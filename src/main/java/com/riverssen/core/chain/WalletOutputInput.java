package com.riverssen.core.chain;

import com.riverssen.core.RiverCoin;
import com.riverssen.utils.FileUtils;
import com.riverssen.utils.Tuple;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class WalletOutputInput
{
//    private static final Set<String> inUse = Collections.synchronizedSet(new HashSet<>());

    private Tuple<List<Tuple<RiverCoin, String>>, List<Tuple<RiverCoin, String>>> txio;

    public WalletOutputInput(File file)
    {
        this();
        readFromFile(file);
    }

    public WalletOutputInput()
    {
        this.txio = new Tuple<>(Collections.synchronizedList(new ArrayList<>()), Collections.synchronizedList(new ArrayList<>()));
    }

    public synchronized void addInput(RiverCoin coin, String hash)
    {
        this.txio.getI().add(new Tuple<>(coin, hash));
    }

    public synchronized void addOutput(RiverCoin coin, String hash)
    {
        this.txio.getJ().add(new Tuple<>(coin, hash));
    }

    public synchronized void addAll(WalletOutputInput walletOutputInput)
    {
        this.txio.getI().addAll(walletOutputInput.txio.getI());
        this.txio.getJ().addAll(walletOutputInput.txio.getJ());
    }

    @Override
    public String toString()
    {
        return super.toString();
    }

    public synchronized Tuple<List<Tuple<RiverCoin,String>>, List<Tuple<RiverCoin,String>>> get()
    {
        return this.txio;
    }

    public synchronized void readFromFile(File file)
    {
        try
        {
            while(FileUtils.isFileBeingUsed(file.toString())) {}

            FileUtils.setFileIsUsed(file.toString());

            DataInputStream stream = new DataInputStream(new FileInputStream(file));

            importFromDataInputStream(stream, false);

            stream.close();

            FileUtils.freeFile(file.toString());
        } catch (Exception e)
        {
            //TODO: Better error and exception handling.
            e.printStackTrace();
        }
    }

    public synchronized void exportToFile(File file)
    {
        try
        {
            while(FileUtils.isFileBeingUsed(file.toString())) {}

            FileUtils.setFileIsUsed(file.toString());

            DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));

            exportToDataOutputStream(stream, false);

            stream.flush();
            stream.close();

            FileUtils.freeFile(file.toString());
        } catch (Exception e)
        {
            //TODO: Better error and exception handling.
            e.printStackTrace();
        }
    }

    public Tuple<Integer, Integer> length()
    {
        return new Tuple<>(txio.getI().size(), txio.getJ().size());
    }

    public void addAll(Tuple<List<Tuple<RiverCoin, String>>, List<Tuple<RiverCoin, String>>> io)
    {
        this.txio.getI().addAll(io.getI());
        this.txio.getJ().addAll(io.getJ());
    }

    public void importFromDataInputStream(DataInputStream stream) throws Exception
    {
        this.importFromDataInputStream(stream, false);
    }

    public void importFromDataInputStream(DataInputStream stream, boolean compressed) throws Exception
    {
        DataInputStream read = stream;

        if(compressed)
        {
            byte data[] = new byte[stream.readInt()];

            InflaterInputStream inputStream = new InflaterInputStream(stream);

            inputStream.read(data);

            inputStream.close();

            read = new DataInputStream(new ByteArrayInputStream(data));
        }

        int amount_inputs = read.readInt();

        for(int i = 0; i < amount_inputs; i ++)
        {
            byte bytes[] = new byte[RiverCoin.MAX_BYTES];
            read.read(bytes);

            addInput(new RiverCoin(new BigInteger(bytes)), read.readUTF());
        }

        int amount_outputs = read.readInt();

        for(int i = 0; i < amount_outputs; i ++)
        {
            byte bytes[] = new byte[RiverCoin.MAX_BYTES];
            read.read(bytes);

            addOutput(new RiverCoin(new BigInteger(bytes)), read.readUTF());
        }

        if(compressed)
            read.close();
    }

    public void exportToDataOutputStream(DataOutputStream stream) throws Exception
    {
        this.exportToDataOutputStream(stream, false);
    }

    public void exportToDataOutputStream(DataOutputStream stream, boolean compress) throws Exception
    {
        if(compress)
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream sos = new DataOutputStream(bos);

            sos.writeInt(txio.getI().size());

            for(Tuple<RiverCoin, String> tuple : txio.getI())
            {
                sos.write(tuple.getI().getBytes());
                sos.writeUTF(tuple.getJ());
            }

            sos.writeInt(txio.getJ().size());

            for(Tuple<RiverCoin, String> tuple : txio.getJ())
            {
                sos.write(tuple.getI().getBytes());
                sos.writeUTF(tuple.getJ());
            }

            sos.flush();
            sos.close();
            bos.flush();
            bos.close();

            byte data[] = bos.toByteArray();

            stream.writeInt(data.length);
            bos = new ByteArrayOutputStream();
            DeflaterOutputStream  dos = new DeflaterOutputStream(bos);

            dos.write(data);

            dos.flush();
            dos.close();

            bos.flush();
            bos.close();

            stream.write(bos.toByteArray());
        } else {
            stream.writeInt(txio.getI().size());

            for(Tuple<RiverCoin, String> tuple : txio.getI())
            {
                stream.write(tuple.getI().getBytes());
                stream.writeUTF(tuple.getJ());
            }

            stream.writeInt(txio.getJ().size());

            for(Tuple<RiverCoin, String> tuple : txio.getJ())
            {
                stream.write(tuple.getI().getBytes());
                stream.writeUTF(tuple.getJ());
            }
        }
    }
}