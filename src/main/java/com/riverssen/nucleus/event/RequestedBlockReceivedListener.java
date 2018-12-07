package com.riverssen.nucleus.event;

public interface RequestedBlockReceivedListener extends EventListener<RequestedBlockReceivedEvent>
{
    @Override
    default EventType getType()
    {
        return EventType.REPLY_TO_REQUEST;
    }
}