package com.riverssen.nucleus.event;

import com.riverssen.nucleus.net.server.IpAddress;

public class PeerDisconnectEvent extends ActionableEvent<IpAddress>
{
    public PeerDisconnectEvent(long time, IpAddress data)
    {
        super(EventType.NOTIFICATION, time, data);
    }
}
