package nucleus.net;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import nucleus.exceptions.FileServiceException;
import nucleus.io.FileService;
import nucleus.net.server.IpAddress;
import nucleus.net.server.PeerGroupCommunicator;

import java.io.IOException;
import java.util.Queue;

public class ServerManager
{
    private MessageQueue            messageQueue;
    private RoundtripQue            roundTripQue;
    private PeerGroupCommunicator   peerGroupCommunicator;
    private DatabaseReader          lookupService;
    private IpAddressList           ipList;

    private Queue<IpAddress>        addresses;

    public ServerManager(FileService entryPoint) throws IOException, FileServiceException, GeoIp2Exception
    {
        this.messageQueue   = new MessageQueue();
        this.roundTripQue   = new RoundtripQue();
        this.lookupService  = new DatabaseReader.Builder(entryPoint.newFile("GeoLiteC").newFile("GeoLiteC.mmdb").file()).build();
        this.ipList         = new IpAddressList(entryPoint, lookupService);
    }
}