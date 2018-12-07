package nucleus.net.server;

import nucleus.consensys.DownloadedBlock;
import nucleus.event.BlockNotificationEvent;
import nucleus.net.ServerManager;
import nucleus.net.p2p.udp.PacketSendReceive;
import nucleus.net.protocol.Message;
import nucleus.net.protocol.message.*;
import nucleus.protocols.protobufs.Block;
import nucleus.system.Context;
import nucleus.system.Parameters;
import nucleus.util.ByteUtil;
import nucleus.util.Logger;
import nucleus.util.SortedLinkedQueue;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

import static nucleus.util.HashUtil.applySha256;

public class Server
{
    private Context             context;
    private DatagramSocket      socket;
    private Set<IpAddress>      connected;
    private long                lastreconnect;

    private Queue<Message>      queue;

    public Server(Context context) throws SocketException
    {
        this.context    = context;
        this.connected  = new LinkedHashSet<>();
        this.queue      = new LinkedBlockingDeque<>();
        this.socket     = PacketSendReceive.createSocket(Parameters.MAIN_NETWORK_NODE_PORT);
        broadcast();
    }


    /**
     * broadcast our presence to all known ip addresses
     */
    private void broadcast()
    {
        int numAlready = connected.size();
        for (IpAddress ipAddress : context.getServerManager().getIpList().get())
        {
            try
            {
                connectTo(ipAddress);

                /**
                 * check that at least 120 new connections were made
                 */
                if ((connected.size() - numAlready) > 120)
                    return;
            } catch (UnknownHostException e)
            {
                Logger.err("An error occurred with the ip address: " + ipAddress + ".");
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        this.lastreconnect = System.currentTimeMillis();
    }

    private void reconnect()
    {
        for (IpAddress ipAddress : connected)
        {
            try
            {
                connectTo(ipAddress);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        lastreconnect = System.currentTimeMillis();
    }

    public void listen()
    {
        while (true)
        {
            /**
             * This reconnects to all peers, keeping the connection alive
             * and tricking the router into thinking the ports should st
             * -ay open.
             *
             * "According to Section 4.3 of RFC 4787, the UDP timeout of
             * a NAT should not be smaller than 2 minutes (120 seconds),
             * except for selected, well-known ports. In practice, however,
             * routers tend to use smaller timeouts. For example, OpenWRT
             * 14.07 uses a timeout of just 60 seconds."
             *
             * Therefore we use a 35 second delay between reconnections.
             *
             * Of course we do send block notifications every 12_000 ms
             * however notifications are by default a NO_REPLY operation
             * therefore to make sure that the connections are always
             * alive on both sides, we do a simple reconnect.
             */
            if (System.currentTimeMillis() - lastreconnect > 35_000L)
                reconnect();

            removeInactive();

            if (getNumActivePeers() < 20)
                broadcast();

            byte header[] = new byte[(int) Message.HEADER_SIZE];
            DatagramPacket packet = new DatagramPacket(header, header.length);

            try
            {
                socket.receive(packet);

                byte header_in[] = packet.getData();

                byte type = header_in[0];
                byte code = header_in[1];
                byte checksum[] = ByteUtil.trim(header_in, 2, 34);
                int  size = ByteUtil.decodei(ByteUtil.trim(header_in, 34, 38));

                if (packet.getLength() != Message.HEADER_SIZE)
                    throw new IOException("header size incompatible.");

                byte message[] = new byte[size];

                try{
                    DatagramPacket msgBody = new DatagramPacket(message, message.length);
                    socket.receive(msgBody);

                    message = msgBody.getData();

                    if (msgBody.getLength() != size)
                        throw new IOException("message body size invalid.");
                    if (!ByteUtil.equals(applySha256(applySha256(message)), checksum))
                        throw new IOException("checksum invalid.");


                    if (type == Message.NOTFY)
                    {
                        switch (code)
                        {
                            case Message.PUNCH:
                                break;
                            case Message.PING:
                                send(new PongMessage(context.getBlockChain().chainSize()), new IpAddress(msgBody.getAddress()));
                                break;
                            case Message.PONG:
                                setPeerChainSize(new IpAddress(msgBody.getAddress()), ByteUtil.decode(message));
                                break;
                            case Message.BLOCK:
                                exclude_send(new BlockNotifyMessage(message), IpAddress.pack(new IpAddress(msgBody.getAddress())));
                                BlockNotificationEvent event = new BlockNotificationEvent(System.currentTimeMillis(), new DownloadedBlock(new Block(message), new IpAddress(msgBody.getAddress())));

                                context.getEventManager().async_fire(event, "Block");
                                break;
                            case Message.BLOCKHEADER:
                            case Message.CHAIN:
                            case Message.CHAINSIZE:
                            case Message.TRANSACTION:
                            case Message.MSG_CORRUPTED:
                                send(new CorruptedMessage(message).getAnswerMessage(context, context.getServerManager()), msgBody.getAddress());
                                break;
                            case Message.PEERS:
                                removeInactive();
                                if (getNumActivePeers() > 0)
                                    send(new PeerNotificationMessage(connected));

                                Set<IpAddress> decoded = PeerNotificationMessage.decode(message);

                                for (IpAddress peer : decoded)
                                    connectTo(peer);
                                break;
                            default:
                                break;
                        }
                    } else if (type == Message.REPLY)
                    {
                        switch (code)
                        {
                            case Message.PUNCH:
                                break;
                            case Message.PING:
                            case Message.PONG:
                            case Message.BLOCK:
                                exclude_send(new BlockNotifyMessage(message), IpAddress.pack(new IpAddress(msgBody.getAddress())));
                                BlockNotificationEvent event = new BlockNotificationEvent(System.currentTimeMillis(), new DownloadedBlock(new Block(message), new IpAddress(msgBody.getAddress())));

                                context.getEventManager().async_fire(event, "Block");
                                break;
                            case Message.BLOCKHEADER:
                            case Message.CHAIN:
                            case Message.CHAINSIZE:
                            case Message.TRANSACTION:
                            case Message.MSG_CORRUPTED:
                                send(new CorruptedMessage(message).getAnswerMessage(context, context.getServerManager()), msgBody.getAddress());
                                break;
                            case Message.PEERS:
                                removeInactive();
                                if (getNumActivePeers() > 0)
                                    send(new PeerNotificationMessage(connected));

                                Set<IpAddress> decoded = PeerNotificationMessage.decode(message);

                                for (IpAddress peer : decoded)
                                    connectTo(peer);
                                break;
                            default:
                                break;
                        }
                    } else if (type == Message.OPTIN)
                    {
                    } else if (type == Message.REQUEST)
                    {
                    }
                } catch (IOException e)
                {
                    InetAddress peer = packet.getAddress();

                    send(new CorruptedMessage(checksum), peer);
                }
            } catch (IOException e)
            {
            }
        }
    }

    private void setPeerChainSize(IpAddress address, long decode)
    {
        for (IpAddress peer : connected)
            if (peer.equals(address))
                peer.setChainSize(decode);
    }

    private long getNumActivePeers()
    {
        long num = 0;

        for (IpAddress address : connected)
            num += address.isActive() ? 1 : 0;

        return num;
    }

    private void removeInactive()
    {
//        Set<IpAddress> inactive = new LinkedHashSet<>();
//
//        for (IpAddress address : connected)
//            if (!address.isActive()) inactive.add(address);
////            else if (address.isActive() && disconnected.contains(address))
////                disconnected.remove(address);
//
//        connected.removeAll(inactive);
//        disconnected.addAll(inactive);
    }

    private static DatagramPacket createPacket(InetAddress ip, short port, Message message)
    {
        byte data[] = message.getFullMessage();
        return new DatagramPacket(data, data.length, ip, port);
    }

    /**
     * @param message The message to broadcast.
     * @throws IOException An exception if the
     */
    public void send(Message message)
    {
        for (IpAddress address : connected)
            send(message, address);
    }

    /**
     * @param message The message to broadcast.
     * @param exclude An array of excluded ip addresses,
     *                they must not be null or empty.
     * @throws IOException An exception if the
     */
    public void exclude_send(Message message, IpAddress[] exclude)
    {
        if (message == null) return;

        for (IpAddress address : connected)
            if (!contains(exclude, address))
                send(message, address);
    }

    private boolean contains(IpAddress[] array, IpAddress check)
    {
        for (IpAddress address : array)
            if (address.equals(check))
                return true;

        return false;
    }

    public void connectTo(IpAddress address) throws IOException
    {
        socket.send(createPacket(address.getAddress(), Parameters.MAIN_NETWORK_NODE_PORT,  new PingMessage(context.getBlockChain().chainSize())));
        for (int i = 0; i < 6; i ++)
            send(new PingMessage(context.getBlockChain().chainSize()));
        connected.add(address);
    }

    /**
     * @param message The message to send.
     * @param peer The peer to send this message to.
     * @throws IOException An exception if the
     */
    public void send(Message message, IpAddress peer)
    {
        if (message == null) return;

        this.send(message, peer.getAddress());
    }

    public void send(Message message, InetAddress peer)
    {
        if (message == null) return;

        long sent = 0;

        try {
            socket.send(createPacket(peer, Parameters.MAIN_NETWORK_NODE_PORT, message));
            sent ++;
        } catch (Exception e)
        {
        }

        if (sent == 0)
        {
            Logger.err("message could not be sent.");
            broadcast();
        }
    }
}
