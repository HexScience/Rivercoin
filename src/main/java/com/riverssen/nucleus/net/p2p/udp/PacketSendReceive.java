package com.riverssen.nucleus.net.p2p.udp;

import com.riverssen.nucleus.net.protocol.Message;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class PacketSendReceive
{
    public static DatagramSocket createSocket(short port) throws SocketException
    {
        return new DatagramSocket(port);
    }
    public static DatagramPacket createPacket(InetAddress ip, short port, Message message)
    {
        byte data[] = message.getFullMessage();
        return new DatagramPacket(data, data.length, ip, port);
    }
}