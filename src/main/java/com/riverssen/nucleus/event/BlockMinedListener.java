package com.riverssen.nucleus.event;

public interface BlockMinedListener extends EventListener<BlockMinedEvent>
{
    default EventType getType()
    {
        return EventType.INTERNAL_NOTIFICATION;
    }
}