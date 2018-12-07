package com.riverssen.nucleus.event;

import com.riverssen.nucleus.consensys.DownloadedBlock;

public class BlockNotificationEvent extends ActionableEvent<DownloadedBlock>
{
    public BlockNotificationEvent(long time, DownloadedBlock data)
    {
        super(EventType.NOTIFICATION, time, data);
    }
}
