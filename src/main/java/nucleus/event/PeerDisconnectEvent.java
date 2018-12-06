package nucleus.event;

import nucleus.net.server.IpAddress;

public class PeerDisconnectEvent extends ActionableEvent<IpAddress>
{
    public PeerDisconnectEvent(long time, IpAddress data)
    {
        super(EventType.NOTIFICATION, time, data);
    }
}
