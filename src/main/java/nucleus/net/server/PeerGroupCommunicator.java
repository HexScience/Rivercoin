package nucleus.net.server;

import nucleus.net.protocol.Message;
import nucleus.net.p2p.UDPMessageFactory;
import nucleus.system.Parameters;
import nucleus.util.Logger;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class PeerGroupCommunicator
{
    private Set<String>         allKnownPossiblePeers;
    private DatagramSocket      socket;
    private UDPMessageFactory   messageFactory;

    /**
     * broadcast our presence to all known ip addresses
     */
    public void broadcast() throws SocketException
    {
        Set<String> toRemove = new HashSet<>();

        for (String ipAddress : allKnownPossiblePeers)
        {
            try
            {
                InetAddress ip = InetAddress.getByName(ipAddress);

                socket.send(createPacket(socket, ip, Parameters.MAIN_NETWORK_NODE_PORT, null));
            } catch (UnknownHostException e)
            {
                Logger.err("An error occurred with the ip address: " + ipAddress + ", (address will be removed).");
                toRemove.add(ipAddress);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        /** remove all invalid ip addresses **/
        allKnownPossiblePeers.removeAll(toRemove);
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
