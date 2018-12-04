package nucleus.event;

import nucleus.protocols.protobufs.Block;

public class BlockMinedEvent extends ActionableEvent<Block>
{
    public BlockMinedEvent(long time, Block data)
    {
        super(EventType.INTERNAL_NOTIFICATION, time, data);
    }
}