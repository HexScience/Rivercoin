package com.riverssen.nucleus.net.p2p;

import com.riverssen.nucleus.NucleusContext;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface Server
{
    boolean bind(int port);

    default void listen(ServerListener listener, NucleusContext context)
    {
        listener.run(context);
    }

    public ByteBuffer receive() throws IOException;

    boolean close();

    boolean isClosed();

    public static interface ServerListener
    {
        void run(NucleusContext context);
    }
}