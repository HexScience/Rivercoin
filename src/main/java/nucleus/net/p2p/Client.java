package nucleus.net.p2p;

import nucleus.net.protocol.Message;

public interface Client
{
    boolean bind(Server server);
    void send(Message message);
    void close();
}
