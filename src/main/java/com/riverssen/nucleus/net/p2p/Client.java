package com.riverssen.nucleus.net.p2p;

import com.riverssen.nucleus.net.protocol.Message;

public interface Client
{
    boolean bind(Server server);
    void send(Message message);
    void close();
}
