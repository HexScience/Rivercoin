package nucleus.event;

import nucleus.protocols.protobufs.Block;

public class BlockMinedEvent extends ActionableEvent<Block>
{
    public BlockMinedEvent(EventType type, long time, Block data)
    {
        super(type, time, data);
    }
}