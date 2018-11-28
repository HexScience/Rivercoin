package nucleus.net.p2p.udp;

import nucleus.net.p2p.Server;
import nucleus.net.protocol.Message;

public class Client implements nucleus.net.p2p.Client
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
