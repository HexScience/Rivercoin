package com.riverssen.net;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PongMessage
{
    public long chainSize;

    public PongMessage(Message message) throws IOException {
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(message.getData()));
        chainSize = stream.readLong();
    }
}
