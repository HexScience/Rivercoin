package com.riverssen.net;

import com.riverssen.core.block.BlockHeader;
import com.riverssen.core.block.FullBlock;
import com.riverssen.core.headers.ContextI;

import java.io.*;

public class Message
{
    private short                 type;
    private ByteArrayOutputStream stream;
    private DataOutputStream      outstm;
    private boolean               consumed;

    public Message()
    {
    }

    public Message(MessageType type)
    {
        short stype = 0;

        for (short s = 0; s < MessageType.values().length; s ++)
            if (MessageType.values()[s].equals(type))
            {
                stype = s;
                break;
            }

        this.type = stype;
        this.stream = new ByteArrayOutputStream();
        this.outstm = new DataOutputStream(this.stream);
    }

    public Message consume()
    {
        this.consumed = true;
        return this;
    }

    public BlockHeader getBlockHeader(ContextI context) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(getData()));
        BlockHeader b =  new BlockHeader(dataInputStream);

        return b;
    }

    public FullBlock getBlock(ContextI context) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(getData()));
        FullBlock b =  new FullBlock(dataInputStream, context);

        return b;
    }

    public boolean isConsumed()
    {
        return this.consumed;
    }

    public DataOutputStream add()
    {
        return this.outstm;
    }

    public byte[] getData() throws IOException
    {
        outstm.flush();
        outstm.close();

        return stream.toByteArray();
    }

    public void send(DataOutputStream outputStream) throws IOException
    {
        byte[] data = getData();
        outputStream.writeShort(type);
        outputStream.writeInt(data.length);
        outputStream.write(data);
        outputStream.flush();
    }

    public Message receive(DataInputStream inputStream) throws IOException
    {
        this.type = inputStream.readShort();

        if (type > MessageType.values().length || type < 0)
            throw new IOException("type mismatch");

        int length = inputStream.readInt();

        byte data[] = new byte[length];

        inputStream.read(data);

        outstm.write(data);

        return this;
    }

    public MessageType getType()
    {
        return MessageType.values()[type];
    }

    public PongMessage toPongMessage() throws IOException {
        return new PongMessage(this);
    }
}
