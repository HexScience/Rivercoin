package nucleus.net;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import nucleus.exceptions.FileServiceException;
import nucleus.util.FileService;
import nucleus.net.protocol.Message;
import nucleus.net.server.IpAddress;
import nucleus.net.server.PeerGroupCommunicator;

import java.io.IOException;
import java.util.Queue;

public class ServerManager
{
    private MessageQueue            messageQueue;
    private RoundtripQue            roundTripQue;
    private PeerGroupCommunicator   server;
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

    public void sendMessage(Message message)
    {
        if (message.getType() == Message.RSPND)
            roundTripQue.push(new MessageRoundTrip(message));
    }

    public IpAddressList getIpList()
    {
        return ipList;
    }
}