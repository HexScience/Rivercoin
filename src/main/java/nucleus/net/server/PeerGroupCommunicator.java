package nucleus.net.server;

import nucleus.NucleusContext;
import nucleus.net.p2p.udp.PacketSendReceive;
import nucleus.net.protocol.Message;
import nucleus.system.Parameters;
import nucleus.util.Logger;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class PeerGroupCommunicator
{
    private NucleusContext      context;
    private DatagramSocket      socket;
    private List<IpAddress>     connected;
//    private UDPMessageFactory   messageFactory;

    public PeerGroupCommunicator(NucleusContext context) throws SocketException
    {
        this.context = context;
        this.socket     = PacketSendReceive.createSocket(Parameters.MAIN_NETWORK_NODE_PORT);
        broadcast();
    }


    /**
     * broadcast our presence to all known ip addresses
     */
    public void broadcast()
    {
        for (IpAddress ipAddress : context.getServerManager().getIpList().get())
        {
            try
            {
                socket.send(createPacket(socket, ipAddress.getAddress(), Parameters.MAIN_NETWORK_NODE_PORT, null));
                connected.add(ipAddress);

                if (connected.size() > 60)
                    return;
            } catch (UnknownHostException e)
            {
                Logger.err("An error occurred with the ip address: " + ipAddress + ".");
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void listen()
    {
    }

    private static DatagramPacket createPacket(DatagramSocket socket, InetAddress ip, short port, Message message)
    {
        byte data[] = message.getFullMessage();
        return new DatagramPacket(data, data.length, ip, port);
    }
}
