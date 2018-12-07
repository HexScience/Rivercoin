package nucleus.net;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import nucleus.event.PeerDisconnectEvent;
import nucleus.event.PeerDisconnectEventListener;
import nucleus.exceptions.EventFamilyDoesNotExistException;
import nucleus.exceptions.FileServiceException;
import nucleus.system.Context;
import nucleus.util.FileService;
import nucleus.net.protocol.Message;
import nucleus.net.server.IpAddress;
import nucleus.net.server.Server;

import java.io.IOException;
import java.net.SocketException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;

public class ServerManager
{
    private MessageQueue                    messageQueue;
    private Map<byte[], MessageRoundTrip>   roundTripQue;
    private Server                          server;
    private DatabaseReader                  lookupService;
    private IpAddressList                   ipList;
    private Context                         context;
    private Queue<IpAddress>                addresses;

    public ServerManager(Context context, FileService entryPoint) throws IOException, FileServiceException, GeoIp2Exception, EventFamilyDoesNotExistException
    {
        this.context        = context;
        this.messageQueue   = new MessageQueue();
        this.roundTripQue   = new LinkedHashMap<>();
        this.lookupService  = new DatabaseReader.Builder(entryPoint.newFile("GeoLiteC").newFile("GeoLiteC.mmdb").file()).build();
        this.ipList         = new IpAddressList(entryPoint, lookupService);

        this.context.getEventManager().register((PeerDisconnectEventListener) (_PeerDisconnectEvent_)->{
            try
            {
                onPeerDisconnectEvent(_PeerDisconnectEvent_);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }, "Server");
    }

    public void launch() throws SocketException
    {
        this.server         = new Server(context);
    }

    private void onPeerDisconnectEvent(PeerDisconnectEvent event) throws IOException
    {
        server.connectTo(event.getData());
    }

    public void sendMessage(Message message)
    {
        roundTripQue.put(message.getCheckSum(), new MessageRoundTrip(message));
        server.send(message);
    }

    public void sendMessages(Message... messages)
    {
        for (Message msg : messages)
        {
            roundTripQue.put(msg.getCheckSum(), new MessageRoundTrip(msg));
            server.send(msg);
        }
    }

    public void sendMessages(IpAddress recipient, Message... messages)
    {
        for (Message msg : messages)
        {
            roundTripQue.put(msg.getCheckSum(), new MessageRoundTrip(msg));
            server.send(msg);
        }
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

    public Message getQueuedMessage(byte[] checksum)
    {
        return roundTripQue.get(checksum).getMsg();
    }
}