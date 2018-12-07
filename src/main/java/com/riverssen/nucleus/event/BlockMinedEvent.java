package com.riverssen.nucleus.event;

import com.riverssen.nucleus.protocols.protobufs.Block;

public class BlockMinedEvent extends ActionableEvent<Block>
{
    public BlockMinedEvent(long time, Block data)
    {
        super(EventType.INTERNAL_NOTIFICATION, time, data);
    }
}