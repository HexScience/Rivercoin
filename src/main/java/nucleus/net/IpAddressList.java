package nucleus.net;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import nucleus.exceptions.FileServiceException;
import nucleus.io.FileService;
import nucleus.math.Vector2d;
import nucleus.net.server.IpAddress;
import nucleus.system.Parameters;
import nucleus.util.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class IpAddressList
{
    private Queue<IpAddress> priorityQueue;
    private FileService      entryPoint;
    private InetAddress      myAddress;
    private Vector2d         myLocale;

    public IpAddressList(FileService entryPoint, DatabaseReader dbReader) throws IOException, FileServiceException, GeoIp2Exception
    {
        BufferedReader reader   = (BufferedReader) entryPoint.newFile("data").newFile("ipdb.dfx").as(BufferedReader.class);
        this.priorityQueue      = new PriorityQueue<>();
        this.seed();
        this.entryPoint         = entryPoint;
        this.myAddress          = InetAddress.getByName(Parameters.getMyIP());
        if (this.myAddress != null)
            myLocale = new Vector2d(dbReader.city(this.myAddress).getLocation());

        String ip = "";
        IpAddress ipAddress = null;

        try
        {
            while ((ip = reader.readLine()) != null)
            {
                ipAddress = new IpAddress(ip);
                add(ipAddress, dbReader);
            }
        } catch (UnknownHostException e)
        {
        }

        reader.close();

        writeOut(true);
    }

    /**
     * This function contacts the NuC website and asks for an updated list of ip addresses.
     */
    private void seed()
    {
    }

    public void add(IpAddress address, DatabaseReader dbReader)
    {
        priorityQueue.add(calculate(address, dbReader));
    }
    public void add(InetAddress address, DatabaseReader dbReader)
    {
        priorityQueue.add(calculate(new IpAddress(address), dbReader));
    }

    public void add(String address, DatabaseReader dbReader)
    {
        IpAddress ip = null;

        try
        {
            ip = new IpAddress(address);

            priorityQueue.add(calculate(ip, dbReader));
        } catch (UnknownHostException e)
        {
        }
    }

    public IpAddress calculate(IpAddress address, DatabaseReader dbreader)
    {
        if (myAddress == null)
        {
            Logger.err("local ip not found.");
            return address;
        }

//        for (IpAddress address : priorityQueue)
//        {
            double distance = 300;
            try
            {
                Vector2d vec = new Vector2d(address.getResponse(dbreader).getLocation());
                distance = (Math.abs(myLocale.Sub(vec).Length()));
            } catch (Exception e)
            {
            }

            address.setDistance(distance);
//        }

//        Queue<IpAddress> temp = this.priorityQueue;

//        this.priorityQueue = new PriorityQueue<>();
//        for (IpAddress address : temp)
//            this.priorityQueue.add(address);
//        this.priorityQueue.addAll(temp);

        return address;
    }

    public void writeOut() throws IOException, FileServiceException
    {
        writeOut(true);
    }

    private void writeOut(boolean overwrite) throws IOException, FileServiceException
    {
        BufferedWriter writer = null;

        if (overwrite)
            writer = (BufferedWriter) entryPoint.newFile("data").newFile("ipdb.dfx").as(BufferedWriter.class);
        else
            writer = new BufferedWriter(new FileWriter(entryPoint.newFile("data").newFile("ipdb.dfx").file(), true));
        for (IpAddress address : priorityQueue)
            writer.write(address.toString() + "\n");

        writer.flush();
        writer.close();
    }

    public List<IpAddress> get(int num)
    {
        List<IpAddress> list = new ArrayList<>();

        int index = 0;

        for (IpAddress address : priorityQueue)
        {
            index ++;
            list.add(address);
            if (index >= num)
                return list;
        }

        return list;
    }

    public Queue<IpAddress> get()
    {
        return priorityQueue;
    }
}