package nucleus.event;

import nucleus.consensys.DownloadedBlock;

public class BlockNotificationEvent extends ActionableEvent<DownloadedBlock>
{
    public BlockNotificationEvent(long time, DownloadedBlock data)
    {
        super(EventType.NOTIFICATION, time, data);
    }
}
