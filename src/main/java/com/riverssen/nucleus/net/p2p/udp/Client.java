package com.riverssen.nucleus.net.p2p.udp;

import com.riverssen.nucleus.net.p2p.Server;
import com.riverssen.nucleus.net.protocol.Message;

public class Client implements com.riverssen.nucleus.net.p2p.Client
{
    @Override
    public boolean bind(Server server)
    {
        return false;
    }

    @Override
    public void send(Message message)
    {

    }

    @Override
    public void close()
    {

    }
}
