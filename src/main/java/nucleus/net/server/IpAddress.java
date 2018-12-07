package nucleus.net.server;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpAddress implements Comparable<IpAddress>
{
    private InetAddress     address;
    private double          distance;
    private long            lastReply;
    private long            chainSize;

    public IpAddress(String address) throws UnknownHostException
    {
        this.address = InetAddress.getByName(address);
    }

    public IpAddress(InetAddress address)
    {
        this.address = address;
    }

    public IpAddress(InetAddress address, long distance)
    {
        this.address = address;
        this.distance = distance;
    }

    public InetAddress getAddress()
    {
        return address;
    }

    public double getDistance()
    {
        return distance;
    }

    public void setDistance(double distance)
    {
        this.distance = distance;
    }

    public boolean isV6()
    {
        return address instanceof Inet6Address;
    }

    public CityResponse getResponse(DatabaseReader lookupService) throws IOException
    {
        try
        {
            CityResponse response = lookupService.city(address);

            return response;
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String toString()
    {
        return address.toString().substring(1);
    }

    @Override
    public int compareTo(IpAddress o)
    {
        return o.distance > distance ? -1 : 1;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof IpAddress)
            return ((IpAddress) obj).address.toString().equals(address.toString());

        return super.equals(obj);
    }

    public void setReplied()
    {
        lastReply = System.currentTimeMillis();
    }

    public boolean isActive()
    {
        return System.currentTimeMillis() - lastReply > 60_000L;
    }

    public static final IpAddress[] pack(IpAddress... addresses)
    {
        return addresses;
    }

    public void setChainSize(long decode)
    {
        this.chainSize = decode;
    }
}
