package com.riverssen.nucleus.net.protocol.message;

import com.riverssen.nucleus.net.ServerManager;
import com.riverssen.nucleus.net.protocol.Message;
import com.riverssen.nucleus.net.protocol.NotificationMessage;
import com.riverssen.nucleus.net.server.IpAddress;
import com.riverssen.nucleus.system.Context;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;
import java.util.Set;

public class PeerNotificationMessage extends NotificationMessage
{
    public PeerNotificationMessage(Set<IpAddress> activePeers)
    {
        super(PEERS, toByteArray(activePeers));
    }

    public static Set<IpAddress> decode(byte[] message)
    {
        Set<IpAddress> set = new LinkedHashSet<>();

        String data[] = new String(message).split("\n");

        for (String line : data)
            if (line.trim().length() > 0)
            {
                try
                {
                    set.add(new IpAddress(InetAddress.getByName(line.trim())));
                } catch (UnknownHostException e)
                {
                    e.printStackTrace();
                }
            }

        return set;
    }

    @Override
    public Message getAnswerMessage(Context context, ServerManager manager)
    {
        return null;
    }

    @Override
    public String toString()
    {
        return null;
    }

    private static byte[] toByteArray(Set<IpAddress> addresses)
    {
        String peers = "";

        for (IpAddress address : addresses)
            peers += address.toString() + "\n";

        return peers.getBytes();
    }
}
