package nucleus.net;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import nucleus.exceptions.FileServiceException;
import nucleus.util.FileService;
import nucleus.net.protocol.Message;
import nucleus.net.server.IpAddress;
import nucleus.net.server.PeerGroupCommunicator;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;

public class ServerManager
{
    private MessageQueue                    messageQueue;
    private Map<byte[], MessageRoundTrip>   roundTripQue;
    private PeerGroupCommunicator           server;
    private DatabaseReader                  lookupService;
    private IpAddressList                   ipList;

    private Queue<IpAddress>                addresses;

    public ServerManager(FileService entryPoint) throws IOException, FileServiceException, GeoIp2Exception
    {
        this.messageQueue   = new MessageQueue();
        this.roundTripQue   = new LinkedHashMap<>();
        this.lookupService  = new DatabaseReader.Builder(entryPoint.newFile("GeoLiteC").newFile("GeoLiteC.mmdb").file()).build();
        this.ipList         = new IpAddressList(entryPoint, lookupService);
    }

    public void sendMessage(Message message)
    {
        roundTripQue.put(message.getCheckSum(), new MessageRoundTrip(message));
        server.send(message);
    }

    public IpAddressList getIpList()
    {
        return ipList;
    }

    public void request(Message request, IpAddress peer)
    {
        roundTripQue.put(request.getCheckSum(), new MessageRoundTrip(request, true));
        server.send(request, peer);
    }
}