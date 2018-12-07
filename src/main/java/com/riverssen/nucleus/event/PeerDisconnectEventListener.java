package com.riverssen.nucleus.event;

public interface PeerDisconnectEventListener extends EventListener<PeerDisconnectEvent>
{
    default EventType getType()
    {
        return EventType.NOTIFICATION;
    }
}
