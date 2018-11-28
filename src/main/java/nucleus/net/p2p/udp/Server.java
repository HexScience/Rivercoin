package nucleus.net.p2p.udp;

import nucleus.protocol.protobufs.Block;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

public class Server implements nucleus.net.p2p.Server
{
    private DatagramSocket socket;

    @Override
    public boolean bind(int port)
    {
        try
        {
            socket = new DatagramSocket(port);
            return true;
        } catch (Exception e)
        {
            return false;
        }
    }

    private byte readByte() throws IOException
    {
        DatagramPacket packet = new DatagramPacket(new byte[1], 1);
        socket.receive(packet);

        return packet.getData()[0];
    }

    private short readShort() throws IOException
    {
        DatagramPacket packet = new DatagramPacket(new byte[2], 2);
        socket.receive(packet);

        return ByteBuffer.wrap(packet.getData()).getShort();
    }

    private int readInt() throws IOException
    {
        DatagramPacket packet = new DatagramPacket(new byte[4], 4);
        socket.receive(packet);

        return ByteBuffer.wrap(packet.getData()).getInt();
    }

    private long readLong() throws IOException
    {
        DatagramPacket packet = new DatagramPacket(new byte[8], 8);
        socket.receive(packet);

        return ByteBuffer.wrap(packet.getData()).getLong();
    }

    private float readFloat() throws IOException
    {
        DatagramPacket packet = new DatagramPacket(new byte[4], 4);
        socket.receive(packet);

        return ByteBuffer.wrap(packet.getData()).getFloat();
    }

    private double readDouble() throws IOException
    {
        DatagramPacket packet = new DatagramPacket(new byte[8], 8);
        socket.receive(packet);

        return ByteBuffer.wrap(packet.getData()).getDouble();
    }

    private Block readBlock(long size) throws IOException
    {
        DatagramPacket packet = new DatagramPacket(new byte[8], 8);
        socket.receive(packet);

        return new Block(new DataInputStream(new ByteArrayInputStream(packet.getData())));
    }

    @Override
    public ByteBuffer receive() throws IOException
    {
        DatagramPacket packet = new DatagramPacket(new byte[2], 2);

        socket.receive(packet);

        byte data[] = packet.getData();

//        ResponseHandler handler = ResponseHandler.construct(data);

        return null;
    }

    @Override
    public boolean close()
    {
        socket.close();
        return socket.isClosed();
    }

    @Override
    public boolean isClosed()
    {
        return socket.isClosed();
    }
}