package nucleus.event;

import nucleus.protocols.protobufs.Block;

public class BlockNotificationEvent extends ActionableEvent<Block>
{
    public BlockNotificationEvent(long time, Block data)
    {
        super(EventType.NOTIFICATION, time, data);
    }
}
